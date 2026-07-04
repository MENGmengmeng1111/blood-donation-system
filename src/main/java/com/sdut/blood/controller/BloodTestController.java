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
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
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
     * 查询检验记录详情
     */
    @GetMapping("/{id}")
    public Result<BloodTest> getTestDetail(@PathVariable Long id) {
        BloodTest test = bloodTestService.getById(id);
        return Result.success(test);
    }
}
