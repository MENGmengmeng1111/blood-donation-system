package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.service.BloodActivityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * 献血活动控制器
 */
@RestController
@RequestMapping("/api/activity")
public class BloodActivityController {

    @Resource
    private BloodActivityService bloodActivityService;

    /**
     * 新增献血活动
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public Result<Void> addActivity(@RequestBody BloodActivity activity) {
        activity.setStatus("未开始");
        activity.setMorningRemaining(activity.getMorningQuota());
        activity.setAfternoonRemaining(activity.getAfternoonQuota());
        bloodActivityService.save(activity);
        return Result.success();
    }

    /**
     * 修改献血活动
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public Result<Void> updateActivity(@RequestBody BloodActivity activity) {
        BloodActivity existing = bloodActivityService.getById(activity.getId());
        if (existing != null) {
            int morningDiff = activity.getMorningQuota() - existing.getMorningQuota();
            int afternoonDiff = activity.getAfternoonQuota() - existing.getAfternoonQuota();
            activity.setMorningRemaining(existing.getMorningRemaining() + morningDiff);
            activity.setAfternoonRemaining(existing.getAfternoonRemaining() + afternoonDiff);
        }
        bloodActivityService.updateById(activity);
        return Result.success();
    }

    /**
     * 删除献血活动（逻辑删除）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        bloodActivityService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询活动详情
     */
    @GetMapping("/{id}")
    public Result<BloodActivity> getActivityById(@PathVariable Long id) {
        BloodActivity activity = bloodActivityService.getById(id);
        return Result.success(activity);
    }

    /**
     * 分页查询活动列表
     */
    @GetMapping("/list")
    public Result<Page<BloodActivity>> listActivities(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Page<BloodActivity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BloodActivity> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(BloodActivity::getStatus, status);
        }
        wrapper.orderByDesc(BloodActivity::getActivityDate);
        bloodActivityService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 查询所有活动（不分页）
     */
    @GetMapping("/all")
    public Result<List<BloodActivity>> getAllActivities() {
        List<BloodActivity> list = bloodActivityService.list();
        return Result.success(list);
    }

    /**
     * 查询可预约的活动列表
     */
    @GetMapping("/available")
    public Result<List<BloodActivity>> listAvailableActivities() {
        List<BloodActivity> list = bloodActivityService.listAvailableActivities();
        return Result.success(list);
    }

    /**
     * 更新活动状态
     */
    @PutMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public Result<Void> updateActivityStatus(@PathVariable Long id, @RequestParam String status) {
        BloodActivity activity = bloodActivityService.getById(id);
        if (activity != null) {
            activity.setStatus(status);
            bloodActivityService.updateById(activity);
        }
        return Result.success();
    }

    /**
     * 查询今日活动
     */
    @GetMapping("/today")
    public Result<List<BloodActivity>> getTodayActivities() {
        LambdaQueryWrapper<BloodActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BloodActivity::getActivityDate, LocalDate.now());
        wrapper.eq(BloodActivity::getStatus, "进行中");
        List<BloodActivity> list = bloodActivityService.list(wrapper);
        return Result.success(list);
    }
}
