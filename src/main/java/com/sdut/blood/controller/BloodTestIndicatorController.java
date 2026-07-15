package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.BloodTestIndicator;
import com.sdut.blood.service.BloodTestIndicatorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/blood-test-indicator")
public class BloodTestIndicatorController {

    @Resource
    private BloodTestIndicatorService bloodTestIndicatorService;

    @GetMapping("/pending/list")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<List<BloodTestIndicator>> listPendingTestList() {
        return bloodTestIndicatorService.listPendingTestList();
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_TESTER')")
    public Result<Void> saveIndicator(@RequestBody BloodTestIndicator indicator) {
        return bloodTestIndicatorService.saveIndicator(indicator);
    }

    @GetMapping("/{testId}")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<BloodTestIndicator> getByTestId(@PathVariable Long testId) {
        BloodTestIndicator indicator = bloodTestIndicatorService.getByTestId(testId);
        return Result.success(indicator);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<List<BloodTestIndicator>> listIndicatorHistory() {
        return bloodTestIndicatorService.listIndicatorHistory();
    }

    @PostMapping("/judge/{testId}")
    @PreAuthorize("hasAuthority('ROLE_TESTER')")
    public Result<Boolean> judgeByIndicators(@PathVariable Long testId) {
        return bloodTestIndicatorService.judgeByIndicators(testId);
    }
}