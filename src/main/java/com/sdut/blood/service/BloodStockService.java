package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.StockInDTO;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.vo.BloodStockVO;
import com.sdut.blood.domain.vo.StockTrendVO;
import com.sdut.blood.domain.vo.StockWarningVO;
import java.util.List;

/**
 * 血液库存服务接口
 */
public interface BloodStockService extends IService<BloodStock> {

    /**
     * 登记血液入库（UC32）
     */
    void stockIn(StockInDTO dto);

    /**
     * 按血型查询库存汇总
     */
    BloodStockVO getStockSummary(String bloodType);

    /**
     * 查询临期库存列表
     */
    List<BloodStock> listNearExpire(Integer days);

    /**
     * 检查库存是否低于阈值预警
     */
    boolean checkStockWarning(String bloodType);

    /**
     * 查询库存明细列表（UC35）
     */
    Result<List<BloodStock>> listStockDetails(String bloodType, String status);

    /**
     * 查看库存预警列表（UC36）
     */
    Result<List<StockWarningVO>> listStockWarning();

    /**
     * 生成库存趋势分析（UC37）
     */
    Result<List<StockTrendVO>> getStockTrend(String bloodType);

    /**
     * 获取所有血型的库存预警详情
     */
    List<StockWarningVO> getStockWarningDetails();

    /**
     * 血液出库
     */
    void stockOut(Long id, String outUnit);

    /**
     * 修改库存信息
     */
    void updateStock(BloodStock bloodStock);
}