package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.StockInDTO;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.BloodStockVO;
import com.sdut.blood.domain.vo.StockTrendVO;
import com.sdut.blood.domain.vo.StockWarningVO;
import com.sdut.blood.mapper.BloodStockMapper;
import com.sdut.blood.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BloodStockServiceImpl extends ServiceImpl<BloodStockMapper, BloodStock> implements BloodStockService {

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private BloodTestService bloodTestService;

    @Resource
    private DonorService donorService;

    @Resource
    private StockThresholdService stockThresholdService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockInDTO dto) {
        // 1. 校验采血记录存在
        BloodCollection collection = bloodCollectionService.getById(dto.getCollectionId());
        if (collection == null) {
            throw new BusinessException("采血记录不存在");
        }

        // 2. 校验血液状态为合格
        BloodTest test = bloodTestService.getByCollectionId(dto.getCollectionId());
        if (test == null || !"合格".equals(test.getBloodStatus())) {
            throw new BusinessException("不合格血液无法入库，请核对后操作");
        }

        // 3. 校验有效期有效
        if (dto.getExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException("有效期无效，请填写正确日期");
        }

        // 4. 校验是否已入库
        BloodStock existStock = lambdaQuery()
                .eq(BloodStock::getCollectionId, dto.getCollectionId())
                .one();
        if (existStock != null) {
            throw new BusinessException("该血液已入库，请勿重复操作");
        }

        // 5. 获取血型
        Donor donor = donorService.getById(collection.getDonorId());

        // 6. 生成入库记录
        BloodStock stock = new BloodStock();
        stock.setCollectionId(dto.getCollectionId());
        stock.setBloodType(donor.getBloodType());
        stock.setBloodAmount(collection.getDonateAmount());
        stock.setExpireDate(dto.getExpireDate());
        stock.setStatus("正常");
        save(stock);

        // 7. 更新检验记录状态为已入库
        test.setBloodStatus("已入库");
        bloodTestService.updateById(test);
    }

    @Override
    public BloodStockVO getStockSummary(String bloodType) {
        Map<String, Object> map = baseMapper.selectStockTotalByBloodType(bloodType);
        BloodStockVO vo = new BloodStockVO();
        vo.setBloodType(bloodType);
        vo.setTotalAmount(map == null ? 0 : ((Number) map.get("total_amount")).intValue());
        vo.setStatus("正常");

        // 计算临期状态
        List<BloodStock> nearList = listNearExpire(7);
        vo.setNearExpire(!nearList.isEmpty());

        // 计算可用天数（简化计算）
        long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(30));
        vo.setAvailableDays(days);
        return vo;
    }

    @Override
    public List<BloodStock> listNearExpire(Integer days) {
        return baseMapper.selectNearExpireList(days);
    }

    @Override
    public boolean checkStockWarning(String bloodType) {
        BloodStockVO summary = getStockSummary(bloodType);
        Integer threshold = stockThresholdService.getThresholdByType(bloodType);
        return summary.getTotalAmount() < threshold;
    }

    @Override
    public Result<List<BloodStock>> listStockDetails(String bloodType, String status) {
        LambdaQueryWrapper<BloodStock> wrapper = new LambdaQueryWrapper<>();
        if (bloodType != null && !bloodType.trim().isEmpty()) {
            wrapper.eq(BloodStock::getBloodType, bloodType);
        }
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(BloodStock::getStatus, status);
        }
        wrapper.orderByDesc(BloodStock::getCreateTime);
        List<BloodStock> list = list(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<List<StockWarningVO>> listStockWarning() {
        List<StockWarningVO> warningList = getStockWarningDetails().stream()
                .filter(w -> "紧急".equals(w.getLevel()) || "预警".equals(w.getLevel()))
                .collect(java.util.stream.Collectors.toList());
        return Result.success(warningList);
    }

    @Override
    public Result<List<StockTrendVO>> getStockTrend(String bloodType) {
        List<StockTrendVO> trendList = new ArrayList<>();
        
        List<Map<String, Object>> inData = baseMapper.selectDailyStockIn(30);
        List<Map<String, Object>> outData = baseMapper.selectDailyStockOut(30);
        
        java.util.Map<String, java.util.Map<String, Integer>> inMap = new java.util.HashMap<>();
        java.util.Map<String, java.util.Map<String, Integer>> outMap = new java.util.HashMap<>();
        
        for (Map<String, Object> item : inData) {
            String date = item.get("date").toString();
            String type = item.get("blood_type").toString();
            Integer amount = ((Number) item.get("in_amount")).intValue();
            inMap.computeIfAbsent(date, k -> new java.util.HashMap<>()).put(type, amount);
        }
        
        for (Map<String, Object> item : outData) {
            String date = item.get("date").toString();
            String type = item.get("blood_type").toString();
            Integer amount = ((Number) item.get("out_amount")).intValue();
            outMap.computeIfAbsent(date, k -> new java.util.HashMap<>()).put(type, amount);
        }
        
        java.util.Set<String> allDates = new java.util.TreeSet<>();
        allDates.addAll(inMap.keySet());
        allDates.addAll(outMap.keySet());
        
        int currentStock = 0;
        for (String date : allDates) {
            StockTrendVO vo = new StockTrendVO();
            vo.setDate(date);
            vo.setBloodType(bloodType);
            
            Integer inAmount = inMap.getOrDefault(date, new java.util.HashMap<>()).getOrDefault(bloodType, 0);
            Integer outAmount = outMap.getOrDefault(date, new java.util.HashMap<>()).getOrDefault(bloodType, 0);
            
            vo.setInAmount(inAmount);
            vo.setOutAmount(outAmount);
            vo.setChangeAmount(inAmount - outAmount);
            
            currentStock += (inAmount - outAmount);
            vo.setStockAmount(currentStock);
            
            trendList.add(vo);
        }
        
        return Result.success(trendList);
    }

    @Override
    public List<StockWarningVO> getStockWarningDetails() {
        List<String> bloodTypes = List.of("A型", "B型", "O型", "AB型");
        List<StockWarningVO> warningList = new ArrayList<>();
        
        LocalDate now = LocalDate.now();
        LocalDate expire7Days = now.plusDays(7);
        
        for (String bloodType : bloodTypes) {
            StockWarningVO vo = new StockWarningVO();
            vo.setBloodType(bloodType);
            
            BloodStockVO summary = getStockSummary(bloodType);
            vo.setCurrentStock(summary.getTotalAmount());
            
            Integer threshold = stockThresholdService.getThresholdByType(bloodType);
            vo.setAlertThreshold(threshold);
            
            if (summary.getTotalAmount() < threshold) {
                vo.setShortageAmount(threshold - summary.getTotalAmount());
            }
            
            List<BloodStock> stocks = list(new LambdaQueryWrapper<BloodStock>()
                    .eq(BloodStock::getBloodType, bloodType)
                    .eq(BloodStock::getStatus, "正常"));
            
            int expiringCount = 0;
            int expiredCount = 0;
            for (BloodStock stock : stocks) {
                if (stock.getExpireDate() != null) {
                    if (stock.getExpireDate().isBefore(now)) {
                        expiredCount++;
                    } else if (stock.getExpireDate().isBefore(expire7Days)) {
                        expiringCount++;
                    }
                }
            }
            vo.setExpiringCount(expiringCount);
            vo.setExpiredCount(expiredCount);
            
            if (summary.getTotalAmount() < threshold * 0.5) {
                vo.setLevel("紧急");
            } else if (summary.getTotalAmount() < threshold || expiringCount > 0) {
                vo.setLevel("预警");
            } else {
                vo.setLevel("正常");
            }
            
            warningList.add(vo);
        }
        
        return warningList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(Long id, String outUnit) {
        BloodStock stock = getById(id);
        if (stock == null) {
            throw new BusinessException("库存记录不存在");
        }
        if ("已出库".equals(stock.getStatus())) {
            throw new BusinessException("该血液已出库");
        }
        stock.setStatus("已出库");
        stock.setOutUnit(outUnit);
        updateById(stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(BloodStock bloodStock) {
        BloodStock stock = getById(bloodStock.getId());
        if (stock == null) {
            throw new BusinessException("库存记录不存在");
        }
        if ("已出库".equals(stock.getStatus())) {
            throw new BusinessException("已出库的记录不能修改");
        }
        if (bloodStock.getExpireDate() != null) {
            stock.setExpireDate(bloodStock.getExpireDate());
        }
        updateById(stock);
    }
}