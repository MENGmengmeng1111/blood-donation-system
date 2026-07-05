package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.CollectionAddDTO;
import com.sdut.blood.domain.dto.CollectionUpdateDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.service.BloodCollectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 采血记录控制器
 */
@RestController
@RequestMapping("/api/collection")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class BloodCollectionController {

    @Resource
    private BloodCollectionService bloodCollectionService;

    /**
     * 新增采血记录（UC25）
     */
    @PostMapping("/add")
    public Result<Void> addCollectionRecord(@Valid @RequestBody CollectionAddDTO dto) {
        bloodCollectionService.addCollectionRecord(dto);
        return Result.success();
    }

    /**
     * 分页查询采血记录列表
     */
    @GetMapping("/list")
    public Result<Page<BloodCollection>> listCollections(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BloodCollection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BloodCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BloodCollection::getCollectionTime);
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
        return Result.success();
    }

    /**
     * 删除采血记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCollectionRecord(@PathVariable Long id) {
        bloodCollectionService.removeById(id);
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
}