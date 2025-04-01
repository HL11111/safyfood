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


}