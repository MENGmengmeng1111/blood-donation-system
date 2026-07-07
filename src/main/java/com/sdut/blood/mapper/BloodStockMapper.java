package com.sdut.blood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdut.blood.domain.entity.BloodStock;
import java.util.List;
import java.util.Map;

/**
 * 血液库存 Mapper
 */
public interface BloodStockMapper extends BaseMapper<BloodStock> {

    /**
     * 按血型统计库存总量
     */
    Map<String, Object> selectStockTotalByBloodType(String bloodType);

    /**
     * 查询指定天数内临期的库存记录
     */
    List<BloodStock> selectNearExpireList(Integer days);

    /**
     * 按日期统计入库量
     */
    List<Map<String, Object>> selectDailyStockIn(Integer days);

    /**
     * 按日期统计出库量
     */
    List<Map<String, Object>> selectDailyStockOut(Integer days);

    /**
     * 查询待入库记录列表（检验合格但未入库的采血记录）
     */
    List<com.sdut.blood.domain.vo.PendingStockInVO> selectPendingStockInList();

    /**
     * 查询可出库库存记录列表（状态为正常/临期/已过期的库存）
     */
    List<com.sdut.blood.domain.vo.PendingStockOutVO> selectStockOutPendingList();
}