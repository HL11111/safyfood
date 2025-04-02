package com.system.controller;

import com.system.pojo.FoodInspectionReport;
import com.system.service.IUsersService;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.service.IFoodInspectionReportService;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * ʳƷ???ⱨ??? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/footReport")
@Slf4j
@AllArgsConstructor
@Tag(name = "食品检测报告管理")
public class FoodInspectionReportController {

    @Autowired
    private IFoodInspectionReportService reportService;
    @Autowired
    private Contract contract;

    @Autowired
    private IUsersService usersService;

    //新增数据 提交食品检测报告 + 存储foodID
    @PostMapping("/{city}/submitFoodInspectionReport")
    @Operation(summary = "提交食品检测报告")
    //注意这里参数的isPassed是true或者是false------保存下来的数值回事0或者1
    public Result submitFoodInspectionReport(@PathVariable String city, @RequestBody FoodInspectionReport report) throws Exception {
        try {
            if (!"SZ".equals(city) && !"GZ".equals(city)) {
                throw new IllegalArgumentException("城市参数无效" + city);
            }

            boolean b = usersService.isMerchantExists(report.getUserId());
            if (!b){
                throw new RuntimeException("商家不存在");
            }

            boolean c = reportService.isExitFoodId(report.getFoodId());
            if (c) {
                throw new RuntimeException("该食品报告已经存在，请勿重复提交");
            }


            log.info("开始提交食品检测报告，foodId: {}", report.getFoodId());
            log.info("调用链码，参数: foodId={}, merchantId={}, inspectionDate={}, inspectionAgency={}, safetyLevel={}, isPassed={}, complaintId={}",
                    report.getFoodId(), report.getUserId(), report.getInspectionDate(), report.getInspectionAgency(),
                    report.getSafetyLevel(), report.getIsPassed(), report.getComplaintId(),report.getGovernmentId());
            String merchantId = String.valueOf(report.getUserId());
            contract.newProposal("submitFoodInspectionReport")
                    .addArguments(
                            report.getFoodId(),
                            merchantId,
                            report.getInspectionDate(),
                            report.getInspectionAgency(),
                            report.getSafetyLevel(),
                            String.valueOf(report.getIsPassed()),
                            report.getComplaintId(),
                            report.getGovernmentId()
                    )
                    .build()        //构建交易提案
                    .endorse()      //认可（endorse）交易提案
                    .submit(); //异步调用

            // 背书成功 -> 调用这个异步提交.submitAsync() -> 就会把背书的响应结果提交到order节点上 -> 进行排序 共识 出块

            // 保存食品报告到数据库
            FoodInspectionReport foodReport = new FoodInspectionReport();
            foodReport.setFoodId(report.getFoodId());
            foodReport.setUserId(report.getUserId());
            foodReport.setInspectionDate(report.getInspectionDate());
            foodReport.setInspectionAgency(report.getInspectionAgency());
            foodReport.setSafetyLevel(report.getSafetyLevel());
            foodReport.setIsPassed(true);
            foodReport.setComplaintId(report.getComplaintId());
            foodReport.setGovernmentId(report.getGovernmentId());
            foodReport.setIsDeleted(0);

            reportService.save(foodReport);

            return Result.ok(report.getFoodId());

        }catch (Exception e){
            log.error("提交检测报告失败:{}", e.getMessage());
            return Result.build(e.getMessage(), ResultCodeEnum.FAIL);

        }
    }

    //修改数据 提交食品检测报告 + 覆盖旧值
    @Operation(summary = "修改食品检测报告")
    @PostMapping("/{city}/updateFoodInspectionReport")
    //注意这里参数的isPassed是true或者是false------保存下来的数值回事0或者1 ------  前端注意 值不能为空
    public Result<String> updateFoodInspectionReport(@PathVariable String city,@RequestBody FoodInspectionReport report) throws Exception {
        try {
            if (!"SZ".equals(city) && !"GZ".equals(city)) {
                throw new IllegalArgumentException("城市参数无效" + city);
            }

            boolean b = usersService.isMerchantExists(report.getUserId());
            if (!b){
                throw new RuntimeException("商家不存在");
            }

            log.info("开始修改食品检测报告，foodId: {}", report.getFoodId());
            log.info("调用链码，参数: foodId={}, merchantId={}, inspectionDate={}, inspectionAgency={}, safetyLevel={}, isPassed={}, complaintId={}",
                    report.getFoodId(), report.getUserId(), report.getInspectionDate(), report.getInspectionAgency(),
                    report.getSafetyLevel(), report.getIsPassed(), report.getComplaintId(),report.getGovernmentId());
            String merchantId = String.valueOf(report.getUserId());
            contract.newProposal("submitFoodInspectionReport")
                    .addArguments(
                            report.getFoodId(),
                            merchantId,
                            report.getInspectionDate(),
                            report.getInspectionAgency(),
                            report.getSafetyLevel(),
                            String.valueOf(report.getIsPassed()),
                            report.getComplaintId(),
                            report.getGovernmentId()
                    )
                    .build()        //构建交易提案
                    .endorse()      //认可（endorse）交易提案
                    .submit(); //异步调用

            // 背书成功 -> 调用这个异步提交.submitAsync() -> 就会把背书的响应结果提交到order节点上 -> 进行排序 共识 出块



            reportService.updateFoodReport(report);

            return Result.ok(report.getFoodId());

        }catch (Exception e){
            log.error("修改检测报告失败:{}", e.getMessage());
            return Result.build(e.getMessage(), ResultCodeEnum.FAIL);


        }
    }

