package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.BloodTestJudgeDTO;
import com.sdut.blood.domain.entity.BloodTest;
import java.util.List;

/**
 * 血液检验服务接口
 */
public interface BloodTestService extends IService<BloodTest> {

    /**
     * 判定血液合格状态（UC29）
     */
    void judgeBloodStatus(BloodTestJudgeDTO dto);

    /**
     * 查询待判定的检验记录
     */
    List<BloodTest> listPendingJudge();

    /**
     * 根据采血记录ID查询检验记录
     */
    BloodTest getByCollectionId(Long collectionId);

    /**
     * 查询检验记录列表（UC28）
     */
    Result<List<BloodTest>> listTestRecords(Long donorId, String bloodStatus);
}