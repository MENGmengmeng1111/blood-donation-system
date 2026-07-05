package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.domain.dto.CollectionAddDTO;
import com.sdut.blood.domain.dto.CollectionUpdateDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import java.util.List;

/**
 * 采血记录服务接口
 */
public interface BloodCollectionService extends IService<BloodCollection> {

    /**
     * 新增采血记录（UC25）
     */
    void addCollectionRecord(CollectionAddDTO dto);

    /**
     * 根据献血者ID查询历史采血记录
     */
    List<BloodCollection> listByDonorId(Long donorId);

    /**
     * 修改采血记录
     */
    void updateCollectionRecord(CollectionUpdateDTO dto);
}