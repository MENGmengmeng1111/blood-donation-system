package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.StockInDTO;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.vo.BloodStockVO;
import com.sdut.blood.domain.vo.StockTrendVO;
import com.sdut.blood.domain.vo.StockWarningVO;
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
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

    /**
     * 查询库存明细列表（UC35）
     */
    @GetMapping("/list")
    public Result<List<BloodStock>> listStockDetails(
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) String status) {
        return bloodStockService.listStockDetails(bloodType, status);
    }

    /**
     * 删除库存记录（UC33）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteStockRecord(@PathVariable Long id) {
        bloodStockService.removeById(id);
        return Result.success();
    }

    /**
     * 修改库存信息（UC34）
     */
    @PutMapping("/update")
    public Result<Void> updateStockInfo(@RequestBody BloodStock bloodStock) {
        bloodStockService.updateStock(bloodStock);
        return Result.success();
    }

    /**
     * 血液出库
     */
    @PutMapping("/out/{id}")
    public Result<Void> stockOut(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        String outUnit = params.get("outUnit");
        bloodStockService.stockOut(id, outUnit);
        return Result.success();
    }

    /**
     * 查看库存预警列表（UC36）
     */
    @GetMapping("/warning/list")
    public Result<List<StockWarningVO>> listStockWarning() {
        return bloodStockService.listStockWarning();
    }

    /**
     * 获取所有血型库存预警详情
     */
    @GetMapping("/warning/details")
    public Result<List<StockWarningVO>> getStockWarningDetails() {
        List<StockWarningVO> details = bloodStockService.getStockWarningDetails();
        return Result.success(details);
    }

    /**
     * 生成库存趋势分析（UC37）
     */
    @GetMapping("/trend")
    public Result<List<StockTrendVO>> getStockTrend(@RequestParam String bloodType) {
        return bloodStockService.getStockTrend(bloodType);
    }
}