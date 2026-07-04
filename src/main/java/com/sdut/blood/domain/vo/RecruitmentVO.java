package com.sdut.blood.domain.vo;

import lombok.Data;
import java.time.LocalDate;

/**
 * 招募名单视图
 */
@Data
public class RecruitmentVO {

    private Long donorId;
    
    private String name;
    
    private String idCardMask;
    
    private String bloodType;
    
    private String phone;
    
    private String gender;
    
    private Integer age;
    
    private String donorStatus;
    
    private LocalDate lastDonateDate;
    
    private Integer totalDonateAmount;
    
    private Integer donateCount;
    
    private String reason;
}