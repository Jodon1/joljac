package com.example.dormmatching.controller.selection;

import com.example.dormmatching.service.selection.SelectionService;
import com.example.dormmatching.service.selection.SelectionService.SelectionSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/selections")
public class SelectionController {

    private final SelectionService selectionService;

    public SelectionController(SelectionService selectionService) {
        this.selectionService = selectionService;
    }

    /**
     * POST /api/selections/{periodId}
     *
     * @param periodId ApplicationPeriod ID
     * @return SelectionSummary 또는 에러 메시지
     */
    @PostMapping("/{periodId}")
    public ResponseEntity<?> runSelection(@PathVariable("periodId") Integer periodId) {
        try {
            SelectionSummary summary = selectionService.runStudentSelection(periodId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException ex) {
            // 잘못된 파라미터(기간 없음, 신청 미마감 등)
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            // 내부 서버 오류
            String errMsg = "선발 처리 중 내부 오류가 발생했습니다: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errMsg);
        }
    }
}
