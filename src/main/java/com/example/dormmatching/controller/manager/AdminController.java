package com.example.dormmatching.controller.manager;

import com.example.dormmatching.dto.*;
import com.example.dormmatching.dto.Response.ErrorResponse;
import com.example.dormmatching.dto.Response.SuccessResponse;
import com.example.dormmatching.entity.application.ApplicationPeriod;
import com.example.dormmatching.entity.application.DormApplication;
import com.example.dormmatching.entity.application.DormCapacity;
import com.example.dormmatching.entity.application.SelectionResult;
import com.example.dormmatching.entity.user.User;
import com.example.dormmatching.repository.ApplicationPeriodRepository;
import com.example.dormmatching.repository.SelectionResultRepository;
import com.example.dormmatching.repository.UserRepository;
import com.example.dormmatching.service.auth.AuthService;
import com.example.dormmatching.service.DormApplicationService;
import com.example.dormmatching.service.DormCapacityService;
import com.example.dormmatching.service.selection.SelectionService;
import com.example.dormmatching.service.selection.SelectionService.SelectionSummary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final DormCapacityService capacityService;
    private final DormApplicationService applicationService;
    private final SelectionService selectionService;
    private final UserRepository userRepository;
    private final ApplicationPeriodRepository periodRepository;
    private final SelectionResultRepository selectionResultRepository;

    // ───────────────────────────────────────────────────────────────────────────
    // 1) 관리자용: 사용자 생성 (기존 기능 그대로)
    // ───────────────────────────────────────────────────────────────────────────
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest req) {
        try {
            authService.register(req);
            SuccessResponse<Void> body = new SuccessResponse<>(
                    HttpStatus.CREATED.value(),
                    true,
                    "사용자 생성이 완료되었습니다.",
                    null
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 1b) 사용자 목록 조회
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        try {
            Pageable pg = PageRequest.of(page - 1, 25, Sort.by("userId").ascending());
            Page<User> userPage = userRepository.findAllDistinct(pg);

            List<RegisterRequest> dtoList = userPage.getContent().stream().map(u -> {
                RegisterRequest dto = new RegisterRequest();
                dto.setStudentNumber(u.getStudentNumber());
                dto.setName(u.getName());
                dto.setStatusId(u.getStatus() != null ? u.getStatus().getStatusId() : null);
                dto.setRoleId(u.getRole() != null ? u.getRole().getRoleId() : null);
                dto.setBirthDate(u.getBirthDate());
                dto.setGender(u.getGender());
                dto.setAddress(u.getAddress());
                dto.setInternational(u.getInternational());
                dto.setPhoneNumber(u.getPhoneNumber());
                dto.setDepartmentId(u.getDepartment() != null ? u.getDepartment().getDepartmentId() : null);
                dto.setGrade(u.getGrade());
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(
                    new SuccessResponse<>(
                            HttpStatus.OK.value(),
                            true,
                            "사용자 목록 조회 성공",
                            dtoList
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "사용자 목록 조회 중 오류가 발생했습니다."
                    ));
        }
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 2) 관리자용: 기숙사 정원 설정 / 조회 / 삭제
    //    → 기존 DormCapacityController의 내용을 모두 이동
    // ───────────────────────────────────────────────────────────────────────────

    /**
     * 기숙사 정원 생성 또는 업데이트
     * (요청 바디 예: { "periodId": 1, "gender": "M", "capacity": 960 } )
     */
    @PostMapping("/capacities")
    public ResponseEntity<?> upsertCapacity(@Valid @RequestBody DormCapacityRequest request) {
        try {
            DormCapacity saved = capacityService.createOrUpdateCapacity(request);
            SuccessResponse<DormCapacity> body = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    "정원이 저장되었습니다.",
                    saved
            );
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /** 해당 신청 기간에 설정된 모든 정원 정보 조회 */
    @GetMapping("/capacities/period/{periodId}")
    public ResponseEntity<?> getCapacitiesByPeriod(
            @PathVariable Integer periodId,
            @RequestParam(name="page", defaultValue="1") int page
    ) {
        try {
            Pageable pg = PageRequest.of(page - 1, 25, Sort.by("capacityId").ascending());
            Page<DormCapacity> capPage = capacityService.getCapacitiesByPeriod(periodId, pg);
            SuccessResponse<Page<DormCapacity>> body = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    "정원 조회가 완료되었습니다.",
                    capPage
            );
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /** 특정 capacityId 정원 삭제 */
    @DeleteMapping("/capacities/{capacityId}")
    public ResponseEntity<?> deleteCapacity(@PathVariable Long capacityId) {
        try {
            capacityService.deleteCapacity(capacityId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 3) 관리자용: 기숙사 신청 자료(예: 검증용) 조회/삭제 등 (추가로 구현 가능)
    //    여기서는 “신청 강제 취소” 하나만 예시로 띄워둡니다.
    //    (원래는 학생이 직접 신청 → 관리자는 조회하거나 강제 취소만 하도록 구현할 수 있습니다.)
    // ───────────────────────────────────────────────────────────────────────────

    // 3) 신청 기간 목록 조회
    @GetMapping("/periods")
    public ResponseEntity<?> getPeriods(
            @RequestParam(name="page", defaultValue="1") int page
    ) {
        try {
            Pageable pg = PageRequest.of(page - 1, 25, Sort.by("periodId").descending());
            Page<ApplicationPeriod> perPage = periodRepository.findAll(pg);
            List<ApplicationPeriodResponse> dtoList = perPage.stream()
                    .map(p -> ApplicationPeriodResponse.builder()
                            .periodId(p.getPeriodId().longValue())
                            .name(p.getName())
                            .startDate(p.getStartDate())
                            .endDate(p.getEndDate())
                            .build())
                    .collect(Collectors.toList());
            SuccessResponse<List<ApplicationPeriodResponse>> body = new SuccessResponse<>(
                    HttpStatus.OK.value(), true,
                    "신청 기간 목록 조회 성공", dtoList
            );
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
                    "신청 기간 조회 중 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 4) 신청 기간 생성
    @PostMapping("/periods")
    public ResponseEntity<?> createPeriod(@Valid @RequestBody ApplicationPeriodRequest req) {
        try {
            ApplicationPeriod period = new ApplicationPeriod();
            period.setName(req.getName());
            period.setStartDate(req.getStartDate());
            period.setEndDate(req.getEndDate());
            ApplicationPeriod saved = periodRepository.save(period);
            ApplicationPeriodResponse resp = ApplicationPeriodResponse.builder()
                    .periodId(saved.getPeriodId().longValue())
                    .name(saved.getName())
                    .startDate(saved.getStartDate())
                    .endDate(saved.getEndDate())
                    .build();
            SuccessResponse<ApplicationPeriodResponse> body = new SuccessResponse<>(
                    HttpStatus.CREATED.value(), true,
                    "신청 기간 생성 성공", resp
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(), false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
                    "신청 기간 생성 중 서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 5) 신청 기간 수정
    @PutMapping("/periods/{periodId}")
    public ResponseEntity<?> updatePeriod(@PathVariable Integer periodId,
                                          @Valid @RequestBody ApplicationPeriodRequest req) {
        try {
            ApplicationPeriod period = periodRepository.findById(periodId)
                    .orElseThrow(() -> new IllegalArgumentException("ApplicationPeriod not found: " + periodId));
            period.setName(req.getName());
            period.setStartDate(req.getStartDate());
            period.setEndDate(req.getEndDate());
            ApplicationPeriod updated = periodRepository.save(period);
            ApplicationPeriodResponse resp = ApplicationPeriodResponse.builder()
                    .periodId(updated.getPeriodId().longValue())
                    .name(updated.getName())
                    .startDate(updated.getStartDate())
                    .endDate(updated.getEndDate())
                    .build();
            SuccessResponse<ApplicationPeriodResponse> body = new SuccessResponse<>(
                    HttpStatus.OK.value(), true,
                    "신청 기간 수정 성공", resp
            );
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(), false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
                    "신청 기간 수정 중 서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 6) 신청 기간 삭제
    @DeleteMapping("/periods/{periodId}")
    public ResponseEntity<?> deletePeriod(@PathVariable Integer periodId) {
        try {
            periodRepository.deleteById(periodId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
                    "신청 기간 삭제 중 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 7) 신청 목록 조회
    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(
            @RequestParam Integer periodId,
            @RequestParam(name="page", defaultValue="1") int page
    ) {
        try {
            Pageable pg = PageRequest.of(page - 1, 25, Sort.by("appliedAt").descending());
            Page<DormApplication> appPage = applicationService.getApplicationsByPeriod(periodId, pg);
            List<Application> dtoList = appPage.stream().map(da -> {
                Application dto = new Application();
                dto.setUserId(da.getUser()     != null ? da.getUser().getUserId()     : null);
                dto.setPeriodId(da.getPeriod() != null ? da.getPeriod().getPeriodId().longValue() : null);
                dto.setAppliedAt(da.getAppliedAt() != null ? da.getAppliedAt().toString() : null);
                dto.setStatus(Boolean.TRUE.equals(da.getProofSubmitted()) ? "제출완료" : "미제출");
                return dto;
            }).collect(Collectors.toList());
            SuccessResponse<List<Application>> body = new SuccessResponse<>(
                    HttpStatus.OK.value(), true,
                    "신청 목록 조회 성공", dtoList
            );
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false,
                    "신청 목록 조회 중 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 특정 userId, periodId에 대해 기숙사 신청 강제 취소
     * (관리자 권한으로, 누군가 신청했으나 관리자가 직접 취소해야 하는 케이스 등)
     */
    @DeleteMapping("/applications/{userId}/{periodId}")
    public ResponseEntity<?> adminCancelApplication(
            @PathVariable Long userId,
            @PathVariable Integer periodId
    ) {
        try {
            boolean ok = applicationService.cancelApplication(userId, periodId);
            if (!ok) {
                ErrorResponse err = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "신청 취소에 실패했습니다."
                );
                return ResponseEntity.badRequest().body(err);
            }
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 4) 관리자용: 선발 로직 실행 (남/여 통합 선발)
    // ───────────────────────────────────────────────────────────────────────────

    /**
     * periodId에 대해 “전체 선발(남/여 합산)” 로직을 실행합니다.
     * 예: /api/admin/selections/1
     */
    @PostMapping("/selections/{periodId}")
    public ResponseEntity<?> runSelection(@PathVariable Integer periodId) {
        try {
            SelectionSummary summary = selectionService.runStudentSelection(periodId);
            SuccessResponse<SelectionSummary> body = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    summary.getMessage(),
                    summary
            );
            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "서버 오류가 발생했습니다."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 10) 선발 결과 조회
    @GetMapping("/selections/results")
    public ResponseEntity<?> getSelectionResults(
            @RequestParam Integer periodId,
            @RequestParam(name="page", defaultValue="1") int page
    ) {
        try {
            Pageable pg = PageRequest.of(page - 1, 25, Sort.by("selectionRank").ascending());
            Page<SelectionResult> resPage =
                    selectionResultRepository.findDistinctByPeriodPeriodId(periodId, pg);

            SuccessResponse<Page<SelectionResult>> body = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    "선발 결과 조회 성공",
                    resPage
            );
            return ResponseEntity.ok(body);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "선발 결과 조회 중 오류가 발생했습니다."
                    ));
        }
    }
}
