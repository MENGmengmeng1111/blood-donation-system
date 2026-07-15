package com.sdut.blood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdut.blood.domain.entity.BloodTestIndicator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BloodTestIndicatorMapper extends BaseMapper<BloodTestIndicator> {

    @Select("SELECT * FROM blood_test_indicator WHERE test_id = #{testId} AND deleted = 0")
    BloodTestIndicator selectByTestId(@Param("testId") Long testId);

    @Select("SELECT bt.id as test_id, bt.collection_id, bt.donor_id, bt.blood_status, bt.batch_no, " +
            "d.name as donor_name, d.gender as donor_gender, d.blood_type as donor_blood_type, d.age as donor_age, " +
            "bc.donate_amount, bc.donate_type, bc.collection_time, " +
            "ti.id as indicator_id, ti.alt, ti.hbv_surface_antigen, ti.hcv_antibody, " +
            "ti.hiv_antibody, ti.syphilis_antibody, ti.white_blood_cell, ti.hemoglobin, ti.platelet, ti.other_abnormality " +
            "FROM blood_test bt " +
            "LEFT JOIN donor d ON bt.donor_id = d.id " +
            "LEFT JOIN blood_collection bc ON bt.collection_id = bc.id " +
            "LEFT JOIN blood_test_indicator ti ON bt.id = ti.test_id " +
            "WHERE bt.blood_status = '待检验' AND bt.deleted = 0 " +
            "ORDER BY bt.create_time ASC")
    List<BloodTestIndicator> selectPendingTestList();

    @Select("SELECT bt.id as test_id, bt.collection_id, bt.donor_id, bt.blood_status, bt.batch_no, bt.recheck_result, bt.unqualified_reason, " +
            "d.name as donor_name, d.gender as donor_gender, d.blood_type as donor_blood_type, d.age as donor_age, " +
            "bc.donate_amount, bc.donate_type, bc.collection_time, " +
            "ti.id as indicator_id, ti.alt, ti.hbv_surface_antigen, ti.hcv_antibody, " +
            "ti.hiv_antibody, ti.syphilis_antibody, ti.white_blood_cell, ti.hemoglobin, ti.platelet, ti.other_abnormality, ti.update_time " +
            "FROM blood_test_indicator ti " +
            "LEFT JOIN blood_test bt ON ti.test_id = bt.id " +
            "LEFT JOIN donor d ON bt.donor_id = d.id " +
            "LEFT JOIN blood_collection bc ON bt.collection_id = bc.id " +
            "WHERE ti.deleted = 0 AND bt.deleted = 0 " +
            "ORDER BY ti.update_time DESC")
    List<BloodTestIndicator> selectIndicatorHistory();
}