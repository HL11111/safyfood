package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.pojo.Vo.InspectionReportVo;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.system.service.IInspectionReportService;
import com.system.pojo.InspectionReport;

import org.springframework.web.bind.annotation.RestController;

import static com.system.utils.ResultCodeEnum.*;



/**
 * <p>
 * ?̼Ҽ??ⱨ??? 前端控制器
 * </p>
 *
 * @author H
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/report")
@AllArgsConstructor
@Slf4j
@Tag(name = "inspectionReport信息管理")
public class InspectionReportController {

    @Autowired
    private IInspectionReportService inspectionReportService;

    @Autowired
    private IUsersService usersService;
    @Autowired
    private Contract contract;


    // 2 新增数据 提交检测报告 + 存储reportId
    @PostMapping("/{city}/submitReport")
    @Operation(summary = "提交商家检测报告")
    public Result<String> submitReport(@PathVariable String city , @RequestBody InspectionReportVo report) throws Exception {


        try{

            if(!"SZ".equals(city) && !"GZ".equals(city)) {
                throw new IllegalArgumentException("城市参数无效" + city);
            }
            System.out.println("城市参数有效");
            String merchantId = String.valueOf(report.getUserId());

            boolean b = usersService.MerchantExists(report.getUserId());
            if (!b){
                throw new RuntimeException("商家不存在");
            }

            boolean c = inspectionReportService.isExitReport(report.getReportId());
            if (c) {
                throw new RuntimeException("该商家报告已经存在，请勿重复提交");
            }

/*
            // 调用 getSafetyStandardsJson() 方法将 safetyStandards 转换为 JSON 字符串
            String safetyStandardsJson = new Gson().toJson(report.getSafetyStandards());

            ////验证 safetyStandardsJson 是否有效的JSON数组
            System.out.println("safetyStandardsJson字符串"+safetyStandardsJson);
            if(safetyStandardsJson == null || !safetyStandardsJson.startsWith("[") || !safetyStandardsJson.endsWith("]")){
                log.error("Invalid safetyStandardsJson format: " + safetyStandardsJson);
                return new Result<>(400,"Invalid safeStandards format:", null);
            }
            System.out.println("-------------------String.valueOf(report.isPassed()),-----------------------"+String.valueOf(report.isPassed()));
*/

            System.out.println("report.getSafetyStandardsJson()----------------" + report.getSafetyStandardsJson());
            //创建一个新的交易提案，并指定要调用的合约方法名
            contract.newProposal("submitInspectionReport")
                    .addArguments(  //定义参数
                            report.getReportId(),
                            merchantId,
                            report.getInspectionDate(),
                            report.getInspectionAgency(),
                            report.getSafetyStandardsJson(),
                            report.getSafetyLevel(),
                            String.valueOf(report.getIsPassed()),
                            report.getGovernmentId()

                            //safetyStandardsJson


                    )
                    .build()        //构建交易提案
                    .endorse()      //认可（endorse）交易提案
                    .submit(); //

            // 背书成功 -> 调用这个异步提交.submitAsync() -> 就会把背书的响应结果提交到order节点上 -> 进行排序 共识 出块

            System.out.println("对象创建成功");


            //保存商家报告到数据库
            InspectionReport inspectionReport = new InspectionReport();
            inspectionReport.setReportId(report.getReportId());
            inspectionReport.setUserId(report.getUserId());
            inspectionReport.setInspectionDate(report.getInspectionDate());
            inspectionReport.setInspectionAgency(report.getInspectionAgency());
            inspectionReport.setSafetyStandard(report.getSafetyStandardsJson());
            inspectionReport.setSafetyLevel(report.getSafetyLevel());
            inspectionReport.setIsPassed(true);
            inspectionReport.setGovernmentId(report.getGovernmentId());
            inspectionReport.setIsDeleted(0);


            inspectionReportService.save(inspectionReport);

            return Result.ok(report.getReportId());


        }catch (Exception e){
            log.error("提交检测报告失败:{}", e.getMessage());
            return Result.build(null,500,"提交检测报告失败:" + e.getMessage());
        }

    }

    // 2 修改数据 提交检测报告 + 存储reportId
    @PostMapping("/{city}/updateReport")
    @Operation(summary = "修改商家检测报告")
    public Result<String> updateReport(@PathVariable String city ,@RequestBody InspectionReportVo report) throws Exception {


        try{

            if(!"SZ".equals(city) && !"GZ".equals(city)) {
                throw new IllegalArgumentException("城市参数无效" + city);
            }
            System.out.println("城市参数有效");
            String merchantId = String.valueOf(report.getUserId());

            boolean b = usersService.MerchantExists(report.getUserId());
            if (!b){
                throw new RuntimeException("商家不存在");
            }
/*
            // 调用 getSafetyStandardsJson() 方法将 safetyStandards 转换为 JSON 字符串
            String safetyStandardsJson = new Gson().toJson(report.getSafetyStandards());

            ////验证 safetyStandardsJson 是否有效的JSON数组
            System.out.println("safetyStandardsJson字符串"+safetyStandardsJson);
            if(safetyStandardsJson == null || !safetyStandardsJson.startsWith("[") || !safetyStandardsJson.endsWith("]")){
                log.error("Invalid safetyStandardsJson format: " + safetyStandardsJson);
                return new Result<>(400,"Invalid safeStandards format:", null);
            }
            System.out.println("-------------------String.valueOf(report.isPassed()),-----------------------"+String.valueOf(report.isPassed()));
*/

            System.out.println("report.getSafetyStandardsJson()----------------" + report.getSafetyStandardsJson());
            //创建一个新的交易提案，并指定要调用的合约方法名
            contract.newProposal("updateInspectionReport")
                    .addArguments(  //定义参数
                            report.getReportId(),
                            merchantId,
                            report.getInspectionDate(),
                            report.getInspectionAgency(),
                            report.getSafetyStandardsJson(),
                            report.getSafetyLevel(),
                            String.valueOf(report.getIsPassed()),
                            report.getGovernmentId()

                            //safetyStandardsJson


                    )
                    .build()        //构建交易提案
                    .endorse()      //认可（endorse）交易提案
                    .submit(); //

            // 背书成功 -> 调用这个异步提交.submitAsync() -> 就会把背书的响应结果提交到order节点上 -> 进行排序 共识 出块

            System.out.println("对象创建成功");




            inspectionReportService.updateInspection(report);

            return Result.build(report.getReportId(), SUCCESS);


        }catch (Exception e){
            log.error("修改检测报告失败:{}", e.getMessage());
            return Result.build(e.getMessage(),FAIL);
        }

    }

    //删除数据 修改状态
    @Transactional
    @DeleteMapping("/deleteInspectionReport/{reportId}")
    @Operation(summary = "删除商家检测报告")
    public Result<String> deleteInspectionReport(
            @PathVariable String reportId) {

        try {


            // 2. 检查报告是否存在
            if (!inspectionReportService.isExitReport(reportId)) {
                throw new RuntimeException("商家报告不存在");
            }

            // 3. 更新区块链状态（如果需要）
            // 注意：区块链数据通常不可删除，可以标记状态或添加删除记录
//            contract.newProposal("markFoodReportDeleted")
//                    .addArguments(foodId)
//                    .build()
//                    .endorse()
//                    .submit();

            // 4. 本地数据库逻辑删除

            inspectionReportService.removeById(reportId);

            // 5. 更新索引状态
            inspectionReportService.updateStatus(reportId);

            return Result.build(reportId,200,"删除成功");

        } catch (Exception e) {
            log.error("删除食品检测报告失败: {}", e.getMessage());
            return Result.build(null ,500,"删除失败: " + e.getMessage());
        }
    }


