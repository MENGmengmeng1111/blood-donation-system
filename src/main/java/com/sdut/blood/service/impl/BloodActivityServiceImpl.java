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
        BloodActivity activity = getById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if ("上午".equals(timeSlot)) {
            if (activity.getMorningRemaining() <= 0) {
                throw new BusinessException("该时段预约人数已满，请选择其他时段");
            }
            activity.setMorningRemaining(activity.getMorningRemaining() - 1);
        } else if ("下午".equals(timeSlot)) {
            if (activity.getAfternoonRemaining() <= 0) {
                throw new BusinessException("该时段预约人数已满，请选择其他时段");
            }
            activity.setAfternoonRemaining(activity.getAfternoonRemaining() - 1);
        } else {
            throw new BusinessException("时段参数错误");
        }

        return updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseQuota(Long activityId, String timeSlot) {
        BloodActivity activity = getById(activityId);
        if (activity == null) {
            return false;
        }

        if ("上午".equals(timeSlot)) {
            activity.setMorningRemaining(activity.getMorningRemaining() + 1);
        } else if ("下午".equals(timeSlot)) {
            activity.setAfternoonRemaining(activity.getAfternoonRemaining() + 1);
        }

        return updateById(activity);
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