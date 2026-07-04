package com.sdut.blood.domain.vo;

import lombok.Data;
import java.time.LocalDate;

/**
 * 库存预警视图
 */
@Data
public class StockWarningVO {

    private String bloodType;
    
    private Integer currentStock;
    
    private Integer alertThreshold;
    
    private Integer shortageAmount;
    
    private Integer expiringCount;
    
    private Integer expiredCount;
    
    private String level;
}