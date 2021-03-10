package com.trueaccord.solution.controller;

import com.trueaccord.solution.model.Debt;
import com.trueaccord.solution.service.DemoService;
import org.easymock.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(EasyMockRunner.class)
public class DemoControllerTest extends EasyMockSupport {

    @TestSubject
    DemoController demoController = new DemoController();

    @Mock
    DemoService demoService;

    @Before
    public void setUp() {

    }

    @Test
    public void getDebt() throws ValidationException {

        List<Debt> expectedDebt = new ArrayList<>();

        demoService.getDebt();
        EasyMock.expectLastCall().andReturn(expectedDebt);
        replayAll();

        List<Debt> actualDebt = demoController.getDebt();
        verifyAll();

        assertEquals(expectedDebt, actualDebt);
    }

    @Test
    public void getDebtStream() throws ValidationException {

        ResponseEntity<StreamingResponseBody> expectedDebtStream = new ResponseEntity<StreamingResponseBody>(HttpStatus.OK);

        demoService.getDebtStream();
        EasyMock.expectLastCall().andReturn(expectedDebtStream);
        replayAll();

        ResponseEntity<StreamingResponseBody> actualDebtStream = demoController.getDebtStream();
        verifyAll();

        assertEquals(expectedDebtStream, actualDebtStream);
    }
}