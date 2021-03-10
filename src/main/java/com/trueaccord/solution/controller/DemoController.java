package com.trueaccord.solution.controller;

import com.trueaccord.solution.model.Debt;
import com.trueaccord.solution.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.xml.bind.ValidationException;
import java.util.List;

@Controller
@EnableAutoConfiguration
@Api(value="TrueAccord Solution Controller")
@RequestMapping(value = "/demo")
public class DemoController extends BaseController{

    @Autowired
    DemoService demoService;

    @RequestMapping(value = "debt", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Debt")
    @ResponseBody
    public List<Debt> getDebt() throws ValidationException {

        return demoService.getDebt();
    }

    @RequestMapping(value = "debtStream", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Get Debt Stream")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> getDebtStream() throws ValidationException {

        return demoService.getDebtStream();
    }
}