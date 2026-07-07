package com.sdut.blood.domain.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PendingStockOutVO {

    private Long stockId;

    private Long collectionId;

    private Long donorId;

    private String donorName;

    private String bloodType;

    private Integer bloodAmount;

    private LocalDate expireDate;

    private String status;

    private LocalDateTime createTime;

    private String donateType;
}