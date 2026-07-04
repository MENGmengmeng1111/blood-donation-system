package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.DonorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * 采血记录控制器（用户自助查询）
 */
@RestController
@RequestMapping("/api/collection")
@PreAuthorize("hasAnyAuthority('ROLE_DONOR','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class CollectionSelfController {

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private DonorService donorService;

    /**
     * 查询我的采血记录
     */
    @GetMapping("/my")
    public Result<List<BloodCollection>> listMyCollections() {
        Long userId = SecurityUtil.getCurrentUserId();
        Donor donor = donorService.getByUserId(userId);
        if (donor == null) {
            return Result.success(List.of());
        }
        List<BloodCollection> list = bloodCollectionService.listByDonorId(donor.getId());
        return Result.success(list);
    }
}