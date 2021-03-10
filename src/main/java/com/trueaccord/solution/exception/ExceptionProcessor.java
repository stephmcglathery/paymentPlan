package com.trueaccord.solution.exception;

import com.trueaccord.solution.service.DemoServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;

@ControllerAdvice
public class ExceptionProcessor {

    private final static Logger log = Logger.getLogger(DemoServiceImpl.class);

    @Autowired
    private MessageSource messageSource;

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo resourceNotFoundExceptionHandler(HttpServletRequest request, ResourceNotFoundException resourceNotFoundException) {

        String messageId = "error.global.invalidpath";
        String exceptionMessage = resourceNotFoundException.getMessage();
        return generateErrorInfo(request, messageId, exceptionMessage);
    }

    private ErrorInfo generateErrorInfo(HttpServletRequest request, String errorMessageId, String exceptionMessage) {

        String errorURL = request.getRequestURL().toString();
        Locale locale = LocaleContextHolder.getLocale();

        String errorMessage = null;

        try {
            errorMessage = messageSource.getMessage(errorMessageId, null, locale);
        } catch (NoSuchMessageException e) {
            e.printStackTrace();
        }

        return new ErrorInfo(errorURL, errorMessage, exceptionMessage);
    }
}
