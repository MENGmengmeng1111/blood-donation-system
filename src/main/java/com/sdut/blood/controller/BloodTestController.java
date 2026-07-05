package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.BloodTestJudgeDTO;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.service.BloodTestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 血液检验控制器
 */
@RestController
@RequestMapping("/api/blood-test")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class BloodTestController {

    @Resource
    private BloodTestService bloodTestService;

    /**
     * 查询所有待判定的检验记录
     */
    @GetMapping("/pending/list")
    public Result<List<BloodTest>> listPendingJudge() {
        List<BloodTest> list = bloodTestService.listPendingJudge();
        return Result.success(list);
    }

    /**
     * 判定血液合格状态（UC29）
     */
    @PostMapping("/judge")
    public Result<Void> judgeBloodStatus(@Valid @RequestBody BloodTestJudgeDTO dto) {
        bloodTestService.judgeBloodStatus(dto);
        return Result.success();
    }

    /**
     * 修改检验信息（PUT方式）
     */
    @PutMapping("/judge")
    public Result<Void> updateJudgeBloodStatus(@Valid @RequestBody BloodTestJudgeDTO dto) {
        bloodTestService.judgeBloodStatus(dto);
        return Result.success();
    }

    /**
     * 查询检验记录详情
     */
    @GetMapping("/{id}")
    public Result<BloodTest> getTestDetail(@PathVariable Long id) {
        BloodTest test = bloodTestService.getById(id);
        return Result.success(test);
    }

    /**
     * 查询检验记录列表（UC28）
     */
    @GetMapping("/list")
    public Result<List<BloodTest>> listTestRecords(
            @RequestParam(required = false) Long donorId,
            @RequestParam(required = false) String bloodStatus) {
        return bloodTestService.listTestRecords(donorId, bloodStatus);
    }

    /**
     * 删除检验记录（UC26）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteTestRecord(@PathVariable Long id) {
        bloodTestService.removeById(id);
        return Result.success();
    }

    /**
     * 修改检验信息（UC27）
     */
    @PutMapping("/update")
    public Result<Void> updateTestRecord(@RequestBody BloodTest bloodTest) {
        bloodTestService.updateById(bloodTest);
        return Result.success();
    }
}