package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.DateUtil;
import com.sdut.blood.common.utils.EncryptUtil;
import com.sdut.blood.common.utils.IdCardUtil;
import com.sdut.blood.common.utils.PhoneUtil;
import com.sdut.blood.domain.dto.DonorAddDTO;
import com.sdut.blood.domain.dto.DonorUpdateDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.DonorVO;
import com.sdut.blood.mapper.DonorMapper;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.DonorService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonorServiceImpl extends ServiceImpl<DonorMapper, Donor> implements DonorService {

    @Resource
    @Lazy
    private BloodCollectionService bloodCollectionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDonor(DonorAddDTO dto) {
        // 1. 格式校验
        if (!IdCardUtil.isValid(dto.getIdCard())) {
            throw new BusinessException("身份证号格式错误，请核对后填写");
        }
        if (!PhoneUtil.isValid(dto.getPhone())) {
            throw new BusinessException("联系电话格式错误，请核对后填写");
        }

        // 2. 唯一性校验
        Donor existDonor = getByIdCard(dto.getIdCard());
        if (existDonor != null) {
            throw new BusinessException("该献血者档案已存在，请勿重复录入");
        }

        // 3. 敏感数据加密
        Donor donor = new Donor();
        BeanUtils.copyProperties(dto, donor);
        donor.setIdCard(EncryptUtil.encrypt(dto.getIdCard()));
        if (dto.getMedicalHistory() != null && !dto.getMedicalHistory().isEmpty()) {
            donor.setMedicalHistory(EncryptUtil.encrypt(dto.getMedicalHistory()));
        }
        donor.setDonorStatus("正常");
        donor.setDeleted(0);

        // 4. 保存档案
        save(donor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDonor(DonorUpdateDTO dto) {
        // 1. 校验档案是否存在
        Donor donor = getById(dto.getId());
        if (donor == null) {
            throw new BusinessException("献血者档案不存在");
        }

        // 2. 格式校验
        if (!PhoneUtil.isValid(dto.getPhone())) {
            throw new BusinessException("联系电话格式错误，请核对后填写");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("姓名不可为空");
        }
        if (dto.getIdCard() != null) {
            if (!IdCardUtil.isValid(dto.getIdCard())) {
                throw new BusinessException("身份证号格式错误，请核对后填写");
            }
            Donor existDonor = getByIdCard(dto.getIdCard());
            if (existDonor != null && !existDonor.getId().equals(donor.getId())) {
                throw new BusinessException("该献血者档案已存在，请勿重复录入");
            }
        }

        // 3. 更新信息
        donor.setName(dto.getName());
        donor.setPhone(dto.getPhone());
        if (dto.getBloodType() != null) {
            donor.setBloodType(dto.getBloodType());
        }
        if (dto.getMedicalHistory() != null) {
            donor.setMedicalHistory(EncryptUtil.encrypt(dto.getMedicalHistory()));
        }
        if (dto.getDonorStatus() != null) {
            donor.setDonorStatus(dto.getDonorStatus());
        }
        if (dto.getIdCard() != null) {
            donor.setIdCard(EncryptUtil.encrypt(dto.getIdCard()));
        }
        if (dto.getGender() != null) {
            donor.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            donor.setAge(dto.getAge());
        }
        if (dto.getAddress() != null) {
            donor.setAddress(dto.getAddress());
        }

        updateById(donor);
    }

    @Override
    public Donor getByIdCard(String idCard) {
        // 先加密再查询（数据库存的是密文）
        String encryptIdCard = EncryptUtil.encrypt(idCard);
        return baseMapper.selectByIdCard(encryptIdCard);
    }

    @Override
    public Donor getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public boolean checkDonateQualification(Long donorId, String donateType) {
        Donor donor = getById(donorId);
        if (donor == null) {
            return false;
        }
        // 校验献血者状态
        if (!"正常".equals(donor.getDonorStatus())) {
            return false;
        }
        // 校验献血间隔
        if ("全血".equals(donateType)) {
            return DateUtil.checkWholeBloodInterval(donor.getLastDonateDate());
        } else if ("成分血".equals(donateType)) {
            return DateUtil.checkComponentBloodInterval(donor.getLastDonateDate());
        }
        return true;
    }

    @Override
    public DonorVO convertToVO(Donor donor) {
        DonorVO vo = new DonorVO();
        BeanUtils.copyProperties(donor, vo);
        if (donor.getIdCard() != null && !donor.getIdCard().isEmpty()) {
            try {
                String idCard = EncryptUtil.decrypt(donor.getIdCard());
                if (idCard != null && idCard.length() >= 18) {
                    vo.setIdCardMask(idCard.substring(0, 6) + "********" + idCard.substring(14));
                }
            } catch (Exception e) {
                vo.setIdCardMask("**********");
            }
        }
        
        DonateStats stats = getDonateStats(donor.getId());
        vo.setDonateCount(stats.getDonateCount());
        vo.setTotalDonateAmount(stats.getTotalAmount());
        
        List<BloodCollection> records = getDonateRecords(donor.getId());
        vo.setDonateRecords(records.stream().map(r -> {
            DonorVO.DonateRecord dr = new DonorVO.DonateRecord();
            dr.setId(r.getId());
            dr.setDonateType(r.getDonateType());
            dr.setDonateAmount(r.getDonateAmount());
            dr.setInitialScreenResult(r.getInitialScreenResult());
            if (r.getCollectionTime() != null) {
                dr.setDonateDate(r.getCollectionTime().toLocalDate());
            }
            return dr;
        }).collect(Collectors.toList()));
        
        return vo;
    }

    @Override
    public List<BloodCollection> getDonateRecords(Long donorId) {
        LambdaQueryWrapper<BloodCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BloodCollection::getDonorId, donorId);
        wrapper.orderByDesc(BloodCollection::getCollectionTime);
        return bloodCollectionService.list(wrapper);
    }

    @Override
    public DonateStats getDonateStats(Long donorId) {
        DonateStats stats = new DonateStats();
        List<BloodCollection> records = getDonateRecords(donorId);
        stats.setDonateCount(records.size());
        stats.setTotalAmount(records.stream()
                .mapToInt(BloodCollection::getDonateAmount)
                .sum());
        return stats;
    }
}
