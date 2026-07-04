package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.StockInDTO;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.vo.BloodStockVO;
import com.sdut.blood.service.BloodStockService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 血液库存控制器
 */
@RestController
@RequestMapping("/api/stock")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
public class BloodStockController {

    @Resource
    private BloodStockService bloodStockService;

    /**
     * 登记血液入库（UC32）
     */
    @PostMapping("/in")
    public Result<Void> stockIn(@Valid @RequestBody StockInDTO dto) {
        bloodStockService.stockIn(dto);
        return Result.success();
    }

    /**
     * 按血型查询库存汇总信息
     */
    @GetMapping("/summary")
    public Result<BloodStockVO> getStockSummary(@RequestParam String bloodType) {
        BloodStockVO summary = bloodStockService.getStockSummary(bloodType);
        return Result.success(summary);
    }

    /**
     * 查询临期库存列表
     */
    @GetMapping("/near-expire")
    public Result<List<BloodStock>> listNearExpire(@RequestParam(defaultValue = "7") Integer days) {
        List<BloodStock> list = bloodStockService.listNearExpire(days);
        return Result.success(list);
    }

    /**
     * 检查库存是否低于安全阈值
     */
    @GetMapping("/warning")
    public Result<Boolean> checkStockWarning(@RequestParam String bloodType) {
        boolean warning = bloodStockService.checkStockWarning(bloodType);
        return Result.success(warning);
    }
}
