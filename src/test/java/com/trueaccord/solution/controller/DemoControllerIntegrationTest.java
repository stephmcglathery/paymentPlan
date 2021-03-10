package com.trueaccord.solution.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trueaccord.solution.DemoApplication;
import com.trueaccord.solution.model.Debt;
import com.trueaccord.solution.model.Payment;
import com.trueaccord.solution.model.PaymentPlan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoControllerIntegrationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper objectMapper = new ObjectMapper();
    private String uri;

    @Test
    public void getDebt() throws IOException {

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/demo/debt"),
                HttpMethod.GET, entity, String.class);

        List<Debt> debt = objectMapper.readValue(response.getBody(), new TypeReference<List<Debt>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(debt.size() > 1);

        debt.forEach(d -> {
            Number remainingAmount = d.getRemaining_amount();

            PaymentPlan paymentPlan = d.getPaymentPlan();

            if (paymentPlan != null) {

                BigDecimal amountToPay = paymentPlan.getAmountToPay();

                List<Payment> payments = paymentPlan.getPayments();

                final BigDecimal[] totalAmountPaid = {new BigDecimal(0)};
                payments.forEach(payment -> {
                    totalAmountPaid[0] = totalAmountPaid[0].add(payment.getAmount());
                });

                assertTrue(d.isIs_in_payment_plan());
                assertTrue(remainingAmount.equals(amountToPay.subtract(totalAmountPaid[0])));

                if (d.getRemaining_amount().doubleValue() == 0) {
                    assertNull(d.getNext_payment_due_date());
                }

                if (paymentPlan.getInstallmentFrequency().equals("WEEKLY") && d.getRemaining_amount().doubleValue() > 0) {
                    if (paymentPlan.getLastPaymentDate() != null) {
                        assertEquals(d.getNext_payment_due_date(), paymentPlan.getLastPaymentDate().plusDays(7));
                    } else {
                        assertEquals(d.getNext_payment_due_date(), paymentPlan.getStartDate().plusDays(7));
                    }
                } else if (paymentPlan.getInstallmentFrequency().equals("BI_WEEKLY") && d.getRemaining_amount().doubleValue() > 0) {
                    if (paymentPlan.getLastPaymentDate() != null) {
                        assertEquals(d.getNext_payment_due_date(), paymentPlan.getLastPaymentDate().plusDays(14));
                    } else {
                        assertEquals(d.getNext_payment_due_date(), paymentPlan.getStartDate().plusDays(14));
                    }
                }

                if(remainingAmount.doubleValue() > 0) {
                 assertNotNull(d.getNext_payment_due_date());
                }
            } else {
                assertTrue(d.getRemaining_amount().equals(d.getAmount()));
                assertFalse(d.isIs_in_payment_plan());
            }
        });
    }

    @Test
    public void getDebtStream() throws IOException {

        String json = readFile(new ClassPathResource("debt.json").getFile());

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/demo/debtStream"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private String readFile(File file) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder sb = new StringBuilder();

        while((line=bufferedReader.readLine())!= null){
            sb.append(line.trim());
        }

        return sb.toString();
    }
}