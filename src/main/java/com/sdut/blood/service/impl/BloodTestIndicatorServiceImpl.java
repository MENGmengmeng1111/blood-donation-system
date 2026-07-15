package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.constants.BloodConstants;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.BloodTestIndicator;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.mapper.BloodTestIndicatorMapper;
import com.sdut.blood.service.BloodTestIndicatorService;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BloodTestIndicatorServiceImpl extends ServiceImpl<BloodTestIndicatorMapper, BloodTestIndicator>
        implements BloodTestIndicatorService {

    @Resource
    private BloodTestService bloodTestService;

    @Resource
    @Lazy
    private DonorService donorService;

    @Override
    public BloodTestIndicator getByTestId(Long testId) {
        return baseMapper.selectByTestId(testId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveIndicator(BloodTestIndicator indicator) {
        BloodTest test = bloodTestService.getById(indicator.getTestId());
        if (test == null) {
            return Result.error("检验记录不存在");
        }

        BloodTestIndicator existing = getByTestId(indicator.getTestId());
        if (existing != null) {
            indicator.setId(existing.getId());
        }

        saveOrUpdate(indicator);

        judgeByIndicators(indicator.getTestId());

        return Result.success();
    }

    @Override
    public Result<List<BloodTestIndicator>> listPendingTestList() {
        List<BloodTestIndicator> list = baseMapper.selectPendingTestList();
        return Result.success(list);
    }

    @Override
    public Result<List<BloodTestIndicator>> listIndicatorHistory() {
        List<BloodTestIndicator> list = baseMapper.selectIndicatorHistory();
        return Result.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> judgeByIndicators(Long testId) {
        BloodTestIndicator indicator = getByTestId(testId);
        if (indicator == null) {
            return Result.error("指标记录不存在");
        }

        BloodTest test = bloodTestService.getById(testId);
        if (test == null) {
            return Result.error("检验记录不存在");
        }

        Donor donor = donorService.getById(test.getDonorId());
        String gender = donor != null ? donor.getGender() : "男";

        List<String> unqualifiedReasons = new ArrayList<>();
        List<String> recheckResultList = new ArrayList<>();

        if (indicator.getAlt() != null && indicator.getAlt() > 40.0) {
            unqualifiedReasons.add("谷丙转氨酶（ALT）超标(" + indicator.getAlt() + "U/L)");
            recheckResultList.add("谷丙转氨酶（ALT）超标");
        }

        boolean hasInfectiousDisease = false;
        if ("阳性".equals(indicator.getHivAntibody())) {
            unqualifiedReasons.add("艾滋病抗体阳性");
            hasInfectiousDisease = true;
        }
        if ("阳性".equals(indicator.getHcvAntibody())) {
            unqualifiedReasons.add("丙肝抗体阳性");
            hasInfectiousDisease = true;
        }
        if ("阳性".equals(indicator.getHbvSurfaceAntigen())) {
            unqualifiedReasons.add("乙肝表面抗原阳性");
            hasInfectiousDisease = true;
        }
        if ("阳性".equals(indicator.getSyphilisAntibody())) {
            unqualifiedReasons.add("梅毒抗体阳性");
            hasInfectiousDisease = true;
        }
        if (hasInfectiousDisease) {
            recheckResultList.add("血液传染病指标异常");
        }

        if (indicator.getWhiteBloodCell() != null) {
            if (indicator.getWhiteBloodCell() < 3.5 || indicator.getWhiteBloodCell() > 9.5) {
                unqualifiedReasons.add("白细胞计数异常(" + indicator.getWhiteBloodCell() + "×10^9/L)");
                recheckResultList.add("白细胞常规指标不合格");
            }
        }

        if (indicator.getHemoglobin() != null) {
            double minHb = "女".equals(gender) ? 115.0 : 120.0;
            if (indicator.getHemoglobin() < minHb) {
                unqualifiedReasons.add("血红蛋白低于标准(" + indicator.getHemoglobin() + "g/L)");
                recheckResultList.add("血红蛋白常规指标不合格");
            }
        }

        if (indicator.getPlatelet() != null) {
            if (indicator.getPlatelet() < 125.0 || indicator.getPlatelet() > 350.0) {
                unqualifiedReasons.add("血小板计数异常(" + indicator.getPlatelet() + "×10^9/L)");
                recheckResultList.add("血小板计数不合格");
            }
        }

        if (indicator.getOtherAbnormality() != null && !indicator.getOtherAbnormality().trim().isEmpty()) {
            unqualifiedReasons.add("其他：" + indicator.getOtherAbnormality());
            recheckResultList.add("其他");
        }

        if (unqualifiedReasons.isEmpty()) {
            test.setBloodStatus(BloodConstants.STATUS_QUALIFIED);
            test.setUnqualifiedReason(null);
            test.setRecheckResult("合格");
        } else {
            test.setBloodStatus(BloodConstants.STATUS_UNQUALIFIED);
            test.setUnqualifiedReason(String.join("；", unqualifiedReasons));
            test.setRecheckResult(String.join("；", recheckResultList));
        }
        test.setJudgeTime(LocalDateTime.now());
        bloodTestService.updateById(test);

        return Result.success(unqualifiedReasons.isEmpty());
    }
}