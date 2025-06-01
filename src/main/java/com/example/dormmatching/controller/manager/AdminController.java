package com.example.dormmatching.controller.manager;

import com.example.dormmatching.dto.DormCapacityRequest;
import com.example.dormmatching.dto.RegisterRequest;
import com.example.dormmatching.dto.Response.ErrorResponse;
import com.example.dormmatching.dto.Response.SuccessResponse;
import com.example.dormmatching.entity.application.DormCapacity;
import com.example.dormmatching.service.DormApplicationService;
import com.example.dormmatching.service.DormCapacityService;
import com.example.dormmatching.service.auth.AuthService;
import com.example.dormmatching.service.selection.SelectionService;
import com.example.dormmatching.service.selection.SelectionService.SelectionSummary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final DormCapacityService capacityService;
    private final DormApplicationService applicationService;
    private final SelectionService selectionService;

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

    // ───────────────────────────────────────────────────────────────────────────
    // 2) 관리자용: 기숙사 정원 설정 / 조회 / 삭제
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
    public ResponseEntity<?> getCapacitiesByPeriod(@PathVariable Integer periodId) {
        try {
            List<DormCapacity> list = capacityService.getCapacitiesByPeriod(periodId);
            SuccessResponse<List<DormCapacity>> body = new SuccessResponse<>(
                    HttpStatus.OK.value(),
                    true,
                    "정원 조회가 완료되었습니다.",
                    list
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
    // 3) 관리자용: 기숙사 신청 페이지(예: 검증용) 조회/삭제 등 (추가 기능 가능)
    //    여기서는 “신청 강제 취소” 하나만 예시로 둡니다.
    // ───────────────────────────────────────────────────────────────────────────

    /**
     * 특정 userId, periodId에 대해 기숙사 신청 강제 취소
     * (관리자 권한으로, 누군가 신청했으나 관리자가 직접 취소해야 하는 경우)
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
    // 4) 관리자용: 선발 로직 실행 (전체 통합 선발)
    // ───────────────────────────────────────────────────────────────────────────

    /**
     * periodId에 대해 “전체 선발(남/여 합산)” 로직을 실행합니다.
     * 예: POST /api/admin/selections/1
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
}
