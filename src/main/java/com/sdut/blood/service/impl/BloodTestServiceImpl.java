package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.constants.BloodConstants;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.BloodTestJudgeDTO;
import com.sdut.blood.domain.dto.BloodTestUpdateDTO;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.mapper.BloodTestMapper;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.domain.entity.Donor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
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
        if (BloodConstants.STATUS_STORED.equals(test.getBloodStatus())) {
            throw new BusinessException("血液已入库，无法修改判定状态");
        }

        String recheckResult = trimToNull(dto.getRecheckResult());
        if (recheckResult != null
                && !BloodConstants.STATUS_QUALIFIED.equals(recheckResult)
                && BloodConstants.STATUS_QUALIFIED.equals(dto.getBloodStatus())) {
            throw new BusinessException("复检结果不是合格时，血液状态只能判定为不合格");
        }

        // 3. 不合格必须填写原因
        if (BloodConstants.STATUS_UNQUALIFIED.equals(dto.getBloodStatus())) {
            if (dto.getUnqualifiedReason() == null || dto.getUnqualifiedReason().trim().isEmpty()) {
                throw new BusinessException("不合格血液请填写具体原因");
            }
            test.setUnqualifiedReason(dto.getUnqualifiedReason());
            test.setBloodStatus(BloodConstants.STATUS_UNQUALIFIED);
        } else if (BloodConstants.STATUS_QUALIFIED.equals(dto.getBloodStatus())) {
            test.setBloodStatus(BloodConstants.STATUS_QUALIFIED);
        } else {
            throw new BusinessException("请选择血液判定状态");
        }

        if (recheckResult != null) {
            test.setRecheckResult(recheckResult);
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
        
        checkAndMarkAttention(test.getDonorId());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    
    private void checkAndMarkAttention(Long donorId) {
        if (donorId == null) {
            return;
        }
        
        LambdaQueryWrapper<BloodTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BloodTest::getDonorId, donorId)
                .isNotNull(BloodTest::getRecheckResult)
                .ne(BloodTest::getRecheckResult, "")
                .ne(BloodTest::getRecheckResult, BloodConstants.STATUS_QUALIFIED);
        int abnormalRecheckCount = (int) count(wrapper);
        
        Donor donor = donorService.getById(donorId);
        if (donor != null && abnormalRecheckCount >= ATTENTION_THRESHOLD) {
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
    public Result<List<BloodTest>> listTestRecords(Long donorId, String bloodStatus, String sortField, String sortOrder) {
        LambdaQueryWrapper<BloodTest> wrapper = new LambdaQueryWrapper<>();
        if (donorId != null) {
            wrapper.eq(BloodTest::getDonorId, donorId);
        }
        if (bloodStatus != null && !bloodStatus.trim().isEmpty()) {
            wrapper.eq(BloodTest::getBloodStatus, bloodStatus);
        }
        if (sortField != null && !sortField.trim().isEmpty()) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            if ("recheckResult".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodTest::getRecheckResult);
                } else {
                    wrapper.orderByDesc(BloodTest::getRecheckResult);
                }
            } else if ("bloodStatus".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodTest::getBloodStatus);
                } else {
                    wrapper.orderByDesc(BloodTest::getBloodStatus);
                }
            } else if ("judgeTime".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodTest::getJudgeTime);
                } else {
                    wrapper.orderByDesc(BloodTest::getJudgeTime);
                }
            } else {
                wrapper.orderByDesc(BloodTest::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(BloodTest::getCreateTime);
        }
        List<BloodTest> list = list(wrapper);
        return Result.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTestRecord(BloodTestUpdateDTO dto) {
        BloodTest test = getById(dto.getId());
        if (test == null) {
            throw new BusinessException("检验记录不存在或已删除");
        }
        if (BloodConstants.STATUS_STORED.equals(test.getBloodStatus())) {
            throw new BusinessException("血液已入库，无法修改检验记录");
        }

        if (dto.getRecheckResult() != null) {
            test.setRecheckResult(dto.getRecheckResult());
        }
        if (dto.getUnqualifiedReason() != null) {
            test.setUnqualifiedReason(dto.getUnqualifiedReason());
        }
        if (dto.getRemark() != null) {
            test.setRemark(dto.getRemark());
        }
        updateById(test);
        checkAndMarkAttention(test.getDonorId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(BloodTest entity) {
        if (entity != null && entity.getId() != null) {
            BloodTest existing = getById(entity.getId());
            if (existing != null && BloodConstants.STATUS_STORED.equals(existing.getBloodStatus())) {
                throw new BusinessException("血液已入库，无法修改检验记录");
            }
        }
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        BloodTest test = getById(id);
        if (test == null) {
            return false;
        }
        if (BloodConstants.STATUS_STORED.equals(test.getBloodStatus())) {
            throw new BusinessException("血液已入库，无法删除检验记录");
        }
        return super.removeById(id);
    }
}
