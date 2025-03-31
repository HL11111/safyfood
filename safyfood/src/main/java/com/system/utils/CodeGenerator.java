package com.system.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * @Author H
 * title
 */
public class CodeGenerator {

    public static void main(String[] args) {
        generate();
    }

    private static void generate() {
        FastAutoGenerator.create("jdbc:mysql:///safyfooddb", "root", "752666")
                .globalConfig(builder -> {
                    builder.author("H") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
//                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\kaifa\\jdk17\\project\\safyfood\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.system") //
                            .moduleName(null) // 设置父包模块名（空字符串""，地址会变成//）
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\kaifa\\jdk17\\project\\safyfood\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
//                builder.mapperBuilder().enableMapperAnnotation().build();  MybatisPlusCong.java文件里加过MapperScan，这里就不需要开启
                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
                            .enableRestStyle();  // 开启生成@RestController 控制器
                    builder.addInclude("article_feedback","article","comment","food_info","foot_inspection_report","government","government_standard","inspection_report","question_bank","question","shopping_cart","users","orders","address","service_info"); // 设置需要生成的表名
//                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
//            .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}

