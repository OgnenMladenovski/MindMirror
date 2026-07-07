package com.mindmirror.controller;

import com.mindmirror.dto.response.HbscComparisonResponse;
import com.mindmirror.entity.HbscReferenceData;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.HbscService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hbsc")
@Tag(name = "HBSC Comparison")
public class HbscController {

    private final HbscService hbscService;

    public HbscController(HbscService hbscService) {
        this.hbscService = hbscService;
    }

    @GetMapping("/comparison")
    @Operation(summary = "Compare the user's habits with HBSC North Macedonia averages")
    public HbscComparisonResponse comparison(@AuthenticationPrincipal UserPrincipal principal) {
        return hbscService.compare(principal.getId());
    }

    @GetMapping("/reference")
    @Operation(summary = "Raw HBSC North Macedonia reference values (public)")
    public List<HbscReferenceData> reference() {
        return hbscService.referenceData();
    }
}
