package com.lyra.api.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
//        Predicate<RequestHandler> adminPredicate = RequestHandlerSelectors.basePackage("com.lyra.admin.controller");
//        Predicate<RequestHandler> articlePredicate = RequestHandlerSelectors.basePackage("com.lyra.article.controller");
//        Predicate<RequestHandler> userPredicate = RequestHandlerSelectors.basePackage("com.lyra.api.user.controller");
//        Predicate<RequestHandler> filesPredicate = RequestHandlerSelectors.basePackage("com.lyra.files.controller");
//

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .select()
                // 扫描 api 包
//                .apis(Predicates.or(adminPredicate, articlePredicate, userPredicate, filesPredicate))
                .apis(Predicates.or(RequestHandlerSelectors.basePackage("com.lyra.user.controller"),
                                    RequestHandlerSelectors.basePackage("com.lyra.file.controller"),
                                    RequestHandlerSelectors.basePackage("com.lyra.admin.controller"),
                                    RequestHandlerSelectors.basePackage("com.lyra.article.controller")))
                // controller 路径 any为任何路径
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 设置文档信息
     * @return apiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("自媒体接口API")
                .contact(new Contact("Lyra",
                        "https://www.cnblogs.com/lyraHeartstrings/",
                        "365373011@qq.com"))
                .description("自媒体平台提供的API文档")
                .version("1.0.0")
                // 网站地址
                .termsOfServiceUrl("https://www.cnblogs.com/lyraHeartstrings/")
                .build();
    }
}
