package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.AppointmentCancelDTO;
import com.sdut.blood.domain.dto.AppointmentSubmitDTO;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.domain.vo.AppointmentVO;
import com.sdut.blood.service.AppointmentService;
import com.sdut.blood.service.BloodActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 献血预约控制器
 */
@RestController
@RequestMapping("/api/appointment")
@PreAuthorize("hasAuthority('ROLE_DONOR')")
@Slf4j
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    @Resource
    private BloodActivityService bloodActivityService;

    /**
     * 查询可预约的活动列表
     */
    @GetMapping("/activity/list")
    public Result<List<BloodActivity>> listAvailableActivities() {
        List<BloodActivity> activityList = bloodActivityService.listAvailableActivities();
        return Result.success(activityList);
    }

    /**
     * 提交献血预约（UC05）
     */
    @PostMapping("/submit")
    public Result<Void> submitAppointment(@Valid @RequestBody AppointmentSubmitDTO dto) {
        log.info("预约请求 - activityId: {}, type: {}, hashCode: {}", 
                dto.getActivityId(), 
                dto.getActivityId() != null ? dto.getActivityId().getClass().getName() : "null",
                dto.getActivityId() != null ? dto.getActivityId().hashCode() : "null");
        log.info("timeSlot: {}", dto.getTimeSlot());
        
        BloodActivity activity = bloodActivityService.getById(dto.getActivityId());
        log.info("数据库中查询到的活动: {}", activity);
        
        appointmentService.submitAppointment(dto);
        return Result.success();
    }

    /**
     * 取消献血预约（UC06）
     */
    @PostMapping("/cancel")
    public Result<Void> cancelAppointment(@Valid @RequestBody AppointmentCancelDTO dto) {
        appointmentService.cancelAppointment(dto);
        return Result.success();
    }

    /**
     * 取消献血预约（简化版）
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelAppointmentById(@PathVariable Long id) {
        appointmentService.cancelAppointmentById(id);
        return Result.success();
    }

    /**
     * 分页查询我的预约列表
     */
    @GetMapping("/my/list")
    public Result<IPage<AppointmentVO>> listMyAppointments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<AppointmentVO> page = appointmentService.listMyAppointments(pageNum, pageSize);
        return Result.success(page);
    }
}