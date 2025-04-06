package com.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("report-contract")
                        .version("1.0")
                        .description(""));
    }
    
    @Bean
    public GroupedOpenApi reportAPI() {
        return GroupedOpenApi.builder().group("报告信息管理").
                pathsToMatch("/report/**").
                build();
    }

    @Bean
    public GroupedOpenApi foodReportAPI() {
        return GroupedOpenApi.builder().group("食品报告信息管理").
                pathsToMatch("/footReport/**").
                build();
    }

    @Bean
    public GroupedOpenApi governmentStandardAPI() {
        return GroupedOpenApi.builder().group("政府标准信息管理").
                pathsToMatch("/government-standard/**").
                build();
    }

    @Bean
    public GroupedOpenApi governmentAPI() {
        return GroupedOpenApi.builder().group("政府信息管理").
                pathsToMatch("/government/**").
                build();
    }
    @Bean
    public GroupedOpenApi phoneCodeAPI() {
        return GroupedOpenApi.builder().group("验证码信息管理").
                pathsToMatch("/users/**").
                build();
    }






}