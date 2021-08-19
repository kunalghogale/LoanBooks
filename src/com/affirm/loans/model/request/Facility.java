package com.affirm.loans.model.request;

import lombok.Data;

@Data
public class Facility {
    float amount;
    float interest_rate;
    int id;
    int bank_id;
}
