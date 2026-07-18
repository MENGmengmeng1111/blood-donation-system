package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.domain.entity.SysMessage;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.mapper.SysMessageMapper;
import com.sdut.blood.service.SysMessageService;
import com.sdut.blood.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    @Resource
    private SysUserService sysUserService;

    @Override
    public void sendMessageToTester(Long relatedId, String donorName, Integer donateAmount) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRole, "ROLE_TESTER");
        wrapper.eq(SysUser::getStatus, 0);
        List<SysUser> testers = sysUserService.list(wrapper);

        for (SysUser tester : testers) {
            SysMessage message = new SysMessage();
            message.setUserId(tester.getId());
            message.setTitle("新的采血记录待化验");
            message.setContent("献血者【" + donorName + "】的血液已采集完成，血量" + donateAmount + "ml，请及时进行化验。");
            message.setType("collection");
            message.setRelatedId(relatedId);
            message.setReadStatus(0);
            message.setCreateTime(LocalDateTime.now());
            save(message);
        }
    }

    @Override
    public List<SysMessage> listByUserId(Long userId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getUserId, userId);
        wrapper.orderByDesc(SysMessage::getCreateTime);
        return list(wrapper);
    }

    @Override
    public int countUnread(Long userId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getUserId, userId);
        wrapper.eq(SysMessage::getReadStatus, 0);
        return (int) count(wrapper);
    }

    @Override
    public void markAsRead(Long messageId) {
        SysMessage message = getById(messageId);
        if (message != null) {
            message.setReadStatus(1);
            updateById(message);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getUserId, userId);
        wrapper.eq(SysMessage::getReadStatus, 0);
        List<SysMessage> messages = list(wrapper);
        for (SysMessage message : messages) {
            message.setReadStatus(1);
        }
        updateBatchById(messages);
    }
}