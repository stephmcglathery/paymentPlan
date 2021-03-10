package com.trueaccord.solution.service;

import com.trueaccord.solution.model.Debt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface DemoService {

    List<Debt> getDebt() throws ValidationException;

    ResponseEntity<StreamingResponseBody> getDebtStream() throws ValidationException;
}