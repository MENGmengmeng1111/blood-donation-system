package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.constants.BloodConstants;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.CollectionAddDTO;
import com.sdut.blood.domain.dto.CollectionUpdateDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.mapper.BloodCollectionMapper;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BloodCollectionServiceImpl extends ServiceImpl<BloodCollectionMapper, BloodCollection> implements BloodCollectionService {

    @Resource
    private DonorService donorService;

    @Resource
    private BloodTestService bloodTestService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCollectionRecord(CollectionAddDTO dto) {
        // 1. 校验献血者档案是否存在
        Donor donor = null;
        if (dto.getDonorId() != null) {
            donor = donorService.getById(dto.getDonorId());
        } else if (dto.getIdCard() != null) {
            donor = donorService.getByIdCard(dto.getIdCard());
        }
        if (donor == null) {
            throw new BusinessException("未找到该献血者信息，请先录入档案");
        }

        // 2. 校验献血量规范
        if ("全血".equals(dto.getDonateType())) {
            if (dto.getDonateAmount() < BloodConstants.WHOLE_BLOOD_MIN
                    || dto.getDonateAmount() > BloodConstants.WHOLE_BLOOD_MAX) {
                throw new BusinessException("献血量数值不符合规范，请核对后填写");
            }
        } else if ("成分血".equals(dto.getDonateType())) {
            if (dto.getDonateAmount() < BloodConstants.COMPONENT_BLOOD_MIN
                    || dto.getDonateAmount() > BloodConstants.COMPONENT_BLOOD_MAX) {
                throw new BusinessException("献血量数值不符合规范，请核对后填写");
            }
        }

        // 3. 生成采血记录
        BloodCollection collection = new BloodCollection();
        collection.setDonorId(donor.getId());
        collection.setDonorIdCard(donor.getIdCard());
        collection.setDonateAmount(dto.getDonateAmount());
        collection.setDonateType(dto.getDonateType());
        collection.setInitialScreenResult(dto.getInitialScreenResult());
        collection.setCollectionTime(LocalDateTime.now());
        Long operatorId = SecurityUtil.getCurrentUserId();
        if (operatorId != null) {
            collection.setOperatorId(operatorId);
        }
        save(collection);

        // 4. 初筛不合格直接标记，不进入复检
        if ("不合格".equals(dto.getInitialScreenResult())) {
            BloodTest test = new BloodTest();
            test.setCollectionId(collection.getId());
            test.setDonorId(donor.getId());
            test.setBloodStatus(BloodConstants.STATUS_UNQUALIFIED);
            test.setUnqualifiedReason("初筛不合格");
            test.setJudgeTime(LocalDateTime.now());
            if (operatorId != null) {
                test.setOperatorId(operatorId);
            }
            bloodTestService.save(test);
        } else {
            // 初筛合格，生成待检验记录
            BloodTest test = new BloodTest();
            test.setCollectionId(collection.getId());
            test.setDonorId(donor.getId());
            test.setBloodStatus(BloodConstants.STATUS_PENDING_TEST);
            bloodTestService.save(test);
        }
    }

    @Override
    public List<BloodCollection> listByDonorId(Long donorId) {
        return baseMapper.selectByDonorId(donorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCollectionRecord(CollectionUpdateDTO dto) {
        BloodCollection collection = getById(dto.getId());
        if (collection == null) {
            throw new BusinessException("采血记录不存在");
        }

        if ("全血".equals(dto.getDonateType())) {
            if (dto.getDonateAmount() < BloodConstants.WHOLE_BLOOD_MIN
                    || dto.getDonateAmount() > BloodConstants.WHOLE_BLOOD_MAX) {
                throw new BusinessException("献血量数值不符合规范，请核对后填写");
            }
        } else if ("成分血".equals(dto.getDonateType())) {
            if (dto.getDonateAmount() < BloodConstants.COMPONENT_BLOOD_MIN
                    || dto.getDonateAmount() > BloodConstants.COMPONENT_BLOOD_MAX) {
                throw new BusinessException("献血量数值不符合规范，请核对后填写");
            }
        }

        collection.setDonateAmount(dto.getDonateAmount());
        collection.setDonateType(dto.getDonateType());
        collection.setInitialScreenResult(dto.getInitialScreenResult());
        updateById(collection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        BloodTest test = bloodTestService.getByCollectionId((Long) id);
        if (test != null && "已入库".equals(test.getBloodStatus())) {
            throw new BusinessException("血液已入库，无法删除采血记录");
        }
        if (test != null) {
            bloodTestService.removeById(test.getId());
        }
        return super.removeById(id);
    }
}