package com.example.dormmatching.service.selection;

import com.example.dormmatching.entity.application.ApplicationPeriod;
import com.example.dormmatching.entity.application.SelectionResult;
import com.example.dormmatching.entity.record.AcademicRecord;
import com.example.dormmatching.entity.record.HealthDiscipline;
import com.example.dormmatching.entity.user.User;
import com.example.dormmatching.repository.AcademicRecordRepository;
import com.example.dormmatching.repository.ApplicationPeriodRepository;
import com.example.dormmatching.repository.HealthDisciplineRepository;
import com.example.dormmatching.repository.RegionRepository;
import com.example.dormmatching.repository.SelectionResultRepository;
import com.example.dormmatching.repository.UserPriorityRepository;
import com.example.dormmatching.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SelectionService {

    private final UserRepository userRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final HealthDisciplineRepository healthDisciplineRepository;
    private final UserPriorityRepository userPriorityRepository;
    private final RegionRepository regionRepository;
    private final SelectionResultRepository selectionResultRepository;
    private final ApplicationPeriodRepository applicationPeriodRepository;

    public SelectionService(
            UserRepository userRepository,
            AcademicRecordRepository academicRecordRepository,
            HealthDisciplineRepository healthDisciplineRepository,
            UserPriorityRepository userPriorityRepository,
            RegionRepository regionRepository,
            SelectionResultRepository selectionResultRepository,
            ApplicationPeriodRepository applicationPeriodRepository
    ) {
        this.userRepository = userRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.healthDisciplineRepository = healthDisciplineRepository;
        this.userPriorityRepository = userPriorityRepository;
        this.regionRepository = regionRepository;
        this.selectionResultRepository = selectionResultRepository;
        this.applicationPeriodRepository = applicationPeriodRepository;
    }

    /**
     * 전체 선발 로직을 실행합니다.
     * @param periodId ApplicationPeriod의 식별자(ID)
     * @return SelectionSummary (선발 결과 요약)
     */
    @Transactional
    public SelectionSummary runStudentSelection(Integer periodId) {
        // 1) 신청 기간 조회 및 유효성 검사
        ApplicationPeriod period = applicationPeriodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("ApplicationPeriod not found: " + periodId));

        // 신청 마감일 전이라면 예외
        LocalDate today = LocalDate.now();
        if (today.isBefore(period.getEndDate())) {
            throw new IllegalArgumentException("아직 신청 기간이 마감되지 않았습니다: " + period.getEndDate());
        }

        // 2) 재학생 신청자 목록 조회 (statusId = 1 으로 가정)
        List<User> studentApplicants = userRepository.findEnrolledStudentsByPeriod(periodId);

        // 3) 점수 계산 및 필터링 (총점 ≥ 175 이상만 선발 대상)
        List<StudentScore> allScores = new ArrayList<>();
        for (User u : studentApplicants) {
            StudentScore sc = calculateScoreForStudent(u);
            if (sc.getTotalScore() >= 175.0) {
                allScores.add(sc);
            }
        }

        // 4) 우선선발 대상자 / 일반 지원자 분리
        List<StudentScore> priorityList = new ArrayList<>();
        List<StudentScore> generalList = new ArrayList<>();
        for (StudentScore sc : allScores) {
            if (sc.isPriority()) priorityList.add(sc);
            else generalList.add(sc);
        }

        // 5) 학년별 배정 인원 계산 (예시: totalCapacity = 200)
        // 실제 용량(totalCapacity)은 상황에 맞게 바꾸세요.
        Map<Integer, Integer> quotaByGrade = calculateQuotaByGrade(200);

        // 6) 우선선발 및 일반 선발
        List<SelectionResult> resultsToSave = new ArrayList<>();
        for (int grade = 1; grade <= 4; grade++) {
            // (1) 우선선발자 중 해당 학년 필터 & 정렬
            int finalGrade = grade;
            List<StudentScore> priorOfGrade = priorityList.stream()
                    .filter(sc -> sc.getGrade() == finalGrade)
                    .sorted(StudentScore::compareByPriorityTieBreaker)
                    .toList();

            int quota = quotaByGrade.getOrDefault(grade, 0);
            int selectedCount = Math.min(quota, priorOfGrade.size());

            // (2) 우선선발 대상자 선발
            for (int i = 0; i < selectedCount; i++) {
                resultsToSave.add(buildSelectionResult(priorOfGrade.get(i), periodId, i + 1));
            }

            // (3) 남은 인원만큼 일반 지원자 중 해당 학년 선발
            int remainingQuota = quota - selectedCount;
            if (remainingQuota > 0) {
                int finalGradeForGeneral = grade;
                List<StudentScore> generalOfGrade = generalList.stream()
                        .filter(sc -> sc.getGrade() == finalGradeForGeneral)
                        .sorted(StudentScore::compareByGeneralTieBreaker)
                        .toList();

                for (int i = 0; i < Math.min(remainingQuota, generalOfGrade.size()); i++) {
                    int rank = selectedCount + i + 1;
                    resultsToSave.add(buildSelectionResult(generalOfGrade.get(i), periodId, rank));
                }
            }
        }

        // 7) DB에 일괄 저장
        selectionResultRepository.saveAll(resultsToSave);

        // 8) 결과 요약 반환
        SelectionSummary summary = new SelectionSummary();
        summary.setPeriodId(periodId);
        summary.setSelectedCount(resultsToSave.size());
        summary.setMessage("총 " + allScores.size() + "명 지원 중 " +
                resultsToSave.size() + "명 선발 완료");
        return summary;
    }

    /**
     * 개별 학생 점수 계산 메서드
     */
    private StudentScore calculateScoreForStudent(User u) {
        StudentScore sc = new StudentScore();
        sc.setUserId(u.getUserId());
        sc.setGender(u.getGender());
        sc.setGrade(u.getGrade());               // User 엔티티에 grade 필드가 있다고 가정
        sc.setDepartmentId(u.getDepartment().getDepartmentId().longValue());

        // (1) 학업성적 계산: 직전학기 GPA × 100
        AcademicRecord ar = academicRecordRepository.findById(u.getUserId())
                .orElseThrow(() -> new RuntimeException("AcademicRecord not found for user " + u.getUserId()));
        double academicScore = ar.getPreviousSemesterGpa() * 100.0;
        sc.setResidencySemesters(ar.getResidencySemesters());

        // (2) 벌점 정보
        HealthDiscipline hd = healthDisciplineRepository.findById(u.getUserId())
                .orElseThrow(() -> new RuntimeException("HealthDiscipline not found for user " + u.getUserId()));
        sc.setPenaltyPoints(hd.getPenaltyPoints());

        // (3) 거리 점수
        int distScore = u.getRegion().getDistanceScore();
        sc.setDistanceScore(distScore);

        // (4) 우선선발 여부: UserPriority 테이블에서 proofSubmitted IS NOT NULL 레코드 존재 여부
        boolean isPriority = !userPriorityRepository
                .findByUserIdAndProofSubmittedIsNotNull(u.getUserId())
                .isEmpty();
        sc.setPriority(isPriority);

        sc.setBirthDate(u.getBirthDate());

        // (5) 최종 총점 계산
        //      학점점수 – (벌점 × 10) – (입사경력학기수 × 5) + 거리점수
        double totalScore = academicScore
                - (hd.getPenaltyPoints() * 10.0)
                - (ar.getResidencySemesters() * 5.0)
                + distScore;
        sc.setTotalScore(totalScore);

        return sc;
    }

    /**
     * 학년별 배정 인원 계산 (전체 수용인원 totalCapacity 기준으로 비율 분배)
     *  예시 비율: 1학년 60%, 2학년 20%, 3학년 15%, 4학년 5%
     */
    private Map<Integer, Integer> calculateQuotaByGrade(int totalCapacity) {
        Map<Integer, Integer> quota = new HashMap<>();
        quota.put(1, (int) Math.floor(totalCapacity * 0.60));
        quota.put(2, (int) Math.floor(totalCapacity * 0.20));
        quota.put(3, (int) Math.floor(totalCapacity * 0.15));
        quota.put(4, (int) Math.floor(totalCapacity * 0.05));
        return quota;
    }

    /**
     * SelectionResult 엔티티를 생성하는 헬퍼 메서드
     */
    private SelectionResult buildSelectionResult(StudentScore sc, Integer periodId, int rank) {
        SelectionResult r = new SelectionResult();
        r.setUserId(sc.getUserId());
        // ApplicationPeriod는 엔티티로 저장하므로, periodId(Long) → period 엔티티로 매핑
        ApplicationPeriod ap = new ApplicationPeriod();
        ap.setPeriodId(periodId);
        r.setPeriod(ap);

        r.setSelectionScore(sc.getTotalScore());
        r.setSelectionRank(rank);
        r.setSelected(true);
        r.setNotifiedAt(LocalDateTime.now());
        return r;
    }

    // ==============================================
    // inner class: StudentScore
    // ==============================================
    @Setter
    @Getter
    public static class StudentScore {
        private Long userId;
        private int grade;               // 학년
        private Long departmentId;
        private String gender;
        private boolean isPriority;
        private double totalScore;
        private int distanceScore;
        private int penaltyPoints;
        private int residencySemesters;
        private LocalDate birthDate;

        /**
         * 우선선발자 동점자 처리 비교 함수
         */
        public static int compareByPriorityTieBreaker(StudentScore a, StudentScore b) {
            int cmp = Double.compare(b.totalScore, a.totalScore);
            if (cmp != 0) return cmp;

            cmp = Integer.compare(b.distanceScore, a.distanceScore);
            if (cmp != 0) return cmp;

            boolean aHasPenalty = a.penaltyPoints > 0;
            boolean bHasPenalty = b.penaltyPoints > 0;
            if (aHasPenalty != bHasPenalty) {
                return aHasPenalty ? 1 : -1;
            }

            boolean aHasCareer = a.residencySemesters > 0;
            boolean bHasCareer = b.residencySemesters > 0;
            if (aHasCareer != bHasCareer) {
                return aHasCareer ? 1 : -1;
            }

            if (a.birthDate != null && b.birthDate != null) {
                return b.birthDate.compareTo(a.birthDate);
            }
            return 0;
        }

        /**
         * 일반지원자 동점자 처리 비교 함수
         */
        public static int compareByGeneralTieBreaker(StudentScore a, StudentScore b) {
            int cmp = Double.compare(b.totalScore, a.totalScore);
            if (cmp != 0) return cmp;

            cmp = Integer.compare(b.distanceScore, a.distanceScore);
            if (cmp != 0) return cmp;

            if (a.birthDate != null && b.birthDate != null) {
                return b.birthDate.compareTo(a.birthDate);
            }
            return 0;
        }
    }

    // ==============================================
    // inner class: SelectionSummary
    // ==============================================
    @Setter
    @Getter
    public static class SelectionSummary {
        private Integer periodId;
        private int selectedCount;
        private String message;
    }
}
