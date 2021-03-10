package com.trueaccord.solution.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Debt implements Serializable {

    private int id;

    private BigDecimal amount;

    boolean is_in_payment_plan;

    Number remaining_amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate next_payment_due_date;

    private PaymentPlan paymentPlan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isIs_in_payment_plan() {
        return is_in_payment_plan;
    }

    public void setIs_in_payment_plan(boolean is_in_payment_plan) {
        this.is_in_payment_plan = is_in_payment_plan;
    }

    public Number getRemaining_amount() {
        return remaining_amount;
    }

    public void setRemaining_amount(BigDecimal remaining_amount) {
        this.remaining_amount = remaining_amount;
    }

    public LocalDate getNext_payment_due_date() {
        return next_payment_due_date;
    }

    public void setNext_payment_due_date(LocalDate next_payment_due_date) {
        this.next_payment_due_date = next_payment_due_date;
    }

    public PaymentPlan getPaymentPlan() {
        return paymentPlan;
    }

    public void setPaymentPlan(PaymentPlan paymentPlan) {
        this.paymentPlan = paymentPlan;
    }

    @Override
    public String toString() {
        return "Debt{" +
                "id=" + id +
                ", amount=" + amount +
                ", is_in_payment_plan=" + is_in_payment_plan +
                ", remaining_amount=" + remaining_amount +
                ", next_payment_due_date=" + next_payment_due_date +
                ", paymentPlan=" + paymentPlan +
                '}';
    }
}