package com.sdut.blood.domain.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StockHistoryVO {

    private Long stockId;

    private Long collectionId;

    private Long donorId;

    private String donorName;

    private String bloodType;

    private Integer bloodAmount;

    private String donateType;

    private LocalDate expireDate;

    private String outUnit;

    private String outPurpose;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}