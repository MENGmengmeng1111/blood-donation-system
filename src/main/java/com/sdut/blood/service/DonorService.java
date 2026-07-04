package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.domain.dto.DonorAddDTO;
import com.sdut.blood.domain.dto.DonorUpdateDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.DonorVO;

import java.util.List;

/**
 * 献血者档案服务接口
 */
public interface DonorService extends IService<Donor> {

    /**
     * 新增献血者档案（UC13）
     */
    void addDonor(DonorAddDTO dto);

    /**
     * 修改献血者档案（UC15）
     */
    void updateDonor(DonorUpdateDTO dto);

    /**
     * 根据身份证号查询档案
     */
    Donor getByIdCard(String idCard);

    /**
     * 根据用户ID查询档案
     */
    Donor getByUserId(Long userId);

    /**
     * 校验献血资格
     */
    boolean checkDonateQualification(Long donorId, String donateType);

    /**
     * 转换为脱敏视图对象
     */
    DonorVO convertToVO(Donor donor);

    /**
     * 获取献血者历史献血记录
     */
    List<BloodCollection> getDonateRecords(Long donorId);

    /**
     * 获取献血者献血统计
     */
    DonateStats getDonateStats(Long donorId);

    @lombok.Data
    class DonateStats {
        private Integer donateCount;
        private Integer totalAmount;
    }
}