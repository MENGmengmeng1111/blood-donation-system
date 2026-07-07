package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.EncryptUtil;
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
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
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
     * 分页查询档案列表（支持按血型、献血状态筛选）
     */
    @GetMapping("/list")
    public Result<Page<Donor>> listDonors(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) String donorStatus,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<Donor> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Donor> wrapper = new LambdaQueryWrapper<>();
        if (bloodType != null && !bloodType.trim().isEmpty()) {
            wrapper.eq(Donor::getBloodType, bloodType);
        }
        if (donorStatus != null && !donorStatus.trim().isEmpty()) {
            wrapper.eq(Donor::getDonorStatus, donorStatus);
        }
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Donor::getName, name.trim());
        }
        if (sortField != null && !sortField.trim().isEmpty()) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            if ("name".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(Donor::getName);
                } else {
                    wrapper.orderByDesc(Donor::getName);
                }
            } else if ("bloodType".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(Donor::getBloodType);
                } else {
                    wrapper.orderByDesc(Donor::getBloodType);
                }
            } else if ("donorStatus".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(Donor::getDonorStatus);
                } else {
                    wrapper.orderByDesc(Donor::getDonorStatus);
                }
            } else {
                wrapper.orderByDesc(Donor::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Donor::getCreateTime);
        }
        donorService.page(page, wrapper);
        page.getRecords().forEach(donor -> {
            if (donor.getIdCard() != null && !donor.getIdCard().isEmpty()) {
                try {
                    donor.setIdCard(EncryptUtil.decrypt(donor.getIdCard()));
                } catch (Exception e) {
                }
            }
            if (donor.getMedicalHistory() != null && !donor.getMedicalHistory().isEmpty()) {
                try {
                    donor.setMedicalHistory(EncryptUtil.decrypt(donor.getMedicalHistory()));
                } catch (Exception e) {
                }
            }
        });
        return Result.success(page);
    }

    /**
     * 查询档案详情（身份证解密）
     */
    @GetMapping("/{id}")
    public Result<Donor> getDonorDetail(@PathVariable Long id) {
        Donor donor = donorService.getById(id);
        if (donor != null) {
            if (donor.getIdCard() != null && !donor.getIdCard().isEmpty()) {
                try {
                    donor.setIdCard(EncryptUtil.decrypt(donor.getIdCard()));
                } catch (Exception e) {
                }
            }
            if (donor.getMedicalHistory() != null && !donor.getMedicalHistory().isEmpty()) {
                try {
                    donor.setMedicalHistory(EncryptUtil.decrypt(donor.getMedicalHistory()));
                } catch (Exception e) {
                }
            }
        }
        return Result.success(donor);
    }

    /**
     * 删除献血者档案（UC14）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDonor(@PathVariable Long id) {
        donorService.removeById(id);
        return Result.success();
    }
}
