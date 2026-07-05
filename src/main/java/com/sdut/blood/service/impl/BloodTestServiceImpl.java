package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.BloodTestJudgeDTO;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.mapper.BloodTestMapper;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.domain.entity.Donor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BloodTestServiceImpl extends ServiceImpl<BloodTestMapper, BloodTest> implements BloodTestService {

    @Resource
    @Lazy
    private DonorService donorService;

    private static final int ATTENTION_THRESHOLD = 2;

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

        if (dto.getRecheckResult() != null) {
            test.setRecheckResult(dto.getRecheckResult());
        }
        if (dto.getRemark() != null) {
            test.setRemark(dto.getRemark());
        }

        test.setJudgeTime(LocalDateTime.now());
        Long operatorId = SecurityUtil.getCurrentUserId();
        if (operatorId != null) {
            test.setOperatorId(operatorId);
        }
        updateById(test);
        
        if ("不合格".equals(dto.getBloodStatus())) {
            checkAndMarkAttention(test.getDonorId());
        }
    }
    
    private void checkAndMarkAttention(Long donorId) {
        if (donorId == null) {
            return;
        }
        
        LambdaQueryWrapper<BloodTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BloodTest::getDonorId, donorId);
        wrapper.eq(BloodTest::getBloodStatus, "不合格");
        int unqualifiedCount = (int) count(wrapper);
        
        Donor donor = donorService.getById(donorId);
        if (donor != null && unqualifiedCount >= ATTENTION_THRESHOLD) {
            donor.setAttentionFlag(1);
            donorService.updateById(donor);
        }
    }

    @Override
    public List<BloodTest> listPendingJudge() {
        return baseMapper.selectPendingJudgeList();
    }

    @Override
    public BloodTest getByCollectionId(Long collectionId) {
        return baseMapper.selectByCollectionId(collectionId);
    }

    @Override
    public Result<List<BloodTest>> listTestRecords(Long donorId, String bloodStatus) {
        LambdaQueryWrapper<BloodTest> wrapper = new LambdaQueryWrapper<>();
        if (donorId != null) {
            wrapper.eq(BloodTest::getDonorId, donorId);
        }
        if (bloodStatus != null && !bloodStatus.trim().isEmpty()) {
            wrapper.eq(BloodTest::getBloodStatus, bloodStatus);
        }
        wrapper.orderByDesc(BloodTest::getCreateTime);
        List<BloodTest> list = list(wrapper);
        return Result.success(list);
    }
}