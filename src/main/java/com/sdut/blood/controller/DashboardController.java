package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.entity.Appointment;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.mapper.AppointmentMapper;
import com.sdut.blood.mapper.BloodActivityMapper;
import com.sdut.blood.mapper.BloodCollectionMapper;
import com.sdut.blood.mapper.BloodStockMapper;
import com.sdut.blood.mapper.BloodTestMapper;
import com.sdut.blood.mapper.DonorMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final List<String> BLOOD_TYPES = List.of("A型", "B型", "O型", "AB型");

    @Resource
    private BloodActivityMapper bloodActivityMapper;

    @Resource
    private DonorMapper donorMapper;

    @Resource
    private BloodCollectionMapper bloodCollectionMapper;

    @Resource
    private BloodStockMapper bloodStockMapper;

    @Resource
    private BloodTestMapper bloodTestMapper;

    @Resource
    private AppointmentMapper appointmentMapper;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Map<String, Object>> getAdminDashboard() {
        List<BloodActivity> activities = bloodActivityMapper.selectList(null);
        List<BloodCollection> collections = bloodCollectionMapper.selectList(null);
        List<BloodStock> stocks = bloodStockMapper.selectList(null);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("activityCount", activities.size());
        data.put("donorCount", donorMapper.selectCount(null));
        data.put("collectionTotalAmount", sumCollectionAmount(collections));
        data.put("stockTotalAmount", sumAvailableStockAmount(stocks));
        data.put("availableActivityCount", listAvailableActivities(activities).size());
        data.put("pendingTestCount", bloodTestMapper.selectCount(Wrappers.<BloodTest>lambdaQuery()
                .eq(BloodTest::getBloodStatus, "待检验")));
        data.put("nearExpireStockCount", countNearExpireStocks(stocks));
        data.put("pendingAppointmentCount", appointmentMapper.selectCount(Wrappers.<Appointment>lambdaQuery()
                .eq(Appointment::getStatus, "待参加")));
        data.put("stockByBloodType", buildStockByBloodType(stocks));
        data.put("upcomingActivities", listAvailableActivities(activities).stream().limit(5).collect(Collectors.toList()));
        return Result.success(data);
    }

    @GetMapping("/donor")
    @PreAuthorize("hasAnyAuthority('ROLE_DONOR','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Map<String, Object>> getDonorDashboard() {
        Long userId = SecurityUtil.getCurrentUserId();
        Donor donor = donorMapper.selectOne(Wrappers.<Donor>lambdaQuery().eq(Donor::getUserId, userId).last("LIMIT 1"));
        List<BloodActivity> activities = bloodActivityMapper.selectList(null);
        List<Appointment> appointments = appointmentMapper.selectList(Wrappers.<Appointment>lambdaQuery()
                .eq(Appointment::getUserId, userId)
                .orderByDesc(Appointment::getAppointmentTime));

        List<BloodCollection> collections = donor == null
                ? List.of()
                : bloodCollectionMapper.selectList(Wrappers.<BloodCollection>lambdaQuery()
                .eq(BloodCollection::getDonorId, donor.getId())
                .orderByDesc(BloodCollection::getCollectionTime));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("donorName", donor == null ? null : donor.getName());
        data.put("donorStatus", donor == null ? null : donor.getDonorStatus());
        data.put("donationCount", collections.size());
        data.put("totalAmount", sumCollectionAmount(collections));
        data.put("pendingAppointments", appointments.stream().filter(this::isPendingAppointment).count());
        data.put("lastDonateDate", donor == null ? null : donor.getLastDonateDate());
        data.put("upcomingActivities", listAvailableActivities(activities).stream().limit(5).collect(Collectors.toList()));
        data.put("recentAppointments", appointments.stream().limit(5).collect(Collectors.toList()));
        return Result.success(data);
    }

    private int sumCollectionAmount(List<BloodCollection> collections) {
        return collections.stream()
                .map(BloodCollection::getDonateAmount)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int sumAvailableStockAmount(List<BloodStock> stocks) {
        return stocks.stream()
                .filter(stock -> !"已出库".equals(stock.getStatus()))
                .map(BloodStock::getBloodAmount)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private long countNearExpireStocks(List<BloodStock> stocks) {
        LocalDate now = LocalDate.now();
        LocalDate sevenDaysLater = now.plusDays(7);
        return stocks.stream()
                .filter(stock -> !"已出库".equals(stock.getStatus()))
                .filter(stock -> stock.getExpireDate() != null)
                .filter(stock -> !stock.getExpireDate().isBefore(now) && !stock.getExpireDate().isAfter(sevenDaysLater))
                .count();
    }

    private List<Map<String, Object>> buildStockByBloodType(List<BloodStock> stocks) {
        return BLOOD_TYPES.stream().map(bloodType -> {
            List<BloodStock> typedStocks = stocks.stream()
                    .filter(stock -> bloodType.equals(stock.getBloodType()))
                    .filter(stock -> !"已出库".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("bloodType", bloodType);
            item.put("totalAmount", typedStocks.stream()
                    .map(BloodStock::getBloodAmount)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum());
            item.put("nearExpireCount", countNearExpireStocks(typedStocks));
            item.put("count", typedStocks.size());
            return item;
        }).collect(Collectors.toList());
    }

    private List<BloodActivity> listAvailableActivities(List<BloodActivity> activities) {
        LocalDate today = LocalDate.now();
        return activities.stream()
                .filter(activity -> activity.getActivityDate() != null && !activity.getActivityDate().isBefore(today))
                .filter(activity -> !"已结束".equals(activity.getStatus()))
                .filter(activity -> safeInt(activity.getMorningRemaining()) + safeInt(activity.getAfternoonRemaining()) > 0)
                .sorted(Comparator.comparing(BloodActivity::getActivityDate))
                .collect(Collectors.toList());
    }

    private boolean isPendingAppointment(Appointment appointment) {
        return "待参加".equals(appointment.getStatus());
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
