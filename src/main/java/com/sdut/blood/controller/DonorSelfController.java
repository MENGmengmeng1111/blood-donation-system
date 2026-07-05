package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.DonorAddDTO;
import com.sdut.blood.domain.dto.DonorUpdateDTO;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.DonorVO;
import com.sdut.blood.service.DonorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 献血者个人档案控制器（用户自助查询）
 */
@RestController
@RequestMapping("/api/donor")
@PreAuthorize("hasAnyAuthority('ROLE_DONOR','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class DonorSelfController {

    @Resource
    private DonorService donorService;

    /**
     * 查询我的档案（身份证脱敏）
     */
    @GetMapping("/my")
    public Result<DonorVO> getMyProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        Donor donor = donorService.getByUserId(userId);
        if (donor == null) {
            return Result.success(null);
        }
        DonorVO vo = donorService.convertToVO(donor);
        return Result.success(vo);
    }

    /**
     * 完善/创建个人档案
     */
    @PostMapping("/my")
    public Result<Void> createOrUpdateProfile(@RequestBody DonorAddDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        Donor existing = donorService.getByUserId(userId);
        
        if (existing != null) {
            DonorUpdateDTO updateDTO = new DonorUpdateDTO();
            updateDTO.setId(existing.getId());
            updateDTO.setName(dto.getName());
            updateDTO.setIdCard(dto.getIdCard());
            updateDTO.setPhone(dto.getPhone());
            updateDTO.setBloodType(dto.getBloodType());
            updateDTO.setGender(dto.getGender());
            updateDTO.setAddress(dto.getAddress());
            updateDTO.setMedicalHistory(dto.getMedicalHistory());
            donorService.updateDonor(updateDTO);
        } else {
            dto.setUserId(userId);
            donorService.addDonor(dto);
        }
        
        return Result.success();
    }

    /**
     * 校验献血资格（UC17）
     */
    @GetMapping("/check-eligibility")
    public Result<String> checkEligibility() {
        Long userId = SecurityUtil.getCurrentUserId();
        Donor donor = donorService.getByUserId(userId);
        if (donor == null) {
            return Result.error("请先完善个人档案");
        }
        if ("暂缓".equals(donor.getDonorStatus())) {
            return Result.error("您的献血状态为暂缓，请联系管理员");
        }
        if ("永久淘汰".equals(donor.getDonorStatus())) {
            return Result.error("您的献血状态为永久淘汰，无法献血");
        }
        if (donorService.checkDonateQualification(donor.getId(), "全血")) {
            return Result.success("恭喜！您符合献血条件");
        } else {
            return Result.error("距上次献血不足间隔要求");
        }
    }
}
