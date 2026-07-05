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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonorServiceImpl extends ServiceImpl<DonorMapper, Donor> implements DonorService {

    private static final List<String> VALID_BLOOD_TYPES = Arrays.asList("A型", "B型", "O型", "AB型");

    private static final List<String> VALID_GENDERS = Arrays.asList("男", "女");

    @Resource
    @Lazy
    private BloodCollectionService bloodCollectionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDonor(DonorAddDTO dto) {
        String name = normalizeBlank(dto.getName());
        String idCard = normalizeBlank(dto.getIdCard());
        String phone = normalizeBlank(dto.getPhone());
        String bloodType = normalizeBlank(dto.getBloodType());
        String gender = normalizeBlank(dto.getGender());
        if (name == null) {
            throw new BusinessException("姓名不可为空");
        }
        // 1. 格式校验
        if (!IdCardUtil.isValid(idCard)) {
            throw new BusinessException("身份证号格式错误，请核对后填写");
        }
        if (!PhoneUtil.isValid(phone)) {
            throw new BusinessException("联系电话格式错误，请核对后填写");
        }
        validateBloodType(bloodType);
        validateGender(gender);

        // 2. 唯一性校验
        Donor existDonor = getByIdCard(idCard);
        if (existDonor != null) {
            throw new BusinessException("该献血者档案已存在，请勿重复录入");
        }

        // 3. 敏感数据加密
        Donor donor = new Donor();
        BeanUtils.copyProperties(dto, donor);
        donor.setName(name);
        donor.setPhone(phone);
        donor.setBloodType(bloodType);
        donor.setGender(gender);
        donor.setIdCard(EncryptUtil.encrypt(idCard));
        donor.setAge(IdCardUtil.getAge(idCard));
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
        String name = normalizeBlank(dto.getName());
        String phone = normalizeBlank(dto.getPhone());
        String bloodType = normalizeBlank(dto.getBloodType());
        String gender = normalizeBlank(dto.getGender());
        if (name == null) {
            throw new BusinessException("姓名不可为空");
        }
        if (!PhoneUtil.isValid(phone)) {
            throw new BusinessException("联系电话格式错误，请核对后填写");
        }
        String idCard = normalizeBlank(dto.getIdCard());
        if (idCard != null) {
            if (!IdCardUtil.isValid(idCard)) {
                throw new BusinessException("身份证号格式错误，请核对后填写");
            }
            Donor existDonor = getByIdCard(idCard);
            if (existDonor != null && !existDonor.getId().equals(donor.getId())) {
                throw new BusinessException("该献血者档案已存在，请勿重复录入");
            }
        }
        if (bloodType != null) {
            validateBloodType(bloodType);
        }
        validateGender(gender);

        // 3. 更新信息
        donor.setName(name);
        donor.setPhone(phone);
        if (bloodType != null) {
            donor.setBloodType(bloodType);
        }
        if (dto.getMedicalHistory() != null) {
            donor.setMedicalHistory(EncryptUtil.encrypt(dto.getMedicalHistory()));
        }
        if (dto.getDonorStatus() != null) {
            donor.setDonorStatus(dto.getDonorStatus());
        }
        if (idCard != null) {
            donor.setIdCard(EncryptUtil.encrypt(idCard));
            donor.setAge(IdCardUtil.getAge(idCard));
        }
        if (gender != null) {
            donor.setGender(gender);
        }
        if (dto.getAddress() != null) {
            donor.setAddress(dto.getAddress());
        }

        updateById(donor);
    }

    @Override
    public Donor getByIdCard(String idCard) {
        // 先加密再查询（数据库存的是密文）
        String normalizedIdCard = normalizeBlank(idCard);
        if (normalizedIdCard == null) {
            return null;
        }
        String encryptIdCard = EncryptUtil.encrypt(normalizedIdCard);
        return baseMapper.selectByIdCard(encryptIdCard);
    }

    private void validateBloodType(String bloodType) {
        if (bloodType == null || !VALID_BLOOD_TYPES.contains(bloodType)) {
            throw new BusinessException("血型参数错误，请选择A型、B型、O型或AB型");
        }
    }

    private void validateGender(String gender) {
        if (gender != null && !gender.trim().isEmpty() && !VALID_GENDERS.contains(gender)) {
            throw new BusinessException("性别参数错误，请选择男或女");
        }
    }

    private String normalizeBlank(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
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
