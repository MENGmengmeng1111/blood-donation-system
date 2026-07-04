package com.sdut.blood.domain.vo;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 献血者档案视图（身份证号脱敏）
 */
@Data
public class DonorVO {

    private Long id;

    private String name;

    /**
     * 脱敏后的身份证号
     */
    private String idCardMask;

    private String bloodType;

    private String phone;

    private String donorStatus;

    private LocalDate lastDonateDate;

    private String gender;

    private Integer age;

    private String address;

    /**
     * 历史献血次数
     */
    private Integer donateCount;

    /**
     * 累计献血量（ml）
     */
    private Integer totalDonateAmount;

    /**
     * 历史献血记录列表
     */
    private List<DonateRecord> donateRecords;

    @Data
    public static class DonateRecord {
        private Long id;
        private String donateType;
        private Integer donateAmount;
        private String initialScreenResult;
        private LocalDate donateDate;
    }
}