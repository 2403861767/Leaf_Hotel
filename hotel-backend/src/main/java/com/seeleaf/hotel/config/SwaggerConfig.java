package com.seeleaf.hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置，提供 Swagger UI 交互式接口文档。
 * <p>
 * 访问地址：{@code http://localhost:8080/swagger-ui.html}
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Leaf-Hotel 酒店前台管理系统 API")
                        .version("v1.0")
                        .description("基于 Spring Boot + Vue3 的前后端分离酒店前台管理系统")
                        .contact(new Contact()
                                .name("Leaf-Hotel Team")
                                .email("contact@leafhotel.com")));
    }
}
