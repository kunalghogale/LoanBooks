package com.affirm.loans.model.request;

import lombok.Data;

@Data
public class Covenant {
    int facility_id;
    Float max_default_likelihood;
    int bank_id;
    String banned_state;
}
