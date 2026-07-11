package com.sdut.blood.controller;

import com.sdut.blood.common.constants.BloodConstants;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.BloodTestJudgeDTO;
import com.sdut.blood.domain.dto.BloodTestUpdateDTO;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.service.OperationLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 血液检验控制器
 */
@RestController
@RequestMapping("/api/blood-test")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class BloodTestController {

    @Resource
    private BloodTestService bloodTestService;

    @Resource
    private DonorService donorService;

    @Resource
    private OperationLogService operationLogService;

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
        operationLogService.saveLog("判定检验", "判定血液检验结果，检验ID：" + dto.getTestId() + "，结果：" + dto.getBloodStatus());
        return Result.success();
    }

    /**
     * 修改检验信息（PUT方式）
     */
    @PutMapping("/judge")
    public Result<Void> updateJudgeBloodStatus(@Valid @RequestBody BloodTestJudgeDTO dto) {
        bloodTestService.judgeBloodStatus(dto);
        operationLogService.saveLog("判定检验", "判定血液检验结果，检验ID：" + dto.getTestId() + "，结果：" + dto.getBloodStatus());
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bloodStatus,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        if (keyword != null && !keyword.trim().isEmpty() && donorId == null) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Donor> donorWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            donorWrapper.like(Donor::getName, keyword.trim());
            List<Donor> donors = donorService.list(donorWrapper);
            if (!donors.isEmpty()) {
                donorId = donors.stream().map(Donor::getId).collect(Collectors.toList()).get(0);
            } else {
                return Result.success(List.of());
            }
        }
        return bloodTestService.listTestRecords(donorId, bloodStatus, sortField, sortOrder);
    }

    /**
     * 删除检验记录（UC26）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteTestRecord(@PathVariable Long id) {
        BloodTest test = bloodTestService.getById(id);
        if (test == null) {
            return Result.error("检验记录不存在或已删除");
        }
        if (BloodConstants.STATUS_STORED.equals(test.getBloodStatus())) {
            return Result.error("血液已入库，无法删除检验记录");
        }
        boolean removed = bloodTestService.removeById(id);
        if (!removed) {
            return Result.error("删除失败，请刷新后重试");
        }
        operationLogService.saveLog("删除检验", "删除检验记录，ID：" + id);
        return Result.success();
    }

    /**
     * 修改检验信息（UC27）
     */
    @PutMapping("/update")
    public Result<Void> updateTestRecord(@Valid @RequestBody BloodTestUpdateDTO dto) {
        bloodTestService.updateTestRecord(dto);
        operationLogService.saveLog("修改检验", "修改检验记录，ID：" + dto.getId());
        return Result.success();
    }
}
