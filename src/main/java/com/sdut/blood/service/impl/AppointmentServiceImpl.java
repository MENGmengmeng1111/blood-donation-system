package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.DateUtil;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.AppointmentCancelDTO;
import com.sdut.blood.domain.dto.AppointmentSubmitDTO;
import com.sdut.blood.domain.entity.Appointment;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.AppointmentVO;
import com.sdut.blood.mapper.AppointmentMapper;
import com.sdut.blood.service.AppointmentService;
import com.sdut.blood.service.BloodActivityService;
import com.sdut.blood.service.DonorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Resource
    private BloodActivityService bloodActivityService;

    @Resource
    private DonorService donorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAppointment(AppointmentSubmitDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 1. 校验活动是否存在且可预约
        log.info("预约提交 - activityId: {}, type: {}", dto.getActivityId(), dto.getActivityId() != null ? dto.getActivityId().getClass().getName() : "null");
        BloodActivity activity = bloodActivityService.getById(dto.getActivityId());
        log.info("查询到的活动: {}", activity);
        if (activity == null) {
            throw new BusinessException("该活动不存在");
        }
        
        // 检查活动日期是否已过期
        if (activity.getActivityDate().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("该活动已过期，无法预约");
        }
        
        if ("已结束".equals(activity.getStatus())) {
            throw new BusinessException("该活动已结束，无法预约");
        }

        // 2. 校验重复预约
        Integer count = baseMapper.countByUserIdAndActivityId(userId, dto.getActivityId());
        if (count > 0) {
            throw new BusinessException("您已预约该活动，不可重复预约");
        }

        // 3. 校验献血者档案是否完整
        Donor donor = donorService.getByUserId(userId);
        if (donor == null || donor.getPhone() == null || donor.getPhone().isEmpty()) {
            throw new BusinessException("请先完善个人档案信息后再预约");
        }

        // 4. 校验献血间隔（默认全血）
        if (!donorService.checkDonateQualification(donor.getId(), "全血")) {
            throw new BusinessException("距上次献血不足间隔要求，暂无法预约");
        }

        // 5. 扣减活动名额
        bloodActivityService.decreaseQuota(dto.getActivityId(), dto.getTimeSlot());

        // 6. 生成预约记录
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setActivityId(dto.getActivityId());
        appointment.setTimeSlot(dto.getTimeSlot());
        appointment.setStatus("待参加");
        appointment.setAppointmentTime(LocalDateTime.now());
        save(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(AppointmentCancelDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();

        // 1. 校验预约记录存在且属于当前用户
        Appointment appointment = getById(dto.getAppointmentId());
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException("无权取消他人预约");
        }

        // 2. 校验预约状态
        if (!"待参加".equals(appointment.getStatus())) {
            throw new BusinessException("该预约已生效，无法取消");
        }

        // 3. 校验取消时间（活动开始前24小时）
        BloodActivity activity = bloodActivityService.getById(appointment.getActivityId());
        LocalDateTime activityStartTime = activity.getActivityDate().atTime(8, 0);
        if (!DateUtil.canCancelAppointment(activityStartTime)) {
            throw new BusinessException("距活动开始不足24小时，无法取消");
        }

        // 4. 恢复活动名额
        bloodActivityService.increaseQuota(appointment.getActivityId(), appointment.getTimeSlot());

        // 5. 更新预约状态
        appointment.setStatus("已取消");
        appointment.setCancelTime(LocalDateTime.now());
        updateById(appointment);
    }

    @Override
    public IPage<AppointmentVO> listMyAppointments(Integer pageNum, Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<AppointmentVO> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectUserAppointmentPage(page, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointmentById(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException("无权取消他人预约");
        }
        if (!"待参加".equals(appointment.getStatus())) {
            throw new BusinessException("该预约已生效，无法取消");
        }
        BloodActivity activity = bloodActivityService.getById(appointment.getActivityId());
        LocalDateTime activityStartTime = activity.getActivityDate().atTime(8, 0);
        if (!DateUtil.canCancelAppointment(activityStartTime)) {
            throw new BusinessException("距活动开始不足24小时，无法取消");
        }
        bloodActivityService.increaseQuota(appointment.getActivityId(), appointment.getTimeSlot());
        appointment.setStatus("已取消");
        appointment.setCancelTime(LocalDateTime.now());
        updateById(appointment);
    }
}