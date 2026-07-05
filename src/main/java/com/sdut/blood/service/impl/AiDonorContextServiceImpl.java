package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdut.blood.common.constants.BloodConstants;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.AppointmentVO;
import com.sdut.blood.service.AppointmentService;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.service.AiDonorContextService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 构建用户侧AI咨询上下文
 */
@Service
public class AiDonorContextServiceImpl implements AiDonorContextService {

    @Resource
    private DonorService donorService;

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private AppointmentService appointmentService;

    @Override
    public String buildCurrentUserContext() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return "当前用户未登录，无法读取个性化献血记录。";
        }

        Donor donor = donorService.getByUserId(userId);
        if (donor == null) {
            return "当前用户尚未建立献血者档案。";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("当前日期：").append(LocalDate.now()).append('\n');
        builder.append("当前用户档案：\n");
        builder.append("- 献血者状态：").append(valueOrDash(donor.getDonorStatus())).append('\n');
        builder.append("- 血型：").append(valueOrDash(donor.getBloodType())).append('\n');
        builder.append("- 性别：").append(valueOrDash(donor.getGender())).append('\n');
        builder.append("- 年龄：").append(donor.getAge() == null ? "-" : donor.getAge()).append('\n');
        builder.append("- 最近一次献血日期：").append(donor.getLastDonateDate() == null ? "暂无记录" : donor.getLastDonateDate()).append('\n');
        builder.append("- 是否重点关注：").append(Integer.valueOf(1).equals(donor.getAttentionFlag()) ? "是" : "否").append('\n');

        DonorService.DonateStats stats = donorService.getDonateStats(donor.getId());
        if (stats != null) {
            builder.append("- 累计献血次数：").append(stats.getDonateCount()).append('\n');
            builder.append("- 累计献血量：").append(stats.getTotalAmount()).append("ml\n");
        }

        builder.append("- 系统全血资格初步判断：")
                .append(donorService.checkDonateQualification(donor.getId(), BloodConstants.DONATE_TYPE_WHOLE) ? "当前满足系统间隔与状态要求" : "当前不满足系统间隔或状态要求")
                .append('\n');

        appendAppointments(builder);
        appendRecentCollections(builder, donor.getId());

        return builder.toString();
    }

    @Override
    public boolean hasCurrentUserContext() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userId != null && donorService.getByUserId(userId) != null;
    }

    private void appendAppointments(StringBuilder builder) {
        IPage<AppointmentVO> page = appointmentService.listMyAppointments(1, 3);
        List<AppointmentVO> appointments = page == null ? List.of() : page.getRecords();
        builder.append("最近预约：\n");
        if (appointments == null || appointments.isEmpty()) {
            builder.append("- 暂无预约记录\n");
            return;
        }
        for (AppointmentVO appointment : appointments) {
            builder.append("- ")
                    .append(valueOrDash(appointment.getActivityName()))
                    .append("，日期：").append(appointment.getActivityDate() == null ? "-" : appointment.getActivityDate())
                    .append("，时段：").append(valueOrDash(appointment.getTimeSlot()))
                    .append("，状态：").append(valueOrDash(appointment.getStatus()))
                    .append('\n');
        }
    }

    private void appendRecentCollections(StringBuilder builder, Long donorId) {
        List<BloodCollection> collections = bloodCollectionService.listByDonorId(donorId);
        builder.append("最近献血记录：\n");
        if (collections == null || collections.isEmpty()) {
            builder.append("- 暂无献血记录\n");
            return;
        }
        List<BloodCollection> recentCollections = collections.stream()
                .sorted((left, right) -> compareCollectionTimeDesc(left.getCollectionTime(), right.getCollectionTime()))
                .limit(3)
                .collect(Collectors.toList());
        for (BloodCollection collection : recentCollections) {
            LocalDateTime collectionTime = collection.getCollectionTime();
            builder.append("- ")
                    .append(collectionTime == null ? "-" : collectionTime.toLocalDate())
                    .append("，类型：").append(valueOrDash(collection.getDonateType()))
                    .append("，献血量：").append(collection.getDonateAmount() == null ? "-" : collection.getDonateAmount())
                    .append("，初筛：").append(valueOrDash(collection.getInitialScreenResult()))
                    .append('\n');
        }
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private int compareCollectionTimeDesc(LocalDateTime left, LocalDateTime right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return right.compareTo(left);
    }
}
