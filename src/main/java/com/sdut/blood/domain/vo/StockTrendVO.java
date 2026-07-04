package com.sdut.blood.domain.vo;

import lombok.Data;

/**
 * 库存趋势视图
 */
@Data
public class StockTrendVO {

    private String date;
    
    private String bloodType;
    
    private Integer stockAmount;
    
    private Integer inAmount;
    
    private Integer outAmount;
    
    private Integer changeAmount;
}