package com.trueaccord.solution.controller;

import com.trueaccord.solution.exception.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @ApiIgnore
    @RequestMapping(value = "**",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String defaultHandler(HttpServletRequest request) throws Exception {

        String requestPath = request.getServletPath();
        throw new ResourceNotFoundException("The resource[" + requestPath + "] does not exist in the Demo Service");
    }
}
