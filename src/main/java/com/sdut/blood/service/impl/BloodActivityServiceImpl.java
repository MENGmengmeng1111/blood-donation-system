package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.DateUtil;
import com.sdut.blood.common.utils.EncryptUtil;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.RecruitmentVO;
import com.sdut.blood.domain.vo.StockWarningVO;
import com.sdut.blood.mapper.BloodActivityMapper;
import com.sdut.blood.service.BloodActivityService;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.BloodStockService;
import com.sdut.blood.service.DonorService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        List<String> neededBloodTypes = getNeededBloodTypes(targetBloodType);
        if (neededBloodTypes.isEmpty()) {
            return List.of();
        }

        List<Donor> allDonors = donorService.list(new LambdaQueryWrapper<Donor>()
                .eq(Donor::getDonorStatus, "正常")
                .in(Donor::getBloodType, neededBloodTypes));
        
        List<RecruitmentVO> result = new ArrayList<>();
        
        for (Donor donor : allDonors) {
            if (Integer.valueOf(1).equals(donor.getAttentionFlag())) {
                continue;
            }
            BloodCollection lastRecord = getLatestDonateRecord(donor.getId());
            String donateType = lastRecord == null ? "全血" : lastRecord.getDonateType();
            LocalDate lastDonateDate = getEffectiveLastDonateDate(donor, lastRecord);
            if (!isEligibleByCycle(lastDonateDate, donateType)) {
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
            vo.setLastDonateDate(lastDonateDate);
            
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
            
            vo.setReason(buildRecruitmentReason(donor.getBloodType(), donateType, lastDonateDate));
            result.add(vo);
        }
        
        Map<String, Integer> bloodTypePriority = buildBloodTypePriority(neededBloodTypes);
        result.sort(Comparator
                .comparing((RecruitmentVO vo) -> bloodTypePriority.getOrDefault(vo.getBloodType(), Integer.MAX_VALUE))
                .thenComparing(RecruitmentVO::getLastDonateDate, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(RecruitmentVO::getDonateCount, Comparator.nullsLast(Comparator.reverseOrder())));
        
        return result;
    }

    private List<String> getNeededBloodTypes(String targetBloodType) {
        if (targetBloodType != null && !targetBloodType.trim().isEmpty()) {
            return List.of(targetBloodType.trim());
        }
        List<StockWarningVO> warningDetails = bloodStockService.getStockWarningDetails();
        List<String> shortageTypes = warningDetails.stream()
                .filter(item -> item.getShortageAmount() != null && item.getShortageAmount() > 0)
                .sorted(Comparator.comparing(StockWarningVO::getShortageAmount, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(StockWarningVO::getBloodType)
                .collect(Collectors.toList());
        if (!shortageTypes.isEmpty()) {
            return shortageTypes;
        }
        return warningDetails.stream()
                .sorted(Comparator.comparing(StockWarningVO::getCurrentStock, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(StockWarningVO::getBloodType)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> buildBloodTypePriority(List<String> bloodTypes) {
        java.util.Map<String, Integer> priority = new java.util.HashMap<>();
        for (int i = 0; i < bloodTypes.size(); i++) {
            priority.put(bloodTypes.get(i), i);
        }
        return priority;
    }

    private BloodCollection getLatestDonateRecord(Long donorId) {
        return bloodCollectionService.getOne(new LambdaQueryWrapper<BloodCollection>()
                .eq(BloodCollection::getDonorId, donorId)
                .orderByDesc(BloodCollection::getCollectionTime)
                .last("LIMIT 1"));
    }

    private LocalDate getEffectiveLastDonateDate(Donor donor, BloodCollection lastRecord) {
        if (lastRecord != null && lastRecord.getCollectionTime() != null) {
            return lastRecord.getCollectionTime().toLocalDate();
        }
        return donor.getLastDonateDate();
    }

    private boolean isEligibleByCycle(LocalDate lastDonateDate, String donateType) {
        if ("成分血".equals(donateType)) {
            return DateUtil.checkComponentBloodInterval(lastDonateDate);
        }
        return DateUtil.checkWholeBloodInterval(lastDonateDate);
    }

    private String buildRecruitmentReason(String bloodType, String donateType, LocalDate lastDonateDate) {
        String interval = "成分血".equals(donateType) ? "已满足28天献血间隔" : "已满足6个月献血间隔";
        String lastDonate = lastDonateDate == null ? "无历史献血记录" : "上次献血：" + lastDonateDate;
        return bloodType + "库存需要补充；" + interval + "；" + lastDonate;
    }
}
