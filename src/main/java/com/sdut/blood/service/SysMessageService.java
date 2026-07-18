package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.domain.entity.SysMessage;

import java.util.List;

public interface SysMessageService extends IService<SysMessage> {

    void sendMessageToTester(Long relatedId, String donorName, Integer donateAmount);

    List<SysMessage> listByUserId(Long userId);

    int countUnread(Long userId);

    void markAsRead(Long messageId);

    void markAllAsRead(Long userId);
}