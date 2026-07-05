package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.DateUtil;
import com.sdut.blood.common.utils.EncryptUtil;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.RecruitmentVO;
import com.sdut.blood.mapper.BloodActivityMapper;
import com.sdut.blood.service.BloodActivityService;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.BloodStockService;
import com.sdut.blood.service.DonorService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodActivityServiceImpl extends ServiceImpl<BloodActivityMapper, BloodActivity> implements BloodActivityService {

    private static final String TIME_SLOT_MORNING = "上午";

    private static final String TIME_SLOT_AFTERNOON = "下午";

    @Resource
    @Lazy
    private DonorService donorService;

    @Resource
    @Lazy
    private BloodCollectionService bloodCollectionService;

    @Resource
    private BloodStockService bloodStockService;

    @Override
    public List<BloodActivity> listAvailableActivities() {
        return baseMapper.selectAvailableActivityList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseQuota(Long activityId, String timeSlot) {
        int affectedRows = updateQuota(activityId, timeSlot, true);
        if (affectedRows <= 0) {
            throw new BusinessException("该时段预约人数已满或活动不可预约");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseQuota(Long activityId, String timeSlot) {
        int affectedRows = updateQuota(activityId, timeSlot, false);
        if (affectedRows <= 0) {
            throw new BusinessException("活动名额恢复失败，请刷新后重试");
        }
        return true;
    }

    private int updateQuota(Long activityId, String timeSlot, boolean decrease) {
        if (activityId == null) {
            throw new BusinessException("活动ID不能为空");
        }
        if (TIME_SLOT_MORNING.equals(timeSlot)) {
            return decrease
                    ? baseMapper.decreaseMorningQuota(activityId)
                    : baseMapper.increaseMorningQuota(activityId);
        }
        if (TIME_SLOT_AFTERNOON.equals(timeSlot)) {
            return decrease
                    ? baseMapper.decreaseAfternoonQuota(activityId)
                    : baseMapper.increaseAfternoonQuota(activityId);
        }
        throw new BusinessException("时段参数错误");
    }

    @Override
    public List<RecruitmentVO> generateRecruitmentList(Long activityId, String targetBloodType) {
        List<Donor> allDonors = donorService.list(new LambdaQueryWrapper<Donor>()
                .eq(Donor::getDonorStatus, "正常"));
        
        List<RecruitmentVO> result = new ArrayList<>();
        
        for (Donor donor : allDonors) {
            if (targetBloodType != null && !targetBloodType.isEmpty() 
                    && !targetBloodType.equals(donor.getBloodType())) {
                continue;
            }
            
            if (!donorService.checkDonateQualification(donor.getId(), "全血")) {
                continue;
            }
            
            RecruitmentVO vo = new RecruitmentVO();
            vo.setDonorId(donor.getId());
            vo.setName(donor.getName());
            vo.setBloodType(donor.getBloodType());
            vo.setPhone(donor.getPhone());
            vo.setGender(donor.getGender());
            vo.setAge(donor.getAge());
            vo.setDonorStatus(donor.getDonorStatus());
            vo.setLastDonateDate(donor.getLastDonateDate());
            
            if (donor.getIdCard() != null) {
                try {
                    String idCard = EncryptUtil.decrypt(donor.getIdCard());
                    if (idCard != null && idCard.length() >= 18) {
                        vo.setIdCardMask(idCard.substring(0, 6) + "********" + idCard.substring(14));
                    }
                } catch (Exception e) {
                    vo.setIdCardMask("**********");
                }
            }
            
            DonorService.DonateStats stats = donorService.getDonateStats(donor.getId());
            vo.setTotalDonateAmount(stats.getTotalAmount());
            vo.setDonateCount(stats.getDonateCount());
            
            vo.setReason("符合献血条件");
            result.add(vo);
        }
        
        result.sort((a, b) -> {
            if (a.getDonateCount() != null && b.getDonateCount() != null) {
                return b.getDonateCount().compareTo(a.getDonateCount());
            }
            return 0;
        });
        
        return result;
    }
}
