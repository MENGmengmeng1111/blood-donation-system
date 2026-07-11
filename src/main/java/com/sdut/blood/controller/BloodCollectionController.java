package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.CollectionAddDTO;
import com.sdut.blood.domain.dto.CollectionUpdateDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.service.OperationLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 采血记录控制器
 */
@RestController
@RequestMapping("/api/collection")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class BloodCollectionController {

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private DonorService donorService;

    @Resource
    private OperationLogService operationLogService;

    /**
     * 新增采血记录（UC25）
     */
    @PostMapping("/add")
    public Result<Void> addCollectionRecord(@Valid @RequestBody CollectionAddDTO dto) {
        bloodCollectionService.addCollectionRecord(dto);
        Donor donor = donorService.getById(dto.getDonorId());
        String donorName = donor != null ? donor.getName() : "未知";
        operationLogService.saveLog("新增采血", "新增采血记录，献血者：" + donorName + "，血量：" + dto.getDonateAmount() + "ml");
        return Result.success();
    }

    /**
     * 分页查询采血记录列表
     */
    @GetMapping("/list")
    public Result<Page<BloodCollection>> listCollections(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String donateType,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<BloodCollection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BloodCollection> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            LambdaQueryWrapper<Donor> donorWrapper = new LambdaQueryWrapper<>();
            donorWrapper.like(Donor::getName, keyword.trim());
            List<Donor> donors = donorService.list(donorWrapper);
            if (!donors.isEmpty()) {
                List<Long> donorIds = donors.stream().map(Donor::getId).collect(Collectors.toList());
                wrapper.in(BloodCollection::getDonorId, donorIds);
            } else {
                page.setRecords(List.of());
                page.setTotal(0);
                return Result.success(page);
            }
        }
        
        if (donateType != null && !donateType.trim().isEmpty()) {
            wrapper.eq(BloodCollection::getDonateType, donateType);
        }
        if (sortField != null && !sortField.trim().isEmpty()) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            if ("donateAmount".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodCollection::getDonateAmount);
                } else {
                    wrapper.orderByDesc(BloodCollection::getDonateAmount);
                }
            } else if ("donateType".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodCollection::getDonateType);
                } else {
                    wrapper.orderByDesc(BloodCollection::getDonateType);
                }
            } else if ("initialScreenResult".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodCollection::getInitialScreenResult);
                } else {
                    wrapper.orderByDesc(BloodCollection::getInitialScreenResult);
                }
            } else if ("collectionTime".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodCollection::getCollectionTime);
                } else {
                    wrapper.orderByDesc(BloodCollection::getCollectionTime);
                }
            } else {
                wrapper.orderByDesc(BloodCollection::getCollectionTime);
            }
        } else {
            wrapper.orderByDesc(BloodCollection::getCollectionTime);
        }
        bloodCollectionService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 根据献血者ID查询历史采血记录
     */
    @GetMapping("/donor/{donorId}")
    public Result<List<BloodCollection>> listByDonorId(@PathVariable Long donorId) {
        List<BloodCollection> list = bloodCollectionService.listByDonorId(donorId);
        return Result.success(list);
    }

    /**
     * 修改采血记录
     */
    @PutMapping("/update")
    public Result<Void> updateCollectionRecord(@Valid @RequestBody CollectionUpdateDTO dto) {
        bloodCollectionService.updateCollectionRecord(dto);
        operationLogService.saveLog("修改采血", "修改采血记录，ID：" + dto.getId());
        return Result.success();
    }

    /**
     * 删除采血记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCollectionRecord(@PathVariable Long id) {
        BloodCollection collection = bloodCollectionService.getById(id);
        if (collection == null) {
            return Result.error("采血记录不存在或已删除");
        }
        boolean removed = bloodCollectionService.removeById(id);
        if (!removed) {
            return Result.error("删除失败，请刷新后重试");
        }
        operationLogService.saveLog("删除采血", "删除采血记录，ID：" + id);
        return Result.success();
    }

    /**
     * 查询采血记录详情
     */
    @GetMapping("/{id}")
    public Result<BloodCollection> getCollectionDetail(@PathVariable Long id) {
        BloodCollection collection = bloodCollectionService.getById(id);
        return Result.success(collection);
    }

    /**
     * 根据献血者ID列表查询采血记录
     */
    @GetMapping("/list-by-donor-ids")
    public Result<List<BloodCollection>> listByDonorIds(@RequestParam List<Long> donorIds) {
        LambdaQueryWrapper<BloodCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BloodCollection::getDonorId, donorIds);
        wrapper.orderByDesc(BloodCollection::getCollectionTime);
        List<BloodCollection> list = bloodCollectionService.list(wrapper);
        return Result.success(list);
    }
}
