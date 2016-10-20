package com.jifenke.lepluslive.global.config;

/**
 * Created by zhangwen on 2016/4/26.
 */

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfig
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket payApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("APP支付")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/lepay/appPay/.*")))//过滤的接口
                .build()
                .apiInfo(payInfo());
    }
    @Bean
    public Docket merchantApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("银联POS商户相关")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/lepay/m_user/.*")))//过滤的接口
                .build()
                .apiInfo(merchantInfo());
    }
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("银联POS用户相关")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/lepay/user/.*")))//过滤的接口
                .build()
                .apiInfo(userInfo());
    }
    @Bean
    public Docket orderApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("银联POS订单相关")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/lepay/u_order/.*")))//过滤的接口
                .build()
                .apiInfo(orderInfo());
    }
    @Bean
    public Docket u_payApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("银联POS支付相关")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/lepay/pospay/u_pay/.*")))//过滤的接口
                .build()
                .apiInfo(u_payInfo());
    }

    private ApiInfo payInfo() {
        ApiInfo apiInfo = new ApiInfo("APP支付接口",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
    private ApiInfo merchantInfo() {
        ApiInfo apiInfo = new ApiInfo("银联POS机商户相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );
      return apiInfo;
    }
    private ApiInfo userInfo() {
        ApiInfo apiInfo = new ApiInfo("银联POS机用户相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
    private ApiInfo orderInfo() {
        ApiInfo apiInfo = new ApiInfo("银联POS机订单相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
    private ApiInfo u_payInfo() {
        ApiInfo apiInfo = new ApiInfo("银联POS机支付相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }






    /** * SpringBoot默认已经将classpath:/META-INF/resources/和classpath:/META-INF/resources/webjars/映射 * 所以该方法不需要重写，如果在SpringMVC中，可能需要重写定义（我没有尝试） * 重写该方法需要 extends WebMvcConfigurerAdapter * */
// @Override
// public void addResourceHandlers(ResourceHandlerRegistry registry) {
// registry.addResourceHandler("swagger-ui.html")
// .addResourceLocations("classpath:/META-INF/resources/");
//
// registry.addResourceHandler("/webjars/**")
// .addResourceLocations("classpath:/META-INF/resources/webjars/");
// }

 /*  @Bean
    public Docket demoApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("demo")
                .genericModelSubstitutes(DeferredResult.class)
// .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/demo/.*")))//过滤的接口
                .build()
                .apiInfo(demoApiInfo());
    }*/
    /*private ApiInfo demoApiInfo() {
        ApiInfo apiInfo = new ApiInfo("Electronic Health Record(EHR) Platform API",//大标题
                "EHR Platform's REST API, for system administrator",//小标题
                "1.0",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }*/
}