/*    @GetMapping("queryInspectionReport/{reportId}")
    @Operation(summary = "查询本地商家检测报告")
    public Result queryInspectionReport(@PathVariable String reportId) {
        return Result.ok(inspectionReportService.getById(reportId));
    }*/

    // 6 查询数据 查询政府标准
    @Operation(summary = "查询政府标准")
    @GetMapping("/GovernmentStandards/{governmentId}")
    public Result queryGovernmentStandardsByKey(@PathVariable String governmentId) {
        try {
            log.info("根据key查询数据:{}", governmentId);

            // 调用链码方法
            byte[] standardsByte = contract.evaluateTransaction("queryGovernmentStandards", governmentId);
            String standardsJson = StringUtils.newStringUtf8(standardsByte);

            log.info("查询数据结果:{}", standardsJson);

            // 直接返回 JSON 字符串
            return Result.ok(standardsJson);
        } catch (Exception e) {
            log.error("查询政府标准失败: {}", e.getMessage());
            return Result.build(e.getMessage(),FAIL);
        }
    }

    // 5 查询数据 查询商家检测报告
    @Operation(summary = "查询商家报告")
    @GetMapping("/InspectionReports/{merchantId}")
    public Result queryInspectionReportsByKey(@PathVariable int merchantId) {
        try {
            log.info("根据key查询数据:{}", merchantId);

            String MerchantIdStr = String.valueOf(merchantId);

            boolean b = inspectionReportService.isExitReportByMerchant(merchantId);
            if (!b){
                throw new RuntimeException("没有该商家D报告信息");
            }
            // 调用链码方法
            byte[] reportByte = contract.evaluateTransaction("queryInspectionReports", MerchantIdStr );

            if (reportByte == null || reportByte.length == 0) {
                return Result.build(null,DATA_DOES_NOT_EXIST);
            }
            String reportJson = StringUtils.newStringUtf8(reportByte);

            System.out.println(reportJson);

            log.info("查询数据结果:{}", reportJson);

            // 直接返回 JSON 字符串
            return Result.build(reportJson,SUCCESS);
        } catch (Exception e) {
            log.error("查询检测报告失败: {}", e.toString());
            return Result.build(e.getMessage(),FAIL);
        }
    }


    // 7 溯源商家报告
    @Operation(summary = "溯源商家报告")
    @GetMapping("/TraceInspectionReport/{reportId}")
    public Result queryInspectionReportByReportId(@PathVariable String reportId) throws Exception {
        try {
            boolean b = inspectionReportService.isExitReport(reportId);
            if (!b){
                throw new RuntimeException("没有该数据信息的索引");
            }

            // 调用链码方法
            byte[] resultBytes = contract.evaluateTransaction("queryInspectionReportHistory", reportId);
            String resultJson = StringUtils.newStringUtf8(resultBytes);

            // 直接返回 JSON 字符串
            return Result.ok(resultJson);
        } catch (Exception e) {
            return Result.build(e.getMessage(),FAIL);
        }
    }


/*
        @Resource
        private IInspectionReportService inspectionReportService;

        @PostMapping
        public Boolean save(@RequestBody InspectionReport inspectionReport) {
            return inspectionReportService.saveOrUpdate(inspectionReport);
        }

        @DeleteMapping("/{id}")
        public Boolean delete(@PathVariable Integer id) {
            return inspectionReportService.removeById(id);
        }

        @GetMapping
        public List<InspectionReport> findAll() {
            return inspectionReportService.list();
        }

        @GetMapping("/{id}")
        public InspectionReport findOne(@PathVariable Integer id) {
            return inspectionReportService.getById(id);
        }

        @GetMapping("/page")
        public Page<InspectionReport> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
            QueryWrapper<InspectionReport> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return inspectionReportService.page(new Page<>(pageNum, pageSize));
        }
*/

}
