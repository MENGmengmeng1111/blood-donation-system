package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.DonorAddDTO;
import com.sdut.blood.domain.dto.DonorUpdateDTO;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.DonorVO;
import com.sdut.blood.service.DonorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 献血者档案控制器
 */
@RestController
@RequestMapping("/api/donor")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
public class DonorController {

    @Resource
    private DonorService donorService;

    /**
     * 新增献血者档案（UC13）
     */
    @PostMapping("/add")
    public Result<Void> addDonor(@Valid @RequestBody DonorAddDTO dto) {
        donorService.addDonor(dto);
        return Result.success();
    }

    /**
     * 修改献血者档案（UC15）
     */
    @PutMapping("/update")
    public Result<Void> updateDonor(@Valid @RequestBody DonorUpdateDTO dto) {
        donorService.updateDonor(dto);
        return Result.success();
    }

    /**
     * 分页查询档案列表（支持按血型筛选）
     */
    @GetMapping("/list")
    public Result<Page<Donor>> listDonors(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String bloodType) {
        Page<Donor> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Donor> wrapper = new LambdaQueryWrapper<>();
        if (bloodType != null && !bloodType.trim().isEmpty()) {
            wrapper.eq(Donor::getBloodType, bloodType);
        }
        wrapper.orderByDesc(Donor::getCreateTime);
        donorService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 查询档案详情（身份证脱敏）
     */
    @GetMapping("/{id}")
    public Result<DonorVO> getDonorDetail(@PathVariable Long id) {
        Donor donor = donorService.getById(id);
        DonorVO vo = donorService.convertToVO(donor);
        return Result.success(vo);
    }
}
