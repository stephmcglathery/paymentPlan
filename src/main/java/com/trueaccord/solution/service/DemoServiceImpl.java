package com.trueaccord.solution.service;

import com.trueaccord.solution.model.Debt;
import com.trueaccord.solution.model.Payment;
import com.trueaccord.solution.model.PaymentPlan;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemoServiceImpl implements DemoService {

    private final static Logger log = Logger.getLogger(DemoServiceImpl.class);

    public ResponseEntity<StreamingResponseBody> getDebtStream() throws ValidationException {

        List<Debt> debtList = getDebt();

        StreamingResponseBody responseBody = response -> {

            debtList.forEach(debt -> {
                try {
                    Thread.sleep(1);
                    response.write((debt.toString()+ "\n").getBytes());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        };
        return ResponseEntity.ok()
                .body(responseBody);
    }

    public List<Debt> getDebt() throws ValidationException {

        log.debug("Attempting to get debt");

        RestTemplate restTemplate = new RestTemplate();
        String debtUrl = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/debts";
        ResponseEntity<Debt[]> response =
                restTemplate.getForEntity(debtUrl, Debt[].class);

        List<Debt> debt = Arrays.asList(response.getBody());
        log.debug("initial debt: " + debt.toString());

        List<PaymentPlan> paymentPlans = getPaymentPlans();
        log.debug("payment plans: " + paymentPlans.toString());

        List<Payment> payments = getPayments();
        log.debug("payments: " + payments.toString());

        LinkedHashMap<Integer, BigDecimal> balancesForPaymentPlans = new LinkedHashMap();

        paymentPlans.forEach(paymentPlan -> {

            LocalDate lastPaymentMadeDate = null;

            BigDecimal balanceForPlan = paymentPlan.getAmountToPay();
            log.debug("Balance for " + paymentPlan.getId() + ": " + balanceForPlan);

            List<Payment> paymentsForPlan = payments.stream()
                    .filter(p->p.getPaymentPlanId() == paymentPlan.getId())
                    .collect(Collectors.toList());

            for (Payment payment : paymentsForPlan) {
                log.debug("Payment for " + paymentPlan.getId() + ": " + payment.getAmount());
                balanceForPlan = balanceForPlan.subtract(payment.getAmount());
                log.debug("Updated balance for " + paymentPlan.getId() + ": " + balanceForPlan);

                if (lastPaymentMadeDate == null) {
                    lastPaymentMadeDate = payment.getDate();
                } else {
                    if (payment.getDate().isAfter(lastPaymentMadeDate)) {
                        lastPaymentMadeDate = payment.getDate();
                    }
                }
            }

            paymentPlan.setLastPaymentDate(lastPaymentMadeDate);
            paymentPlan.setPayments(paymentsForPlan);
            balancesForPaymentPlans.put(paymentPlan.getId(), balanceForPlan);

            log.debug("Final balance for " + paymentPlan.getId() + ": " + balanceForPlan);
        });

        for (Debt d : debt) {
            PaymentPlan paymentPlan = paymentPlans.stream()
                    .filter(p -> d.getId() == (p.getDebtId()))
                    .findAny()
                    .orElse(null);

            if (paymentPlan != null) {
                d.setPaymentPlan(paymentPlan);
                d.setIs_in_payment_plan(true);
                d.setRemaining_amount(balancesForPaymentPlans.get(paymentPlan.getId()));

                if (d.getRemaining_amount().intValue() > 0) {

                    d.setNext_payment_due_date(determineNextPaymentDate(d));
                }
            } else {
                d.setRemaining_amount(d.getAmount());
            }
        }
        return debt;
    }

    private LocalDate determineNextPaymentDate(Debt debt) throws ValidationException {

        log.debug("Attempting to determine next payment date for debt: " + debt.getId());

        PaymentPlan paymentPlan = debt.getPaymentPlan();
        String installmentFrequency = debt.getPaymentPlan().getInstallmentFrequency();
        LocalDate nextPaymentDate = null;

        switch (installmentFrequency) {
            case "WEEKLY":
                if (paymentPlan.getLastPaymentDate() != null) {
                    nextPaymentDate = paymentPlan.getLastPaymentDate().plusDays(7);
                } else {
                    nextPaymentDate = paymentPlan.getStartDate().plusDays(7);
                }
                break;

            case "BI_WEEKLY":
                if (paymentPlan.getLastPaymentDate() != null) {
                    nextPaymentDate = paymentPlan.getLastPaymentDate().plusDays(14);
                } else {
                    nextPaymentDate = paymentPlan.getStartDate().plusDays(14);
                }
                break;

            default:
                throw new ValidationException(installmentFrequency + " is an unsupported installment frequency");
        }
        log.debug("Next payment date for debt '" + debt.getId() + ": " + nextPaymentDate);
        return nextPaymentDate;
    }

    private List<PaymentPlan> getPaymentPlans() {

        log.debug("Attempting to get payment plans");

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payment_plans";

        ResponseEntity<PaymentPlan[]> response =
                restTemplate.getForEntity(url, PaymentPlan[].class);

        return Arrays.asList(response.getBody());
    }

    private List<Payment> getPayments() {

        log.debug("Attempting to get payments");

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payments";

        ResponseEntity<Payment[]> response =
                restTemplate.getForEntity(url, Payment[].class);

        return Arrays.asList(response.getBody());
    }
}