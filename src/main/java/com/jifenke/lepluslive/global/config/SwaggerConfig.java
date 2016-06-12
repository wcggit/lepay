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

import java.nio.channels.Selector;

/**
 * SwaggerConfig
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .genericModelSubstitutes(DeferredResult.class)
// .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/user/.*")))//过滤的接口
                .build()
                .apiInfo(userApiInfo());
    }

    @Bean
    public Docket merchantApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("merchant")
                .genericModelSubstitutes(DeferredResult.class)
// .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/merchant/.*")))//过滤的接口
                .build()
                .apiInfo(merchantApiInfo());
    }

    @Bean
    public Docket topicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("topic")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .paths(or(regex("/topic/.*")))//过滤的接口
                .build()
                .apiInfo(topicApiInfo());
    }

    @Bean
    public Docket cityApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("city")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")
                .select()
                .paths(or(regex("/city/.*")))
                .build()
                .apiInfo(cityApiInfo());
    }
    @Bean
    public Docket addressApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("address")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")
                .select()
                .paths(or(regex("/address/.*")))
                .build()
                .apiInfo(addressApiInfo());
    }
  @Bean
  public Docket shopApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("shop")
        .genericModelSubstitutes(DeferredResult.class)
        .useDefaultResponseMessages(false)
        .forCodeGeneration(true)
        .pathMapping("/")
        .select()
        .paths(or(regex("/shop/.*")))
        .build()
        .apiInfo(shopApiInfo());
  }
  @Bean
  public Docket orderApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("order")
        .genericModelSubstitutes(DeferredResult.class)
        .useDefaultResponseMessages(false)
        .forCodeGeneration(true)
        .pathMapping("/")
        .select()
        .paths(or(regex("/order/.*")))
        .build()
        .apiInfo(orderApiInfo());
  }

  @Bean
  public Docket scoreApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("积分和红包")
        .genericModelSubstitutes(DeferredResult.class)
        .useDefaultResponseMessages(false)
        .forCodeGeneration(true)
        .pathMapping("/")
        .select()
        .paths(or(regex("/score/.*")))
        .build()
        .apiInfo(scoreApiInfo());
  }


    private ApiInfo userApiInfo() {
        ApiInfo apiInfo = new ApiInfo("用户相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }

    private ApiInfo merchantApiInfo() {
        ApiInfo apiInfo = new ApiInfo("商家相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }

    private ApiInfo topicApiInfo() {
        ApiInfo apiInfo = new ApiInfo("专题相关操作",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }

    private ApiInfo cityApiInfo() {
        ApiInfo apiInfo = new ApiInfo("获取城市列表",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
    private ApiInfo addressApiInfo() {
        ApiInfo apiInfo = new ApiInfo("收货地址管理",//大标题
                "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                "0.1",//版本
                "NO terms of service",
                "zhangwenit@126.com",//作者
                "The Apache License, Version 2.0",//链接显示文字
                "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
        );

        return apiInfo;
    }
  private ApiInfo shopApiInfo() {
    ApiInfo apiInfo = new ApiInfo("商品管理",//大标题
                                  "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                                  "0.1",//版本
                                  "NO terms of service",
                                  "zhangwenit@126.com",//作者
                                  "The Apache License, Version 2.0",//链接显示文字
                                  "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
    );

    return apiInfo;
  }
  private ApiInfo orderApiInfo() {
    ApiInfo apiInfo = new ApiInfo("订单管理",//大标题
                                  "EHR Platform's REST API, all the applications could access the Object model data via JSON.",//小标题
                                  "0.1",//版本
                                  "NO terms of service",
                                  "zhangwenit@126.com",//作者
                                  "The Apache License, Version 2.0",//链接显示文字
                                  "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
    );

    return apiInfo;
  }
  private ApiInfo scoreApiInfo() {
    ApiInfo apiInfo = new ApiInfo("积分和红包管理",//大标题
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
