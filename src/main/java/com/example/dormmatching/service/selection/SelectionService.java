package com.example.dormmatching.service.selection;

import com.example.dormmatching.entity.application.ApplicationPeriod;
import com.example.dormmatching.entity.application.DormCapacity;
import com.example.dormmatching.entity.application.SelectionResult;
import com.example.dormmatching.entity.record.AcademicRecord;
import com.example.dormmatching.entity.record.HealthDiscipline;
import com.example.dormmatching.entity.user.User;
import com.example.dormmatching.repository.AcademicRecordRepository;
import com.example.dormmatching.repository.ApplicationPeriodRepository;
import com.example.dormmatching.repository.DormCapacityRepository;
import com.example.dormmatching.repository.HealthDisciplineRepository;
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
import java.util.stream.Collectors;

@Service
public class SelectionService {

    private final UserRepository userRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final HealthDisciplineRepository healthDisciplineRepository;
    private final UserPriorityRepository userPriorityRepository;
    private final SelectionResultRepository selectionResultRepository;
    private final ApplicationPeriodRepository applicationPeriodRepository;
    private final DormCapacityRepository dormCapacityRepository;

    public SelectionService(
            UserRepository userRepository,
            AcademicRecordRepository academicRecordRepository,
            HealthDisciplineRepository healthDisciplineRepository,
            UserPriorityRepository userPriorityRepository,
            SelectionResultRepository selectionResultRepository,
            ApplicationPeriodRepository applicationPeriodRepository,
            DormCapacityRepository dormCapacityRepository
    ) {
        this.userRepository = userRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.healthDisciplineRepository = healthDisciplineRepository;
        this.userPriorityRepository = userPriorityRepository;
        this.selectionResultRepository = selectionResultRepository;
        this.applicationPeriodRepository = applicationPeriodRepository;
        this.dormCapacityRepository = dormCapacityRepository;
    }

    @Transactional
    public SelectionSummary runStudentSelection(Integer periodId) {
        // 0) 이전 결과 삭제 (중복 INSERT 방지)
        selectionResultRepository.deleteByPeriodPeriodId(periodId);

        // 1) 기간 정보 조회 및 마감 여부 확인
        ApplicationPeriod period = applicationPeriodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("ApplicationPeriod not found: " + periodId));
        if (LocalDate.now().isBefore(period.getEndDate())) {
            throw new RuntimeException("아직 신청 기간이 마감되지 않았습니다: " + period.getEndDate());
        }

        // 2) 지원자 조회
        List<User> allApplicants = userRepository.findEnrolledStudentsByPeriod(periodId);

        // 3) 성별별 분리
        List<User> maleApplicants = allApplicants.stream()
                .filter(u -> "M".equalsIgnoreCase(u.getGender()))
                .collect(Collectors.toList());
        List<User> femaleApplicants = allApplicants.stream()
                .filter(u -> "F".equalsIgnoreCase(u.getGender()))
                .collect(Collectors.toList());

        // 4) quota 조회
        List<DormCapacity> caps = dormCapacityRepository.findByPeriodPeriodId(periodId);
        int maleQuota = caps.stream()
                .filter(c -> "M".equalsIgnoreCase(c.getGender()))
                .findFirst()
                .map(DormCapacity::getCapacity)
                .orElse(0);
        int femaleQuota = caps.stream()
                .filter(c -> "F".equalsIgnoreCase(c.getGender()))
                .findFirst()
                .map(DormCapacity::getCapacity)
                .orElse(0);

        // 5) 선발 로직
        List<SelectionResult> finalResults = new ArrayList<>();
        finalResults.addAll(selectByGenderPeriodAndQuota(maleApplicants, periodId, maleQuota));
        finalResults.addAll(selectByGenderPeriodAndQuota(femaleApplicants, periodId, femaleQuota));

        // 6) 저장
        selectionResultRepository.saveAll(finalResults);

