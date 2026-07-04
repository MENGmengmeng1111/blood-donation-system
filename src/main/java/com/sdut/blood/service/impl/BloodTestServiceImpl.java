package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.BloodTestJudgeDTO;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.mapper.BloodTestMapper;
import com.sdut.blood.service.BloodTestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BloodTestServiceImpl extends ServiceImpl<BloodTestMapper, BloodTest> implements BloodTestService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void judgeBloodStatus(BloodTestJudgeDTO dto) {
        // 1. 校验记录存在
        BloodTest test = getById(dto.getTestId());
        if (test == null) {
            throw new BusinessException("检验记录不存在");
        }

        // 2. 校验状态：已入库不可修改
        if ("已入库".equals(test.getBloodStatus())) {
            throw new BusinessException("血液已入库，无法修改判定状态");
        }

        // 3. 不合格必须填写原因
        if ("不合格".equals(dto.getBloodStatus())) {
            if (dto.getUnqualifiedReason() == null || dto.getUnqualifiedReason().trim().isEmpty()) {
                throw new BusinessException("不合格血液请填写具体原因");
            }
            test.setUnqualifiedReason(dto.getUnqualifiedReason());
            test.setBloodStatus("不合格");
        } else if ("合格".equals(dto.getBloodStatus())) {
            test.setBloodStatus("合格");
        } else {
            throw new BusinessException("请选择血液判定状态");
        }

        test.setJudgeTime(LocalDateTime.now());
        test.setOperatorId(SecurityUtil.requireCurrentUserId());
        updateById(test);
    }

    @Override
    public List<BloodTest> listPendingJudge() {
        return baseMapper.selectPendingJudgeList();
    }

    @Override
    public BloodTest getByCollectionId(Long collectionId) {
        return baseMapper.selectByCollectionId(collectionId);
    }
}
