package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.BloodActivity;
import com.sdut.blood.domain.vo.RecruitmentVO;
import com.sdut.blood.service.BloodActivityService;
import com.sdut.blood.service.OperationLogService;
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

    @Resource
    private OperationLogService operationLogService;

    /**
     * 新增献血活动
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Void> addActivity(@RequestBody BloodActivity activity) {
        if (activity.getStatus() == null || activity.getStatus().trim().isEmpty()) {
            activity.setStatus("未开始");
        }
        activity.setMorningRemaining(activity.getMorningQuota());
        activity.setAfternoonRemaining(activity.getAfternoonQuota());
        bloodActivityService.save(activity);
        operationLogService.saveLog("新增活动", "新增献血活动，名称：" + activity.getActivityName());
        return Result.success();
    }

    /**
     * 修改献血活动
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Void> updateActivity(@RequestBody BloodActivity activity) {
        BloodActivity existing = bloodActivityService.getById(activity.getId());
        if (existing != null) {
            int morningDiff = activity.getMorningQuota() - existing.getMorningQuota();
            int afternoonDiff = activity.getAfternoonQuota() - existing.getAfternoonQuota();
            activity.setMorningRemaining(existing.getMorningRemaining() + morningDiff);
            activity.setAfternoonRemaining(existing.getAfternoonRemaining() + afternoonDiff);
        }
        bloodActivityService.updateById(activity);
        operationLogService.saveLog("修改活动", "修改献血活动，ID：" + activity.getId() + "，名称：" + activity.getActivityName());
        return Result.success();
    }

    /**
     * 删除献血活动（逻辑删除）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        bloodActivityService.removeById(id);
        operationLogService.saveLog("删除活动", "删除献血活动，ID：" + id);
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        Page<BloodActivity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BloodActivity> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(BloodActivity::getActivityName, keyword.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(BloodActivity::getStatus, status);
        }
        if (sortField != null && !sortField.trim().isEmpty()) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            if ("activityName".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodActivity::getActivityName);
                } else {
                    wrapper.orderByDesc(BloodActivity::getActivityName);
                }
            } else if ("activityDate".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodActivity::getActivityDate);
                } else {
                    wrapper.orderByDesc(BloodActivity::getActivityDate);
                }
            } else if ("status".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodActivity::getStatus);
                } else {
                    wrapper.orderByDesc(BloodActivity::getStatus);
                }
            } else if ("morningQuota".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodActivity::getMorningQuota);
                } else {
                    wrapper.orderByDesc(BloodActivity::getMorningQuota);
                }
            } else if ("afternoonQuota".equals(sortField.trim())) {
                if (isAsc) {
                    wrapper.orderByAsc(BloodActivity::getAfternoonQuota);
                } else {
                    wrapper.orderByDesc(BloodActivity::getAfternoonQuota);
                }
            } else {
                wrapper.orderByDesc(BloodActivity::getActivityDate);
            }
        } else {
            wrapper.orderByDesc(BloodActivity::getActivityDate);
        }
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
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

    /**
     * 智能生成招募名单（UC23）
     */
    @GetMapping("/recruitment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<List<RecruitmentVO>> generateRecruitmentList(
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String bloodType) {
        List<RecruitmentVO> list = bloodActivityService.generateRecruitmentList(activityId, bloodType);
        return Result.success(list);
    }
}