        // 7) 요약 생성
        SelectionSummary summary = new SelectionSummary();
        summary.setPeriodId(periodId);
        summary.setSelectedCount((int) finalResults.stream().filter(SelectionResult::getSelected).count());
        summary.setMessage("총 " + allApplicants.size() + "명 지원 중 " +
                summary.getSelectedCount() + "명 선발 완료");
        return summary;
    }

    /**
     * 성별 지원자 리스트를 받아, ① 우선선발 대상 → ② 학년별 비율 배분 → ③ 남은 인원이 quota 미달 시 후보군에서 채우기
     * 최종 SelectionResult 목록(Selected=true/false 포함)을 반환합니다.
     *
     * @param applicants 해당 성별 지원자(User) 리스트
     * @param periodId   지원 기간 ID
     * @param quota      해당 성별 정원 수
     * @return 성별에 대한 SelectionResult 목록
     */
    private List<SelectionResult> selectByGenderPeriodAndQuota(
            List<User> applicants,
            Integer periodId,
            int quota
    ) {
        // scoredList  = 총점(175 이상) 지원자
        // candidateList = 총점(175 미만) 지원자 (후보군)
        List<StudentScore> scoredList    = new ArrayList<>();
        List<StudentScore> candidateList = new ArrayList<>();
        List<SelectionResult> results    = new ArrayList<>();

        // 1) 지원자가 없거나 정원이 0이면 → 지원자 전원 불합격 처리
        if (applicants.isEmpty() || quota <= 0) {
            int rank = 1;
            for (User u : applicants) {
                StudentScore sc = calculateScoreForStudent(u);
                SelectionResult r = buildSelectionResult(sc, periodId, rank++);
                r.setSelected(false);
                results.add(r);
            }
            return results;
        }

        // 2) 지원자별 점수 계산 → 총점에 따라 scoredList 또는 candidateList로 나눔
        for (User u : applicants) {
            StudentScore sc = calculateScoreForStudent(u);

            // ① 재학생/편입생: totalScore = GPA×100 − 벌점×10 − 거주학기×5 + 거리점수
            // ② 신입생: totalScore = admissionScore
            if (sc.getTotalScore() >= 175.0) {
                // 총점 ≥ 175 → 우선선발·학년별 분배 풀에 남김
                scoredList.add(sc);
            } else {
                // 총점 < 175 → 후보(candidates) 풀에 보류
                candidateList.add(sc);
            }
        }

        // Debug: scoredList, candidateList 크기 확인
        System.out.println(">>> Debug: scoredList.size()=" + scoredList.size() +
                ", candidateList.size()=" + candidateList.size());

        // 3) scoredList(총점 ≥ 175) 중 우선선발 대상(SC.isPriority==true)과 비우선선발 대상 분리
        List<StudentScore> priorityList    = scoredList.stream()
                .filter(StudentScore::isPriority)
                .collect(Collectors.toList());
        List<StudentScore> nonPriorityList = scoredList.stream()
                .filter(sc -> !sc.isPriority())
                .collect(Collectors.toList());

        // 4) 우선선발 대상 정렬 → quota만큼 먼저 뽑기
        priorityList.sort(StudentScore::compareByGeneralTieBreaker);
        List<StudentScore> selectedPriority;
        List<StudentScore> leftoverAfterPriority;
        if (priorityList.size() <= quota) {
            selectedPriority       = new ArrayList<>(priorityList);
            leftoverAfterPriority = new ArrayList<>(nonPriorityList);
        } else {
            selectedPriority       = priorityList.subList(0, quota);
            leftoverAfterPriority = new ArrayList<>(priorityList.subList(quota, priorityList.size()));
            leftoverAfterPriority.addAll(nonPriorityList);
        }

        // 5) 우선선발 결과 → results에 추가 (selected=true)
        int rank = 1;
        for (StudentScore ps : selectedPriority) {
            SelectionResult r = buildSelectionResult(ps, periodId, rank++);
            r.setSelected(true);
            results.add(r);
        }

        // 6) 남은 정원 수 계산 (quota - selectedPriority.size())
        int remainingQuota = quota - selectedPriority.size();
        if (remainingQuota <= 0) {
            // 남은 quota가 없으면, leftoverAfterPriority와 candidateList 전원 불합격 처리
            for (StudentScore sc : leftoverAfterPriority) {
                SelectionResult r = buildSelectionResult(sc, periodId, rank++);
                r.setSelected(false);
                results.add(r);
            }
            for (StudentScore sc : candidateList) {
                SelectionResult r = buildSelectionResult(sc, periodId, rank++);
                r.setSelected(false);
                results.add(r);
            }
            return results;
        }

        // 7) leftoverAfterPriority(총점 ≥ 175이지만 우선선발·우선순위 탈락)에서 “학년별 비율(60:20:15:5)”로 배분
        Map<Integer, List<StudentScore>> byGrade = new HashMap<>();
        for (StudentScore sc : leftoverAfterPriority) {
            byGrade.computeIfAbsent(sc.getGrade(), g -> new ArrayList<>()).add(sc);
        }

        // 학년별 비율(%)
        Map<Integer, Integer> gradeRatio = Map.of(
                1, 60,
                2, 20,
                3, 15,
                4, 5
        );
        // floor 계산을 위한 초기 배정
        Map<Integer, Integer> initialAlloc = new HashMap<>();
        int totalAllocatedFloor = 0;
        for (int grade = 1; grade <= 4; grade++) {
            int ratio = gradeRatio.get(grade);
            int cnt   = (remainingQuota * ratio) / 100; // floor
            // “해당 학년에 지원자가 아예 없으면 0”
            if (byGrade.getOrDefault(grade, Collections.emptyList()).isEmpty()) {
                cnt = 0;
            }
            initialAlloc.put(grade, cnt);
            totalAllocatedFloor += cnt;
        }

        // 8) floor 연산 결과로 인해 발생한 “wastedSeats”(빈 학년 할당분)를 모으기
        int wastedSeats = 0;
        for (int grade = 1; grade <= 4; grade++) {
            if (byGrade.getOrDefault(grade, Collections.emptyList()).isEmpty()) {
                wastedSeats += initialAlloc.getOrDefault(grade, 0);
            }
        }

        // 9) 나머지(floor 후 남은) 비율 합
        int remainingRatioSum = 0;
        for (int grade = 1; grade <= 4; grade++) {
            if (!byGrade.getOrDefault(grade, Collections.emptyList()).isEmpty()) {
                remainingRatioSum += gradeRatio.get(grade);
            }
        }

        // 10) wastedSeats를 “지원자가 있는 학년”에 비율대로 다시 floor 배분
        Map<Integer, Integer> redistributed = new HashMap<>();
        int redistributedTotalFloor = 0;
        for (int grade = 1; grade <= 4; grade++) {
            if (byGrade.getOrDefault(grade, Collections.emptyList()).isEmpty()) {
                redistributed.put(grade, 0);
            } else {
                int ratio = gradeRatio.get(grade);
                int add   = (wastedSeats * ratio) / remainingRatioSum; // floor
                redistributed.put(grade, add);
                redistributedTotalFloor += add;
            }
        }

        // 11) redistributed 과정에서도 발생한 남은 gap만큼, 점수 순으로 추가 배정
        int gapToFill = wastedSeats - redistributedTotalFloor;
        if (gapToFill > 0) {
            List<StudentScore> allLeftovers = leftoverAfterPriority.stream()
                    .filter(sc -> !byGrade.getOrDefault(sc.getGrade(), Collections.emptyList()).isEmpty())
                    .sorted(StudentScore::compareByGeneralTieBreaker)
                    .collect(Collectors.toList());
            int idx2 = 0;
            while (gapToFill > 0 && idx2 < allLeftovers.size()) {
                int g = allLeftovers.get(idx2++).getGrade();
                redistributed.put(g, redistributed.getOrDefault(g, 0) + 1);
                gapToFill--;
            }
        }

        // 12) 최종 학년별 할당량(finalAlloc) 계산 = initialAlloc + redistributed
        Map<Integer, Integer> finalAlloc = new HashMap<>();
        for (int grade = 1; grade <= 4; grade++) {
            int base  = initialAlloc.getOrDefault(grade, 0);
            int extra = redistributed.getOrDefault(grade, 0);
            finalAlloc.put(grade, base + extra);
        }

        // 13) leftoverAfterPriority(총점 ≥ 175 후보군)에서 학년별로 finalAlloc만큼 selected=true, 나머지는 false
        for (int grade = 1; grade <= 4; grade++) {
            List<StudentScore> group = byGrade.getOrDefault(grade, Collections.emptyList());
            group.sort(StudentScore::compareByGeneralTieBreaker);
            int allocateCount = finalAlloc.getOrDefault(grade, 0);
            int idx3 = 0;
            for (StudentScore sc : group) {
                SelectionResult r = buildSelectionResult(sc, periodId, -1);
                if (idx3 < allocateCount) {
                    r.setSelected(true);
                } else {
                    r.setSelected(false);
                }
                results.add(r);
                idx3++;
            }
        }

        // 14) **③ 후보군(candidateList)에서 quota 미달만큼 채우기**
        //    지금까지 results에 들어간 “selected=true” 인원 수 계산
        long alreadySelectedCount = results.stream().filter(SelectionResult::getSelected).count();
        //    quota - alreadySelectedCount 만큼이 남았는지 확인
        int stillToFill = quota - (int) alreadySelectedCount;
        if (stillToFill > 0) {
            // 후보군 전체를 총점 순(desc)으로 정렬 → 상위 stillToFill명만 “selected=true”로 변경
            candidateList.sort(StudentScore::compareByGeneralTieBreaker);
            int idx4 = 0;
            for (StudentScore sc : candidateList) {
                if (idx4 >= stillToFill) {
                    // 후보군 중 quota 다 채웠으면 나머지는 모두 불합격
                    SelectionResult r = buildSelectionResult(sc, periodId, -1);
                    r.setSelected(false);
                    results.add(r);
                    continue;
                }
                // stillToFill가 남아 있으므로, 해당 후보를 selected=true로 채움
                SelectionResult r = buildSelectionResult(sc, periodId, -1);
                r.setSelected(true);
                results.add(r);
                idx4++;
            }
        } else {
            // quota를 이미 다 채웠으면 후보군 전원 불합격 처리
            for (StudentScore sc : candidateList) {
                SelectionResult r = buildSelectionResult(sc, periodId, -1);
                r.setSelected(false);
                results.add(r);
            }
        }

        // 15) 최종 순위(rank) 부여를 위해 all SelectionResult(합격+불합격) 리스트를 totalScore 기준으로 정렬 후 rank 세팅
        finalResultsRankAssignment(results);

        return results;
    }

    /**
     * SelectionResult 리스트를 “총점(SelectionScore) 기준 내림차순”으로 정렬한 뒤, 순위를 1부터 매깁니다.
     */
    private void finalResultsRankAssignment(List<SelectionResult> results) {
        results.sort(Comparator.comparingDouble(SelectionResult::getSelectionScore).reversed());
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setSelectionRank(i + 1);
        }
    }

    /**
     * 지원자 1명(User)의 “총점”을 계산하여 StudentScore 객체로 반환합니다.
     *  - status_id == 2 (신입생)은 admissionScore
     *  - status_id != 2 (재학생/편입생/대학원생)은 GPA, 벌점, 기입사경력, 거리점수 계산
     */
    private StudentScore calculateScoreForStudent(User u) {
        StudentScore sc = new StudentScore();
        sc.setUserId(u.getUserId());
        sc.setGender(u.getGender());
        sc.setGrade(u.getGrade());
        sc.setDepartmentId(u.getDepartment().getDepartmentId().longValue());

        // 1) AcademicRecord 조회 (신입생/재학생 공통 사용)
        AcademicRecord ar = academicRecordRepository.findById(u.getUserId()).orElse(null);

        // 2) status 확인 (신입생=2, 그 외 재학생=1, 편입=3, 대학원=4 등)
        Integer statusId = (u.getStatus() != null) ? u.getStatus().getStatusId() : null;
        boolean isNewStudent = (statusId != null && statusId == 2);

        if (isNewStudent) {
            // ──────────── 신입생 로직 (admissionScore 기준) ────────────
            int admissionScore = (ar == null || ar.getAdmissionScore() == null) ? 0 : ar.getAdmissionScore();
            sc.setAdmissionScore(admissionScore);
            sc.setResidencySemesters(0);
            sc.setPenaltyPoints(0);
            sc.setDistanceScore(u.getRegion() == null ? 0 : u.getRegion().getDistanceScore());
            sc.setPriority(!userPriorityRepository.findByUserIdAndProofSubmittedTrue(u.getUserId()).isEmpty());
            sc.setBirthDate(u.getBirthDate());

            // 신입생 총점 = admissionScore
            sc.setTotalScore((double) admissionScore);

        } else {
            // ──────────── 재학생/편입생/대학원생 로직 ────────────
            double academicScore = 0.0;
            if (ar != null && ar.getPreviousSemesterGpa() != null) {
                academicScore = ar.getPreviousSemesterGpa() * 100.0;
            }
            sc.setResidencySemesters(ar == null || ar.getResidencySemesters() == null
                    ? 0 : ar.getResidencySemesters());
            sc.setAdmissionScore(ar == null || ar.getAdmissionScore() == null
                    ? 0 : ar.getAdmissionScore());

            HealthDiscipline hd = healthDisciplineRepository.findById(u.getUserId()).orElse(null);
            int penalty = (hd == null || hd.getPenaltyPoints() == null) ? 0 : hd.getPenaltyPoints();
            sc.setPenaltyPoints(penalty);

            int distScore = u.getRegion() == null ? 0 : u.getRegion().getDistanceScore();
            sc.setDistanceScore(distScore);

            boolean isPriority = !userPriorityRepository
                    .findByUserIdAndProofSubmittedTrue(u.getUserId())
                    .isEmpty();
            sc.setPriority(isPriority);

            sc.setBirthDate(u.getBirthDate());

            // 재학생 총점 = GPA×100 − (penalty×10) − (residencySemesters×5) + distanceScore
            double penaltyDeduction = penalty * 10.0;
            double historyDeduction = sc.getResidencySemesters() * 5.0;
            sc.setTotalScore(academicScore - penaltyDeduction - historyDeduction + distScore);
        }

        return sc;
    }

    /**
     * StudentScore를 바탕으로 SelectionResult 객체를 생성합니다.
     *
     * @param sc       학생 점수 정보
     * @param periodId 지원 기간 ID
     * @param rank     임시 순위(나중에 final rank 재계산)
     * @return SelectionResult (selected는 기본 false, 나중에 true로 바꾸기도 함)
     */
    private SelectionResult buildSelectionResult(StudentScore sc, Integer periodId, int rank) {
        SelectionResult r = new SelectionResult();
        // user_id (FK)
        r.setUserId(sc.getUserId());
        // period_id (FK)
        r.setPeriodId(periodId);
        // selection_score, selection_rank, selected 기본값(false), notified_at
        r.setSelectionScore(sc.getTotalScore());
        r.setSelectionRank(rank);
        r.setSelected(false);
        r.setNotifiedAt(LocalDateTime.now());
        return r;
    }

    @Setter @Getter
    public static class StudentScore {
        private Long userId;
        private int grade;
        private Long departmentId;
        private String gender;
        private boolean isPriority;
        private double totalScore;
        private int distanceScore;
        private int penaltyPoints;
        private int residencySemesters;
        private int admissionScore;
        private LocalDate birthDate;

        /**
         * 총점 기준 내림차순, 동점자 tie-breaker: 거리점수 ↓, 벌점 ↑, 재거주학기 ↑, 생일(나이↑)
         */
        public static int compareByGeneralTieBreaker(StudentScore a, StudentScore b) {
            int cmp = Double.compare(b.totalScore, a.totalScore);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(b.distanceScore, a.distanceScore);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(a.penaltyPoints, b.penaltyPoints);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(a.residencySemesters, b.residencySemesters);
            if (cmp != 0) return cmp;
            if (a.birthDate != null && b.birthDate != null) {
                return b.birthDate.compareTo(a.birthDate);
            }
            return 0;
        }
    }

    @Setter @Getter
    public static class SelectionSummary {
        private Integer periodId;
        private int selectedCount;
        private String message;
    }
}
