package com.wu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi(){
        return  new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wu.controller"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(securityContexts())
                .securitySchemes(securitySchemes());
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("云E办的接口文档")
                .description("云E办的接口文档")
                .contact(new Contact("吴华亮", "http://localhost:8081/doc.html", "2570700075@qq.com"))
                .version("1.0")
                .build();
    }
    private List<ApiKey> securitySchemes(){
        //设置请求头信息
        List<ApiKey> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization","Authorization","Header");
        result.add(apiKey);
        return  result;
    }
    private  List<SecurityContext> securityContexts() {
        //设置需登录认证的路径
        ArrayList<SecurityContext> result = new ArrayList<>();
        result.add(getContextBypath("/hello/.*"));
        return result;
    }

    private SecurityContext getContextBypath(String pathRegx) {
        return  SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegx))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
       List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope=new AuthorizationScope("global","accessEverything");
        AuthorizationScope[]  authorizationScope1 = new AuthorizationScope[1];
        authorizationScope1[0]=authorizationScope;
       result.add(new SecurityReference("Authorization",authorizationScope1));
       return  result;
    }

}