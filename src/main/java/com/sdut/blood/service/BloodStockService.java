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
    Result<List<BloodStock>> listStockDetails(String bloodType, String status, String sortField, String sortOrder);

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
    void stockOut(Long id, String outUnit, String outPurpose);

    /**
     * 修改库存信息
     */
    void updateStock(BloodStock bloodStock);

    /**
     * 查询待入库记录列表（检验合格但未入库的采血记录）
     */
    java.util.List<com.sdut.blood.domain.vo.PendingStockInVO> listPendingStockIn(String keyword, String bloodType);

    /**
     * 查询可出库库存记录列表（状态为正常/临期/已过期的库存）
     */
    java.util.List<com.sdut.blood.domain.vo.PendingStockOutVO> listStockOutPending(String keyword, String bloodType);

    /**
     * 查询入库操作历史（所有已入库的记录）
     */
    java.util.List<com.sdut.blood.domain.vo.StockHistoryVO> listStockInHistory();

    /**
     * 查询出库操作历史（已出库的记录）
     */
    java.util.List<com.sdut.blood.domain.vo.StockHistoryVO> listStockOutHistory();
}