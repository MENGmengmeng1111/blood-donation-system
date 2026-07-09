package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.StockInDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.BloodStockVO;
import com.sdut.blood.domain.vo.PendingStockInVO;
import com.sdut.blood.domain.vo.PendingStockOutVO;
import com.sdut.blood.domain.vo.StockTrendVO;
import com.sdut.blood.domain.vo.StockWarningVO;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.BloodStockService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.service.OperationLogService;
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
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class BloodStockController {

    @Resource
    private BloodStockService bloodStockService;

    @Resource
    private DonorService donorService;

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private OperationLogService operationLogService;

    /**
     * 查询待入库记录列表（检验合格但未入库的采血记录）
     */
    @GetMapping("/pending")
    public Result<List<PendingStockInVO>> listPendingStockIn(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bloodType) {
        List<PendingStockInVO> list = bloodStockService.listPendingStockIn(keyword, bloodType);
        return Result.success(list);
    }

    /**
     * 查询可出库库存记录列表（状态为正常/临期/已过期的库存）
     */
    @GetMapping("/out-pending")
    public Result<List<PendingStockOutVO>> listStockOutPending(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bloodType) {
        List<PendingStockOutVO> list = bloodStockService.listStockOutPending(keyword, bloodType);
        return Result.success(list);
    }

    /**
     * 登记血液入库（UC32）
     */
    @PostMapping("/in")
    public Result<Void> stockIn(@Valid @RequestBody StockInDTO dto) {
        bloodStockService.stockIn(dto);
        operationLogService.saveLog("血液入库", "血液入库，采血记录ID：" + dto.getCollectionId());
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Donor> donorWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            donorWrapper.like(Donor::getName, keyword.trim());
            java.util.List<Donor> donors = donorService.list(donorWrapper);
            if (!donors.isEmpty()) {
                java.util.List<Long> donorIds = donors.stream().map(Donor::getId).collect(java.util.stream.Collectors.toList());
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BloodCollection> collectionWrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                collectionWrapper.in(BloodCollection::getDonorId, donorIds);
                java.util.List<BloodCollection> collections = bloodCollectionService.list(collectionWrapper);
                if (!collections.isEmpty()) {
                    java.util.List<Long> collectionIds = collections.stream().map(BloodCollection::getId).collect(java.util.stream.Collectors.toList());
                    com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BloodStock> wrapper = 
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                    wrapper.in(BloodStock::getCollectionId, collectionIds);
                    if (bloodType != null && !bloodType.trim().isEmpty()) {
                        wrapper.eq(BloodStock::getBloodType, bloodType);
                    }
                    if (status != null && !status.trim().isEmpty()) {
                        wrapper.eq(BloodStock::getStatus, status);
                    }
                    if (sortField != null && !sortField.trim().isEmpty()) {
                        boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
                        if ("bloodType".equals(sortField.trim())) {
                            if (isAsc) wrapper.orderByAsc(BloodStock::getBloodType);
                            else wrapper.orderByDesc(BloodStock::getBloodType);
                        } else if ("bloodAmount".equals(sortField.trim())) {
                            if (isAsc) wrapper.orderByAsc(BloodStock::getBloodAmount);
                            else wrapper.orderByDesc(BloodStock::getBloodAmount);
                        } else if ("expireDate".equals(sortField.trim())) {
                            if (isAsc) wrapper.orderByAsc(BloodStock::getExpireDate);
                            else wrapper.orderByDesc(BloodStock::getExpireDate);
                        } else if ("status".equals(sortField.trim())) {
                            if (isAsc) wrapper.orderByAsc(BloodStock::getStatus);
                            else wrapper.orderByDesc(BloodStock::getStatus);
                        } else if ("createTime".equals(sortField.trim())) {
                            if (isAsc) wrapper.orderByAsc(BloodStock::getCreateTime);
                            else wrapper.orderByDesc(BloodStock::getCreateTime);
                        } else {
                            wrapper.orderByDesc(BloodStock::getCreateTime);
                        }
                    } else {
                        wrapper.orderByDesc(BloodStock::getCreateTime);
                    }
                    return Result.success(bloodStockService.list(wrapper));
                }
            }
            return Result.success(java.util.List.of());
        }
        return bloodStockService.listStockDetails(bloodType, status, sortField, sortOrder);
    }

    /**
     * 查询库存记录详情
     */
    @GetMapping("/{id}")
    public Result<BloodStock> getStockDetail(@PathVariable Long id) {
        BloodStock stock = bloodStockService.getById(id);
        if (stock == null) {
            return Result.error("库存记录不存在或已删除");
        }
        return Result.success(stock);
    }

    /**
     * 删除库存记录（UC33）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteStockRecord(@PathVariable Long id) {
        bloodStockService.removeById(id);
        operationLogService.saveLog("删除库存", "删除库存记录，ID：" + id);
        return Result.success();
    }

    /**
     * 修改库存信息（UC34）
     */
    @PutMapping("/update")
    public Result<Void> updateStockInfo(@RequestBody BloodStock bloodStock) {
        bloodStockService.updateStock(bloodStock);
        operationLogService.saveLog("修改库存", "修改库存信息，ID：" + bloodStock.getId());
        return Result.success();
    }

    /**
     * 血液出库
     */
    @PutMapping("/out/{id}")
    public Result<Void> stockOut(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        String outUnit = params.get("outUnit");
        bloodStockService.stockOut(id, outUnit);
        operationLogService.saveLog("血液出库", "血液出库，库存ID：" + id + "，用血单位：" + outUnit);
        return Result.success();
    }

    /**
     * 查询入库操作历史
     */
    @GetMapping("/in-history")
    public Result<List<com.sdut.blood.domain.vo.StockHistoryVO>> listStockInHistory() {
        List<com.sdut.blood.domain.vo.StockHistoryVO> list = bloodStockService.listStockInHistory();
        return Result.success(list);
    }

    /**
     * 查询出库操作历史
     */
    @GetMapping("/out-history")
    public Result<List<com.sdut.blood.domain.vo.StockHistoryVO>> listStockOutHistory() {
        List<com.sdut.blood.domain.vo.StockHistoryVO> list = bloodStockService.listStockOutHistory();
        return Result.success(list);
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
