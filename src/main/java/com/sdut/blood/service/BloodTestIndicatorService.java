package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.BloodTestIndicator;

import java.util.List;

public interface BloodTestIndicatorService extends IService<BloodTestIndicator> {

    BloodTestIndicator getByTestId(Long testId);

    Result<Void> saveIndicator(BloodTestIndicator indicator);

    Result<List<BloodTestIndicator>> listPendingTestList();

    Result<List<BloodTestIndicator>> listIndicatorHistory();

    Result<Boolean> judgeByIndicators(Long testId);
}