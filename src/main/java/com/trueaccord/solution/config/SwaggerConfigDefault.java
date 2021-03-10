package com.trueaccord.solution.config;

import com.trueaccord.solution.exception.ErrorInfo;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@Profile("!production")
public class SwaggerConfigDefault extends SwaggerConfigBase {

    @Autowired
    private TypeResolver typeResolver;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private static final List<ResponseMessage> GET_RESPONSE_MESSAGES;

    static {

        GET_RESPONSE_MESSAGES = new ArrayList<ResponseMessage>(3);
        GET_RESPONSE_MESSAGES.add(new ResponseMessageBuilder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(BAD_REQUEST_ERROR_MESSAGE)
                .responseModel(ERROR_INFO_MODEL_REF)
                .build());
        GET_RESPONSE_MESSAGES.add(new ResponseMessageBuilder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(NOT_FOUND_ERROR_MESSAGE)
                .responseModel(ERROR_INFO_MODEL_REF)
                .build());
        GET_RESPONSE_MESSAGES.add(new ResponseMessageBuilder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(INTERNAL_SERVER_ERROR_MESSAGE)
                .responseModel(ERROR_INFO_MODEL_REF)
                .build());
    }

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, GET_RESPONSE_MESSAGES)
                .additionalModels(typeResolver.resolve(ErrorInfo.class));
    }


    private ApiInfo apiInfo() {
        String title = "TrueAccord Solution (" + activeProfile + ")";

        return new ApiInfoBuilder()
                .title(title)
                .description("This is a solution for TrueAccord")
                .version("1.0")
                .build();
    }
}