    //删除数据 修改状态
    @Operation(summary = "删除食品检测报告")
    @DeleteMapping("/deleteFoodInspectionReport/{foodId}")
    public Result deleteFoodInspectionReport(
            @PathVariable String foodId) {

        try {


            // 2. 检查报告是否存在
            if (!reportService.isExitFoodId(foodId)) {
                throw new RuntimeException("食品报告不存在");
            }

            // 3. 更新区块链状态（如果需要）
            // 注意：区块链数据通常不可删除，可以标记状态或添加删除记录
//            contract.newProposal("markFoodReportDeleted")
//                    .addArguments(foodId)
//                    .build()
//                    .endorse()
//                    .submit();

            // 4. 本地数据库逻辑删除
            reportService.removeById(foodId);

            return Result.ok(foodId);


        } catch (Exception e) {
            log.error("删除食品检测报告失败: {}", e.getMessage());
            return Result.build(e.getMessage(), ResultCodeEnum.FAIL);
        }
    }


    //查询数据 查询食品检测报告
    @Operation(summary = "查询食品检测报告")
    @GetMapping("/FoodReports/{merchantId}")
    public Result queryFoodReportsByKey(@PathVariable int merchantId) {
        try {
            log.info("根据key查询数据{}", merchantId);

            boolean b = usersService.isMerchantExists(merchantId);
            if (!b){
                throw new RuntimeException("商家不存在");
            }

            //调用链码方法
            byte[] reportByte = contract.evaluateTransaction("queryFoodReports", String.valueOf(merchantId));
            String reportJson = StringUtils.newStringUtf8(reportByte);

            //
            log.info("查询数据的结果：{}", reportJson);

            if (reportJson == null) {
                return Result.build(null, ResultCodeEnum.DATA_DOES_NOT_EXIST);
            }
            // 直接返回 JSON 字符串
            return Result.ok(reportJson);
        } catch (Exception e) {
            log.error("查询检测报告失败: {}", e.getMessage());
            return Result.build(e.getMessage(), ResultCodeEnum.FAIL);
        }
    }


    // 溯源食品报告
    @Operation(summary = "溯源食品检测报告")
    @GetMapping("/TraceFoodReport/{foodId}")
    public Result<String> queryFoodReportByFoodId(@PathVariable String foodId) throws Exception {
        try{

            boolean b = reportService.isExitFoodId(foodId);
            if (!b){
                throw new RuntimeException("没有该数据信息的索引");
            }


            byte[] resultBytes = contract.evaluateTransaction("queryFoodReportHistory", foodId);
            String resultJson = StringUtils.newStringUtf8(resultBytes);

            //直接返回 JSON 字符串
            if (resultJson == null) {
                return Result.build(null, ResultCodeEnum.DATA_DOES_NOT_EXIST);
            }

            return Result.ok(resultJson);
        } catch (Exception e) {
            return Result.build(e.getMessage(), ResultCodeEnum.FAIL);
        }
    }


/*

        @Resource
        private IFootInspectionReportService footInspectionReportService;

        @PostMapping
        public Boolean save(@RequestBody FootInspectionReport footInspectionReport) {
            return footInspectionReportService.saveOrUpdate(footInspectionReport);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return footInspectionReportService.removeById(id);
        }

        @GetMapping
        public List<FootInspectionReport> findAll() {
            return footInspectionReportService.list();
        }

        @GetMapping("/{id}")
        public FootInspectionReport findOne(@PathVariable Integer id) {
            return footInspectionReportService.getById(id);
        }

        @GetMapping("/page")
        public Page<FootInspectionReport> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<FootInspectionReport> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return footInspectionReportService.page(new Page<>(pageNum, pageSize));
        }
*/

}

