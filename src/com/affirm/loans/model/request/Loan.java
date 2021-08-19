package com.affirm.loans.model.request;

import lombok.Data;

@Data
public class Loan {
    float interest_rate;
    float amount;
    int id;
    float default_likelihood;
    String state;

}
