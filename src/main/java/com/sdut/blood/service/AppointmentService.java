package com.sdut.blood.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.domain.dto.AppointmentCancelDTO;
import com.sdut.blood.domain.dto.AppointmentSubmitDTO;
import com.sdut.blood.domain.vo.AppointmentVO;

/**
 * 预约记录服务接口
 */
public interface AppointmentService extends IService<com.sdut.blood.domain.entity.Appointment> {

    /**
     * 提交献血预约（UC05）
     */
    void submitAppointment(AppointmentSubmitDTO dto);

    /**
     * 取消献血预约（UC06）
     */
    void cancelAppointment(AppointmentCancelDTO dto);

    /**
     * 分页查询我的预约列表
     */
    IPage<AppointmentVO> listMyAppointments(Integer pageNum, Integer pageSize);

    /**
     * 根据ID取消预约
     */
    void cancelAppointmentById(Long id);
}