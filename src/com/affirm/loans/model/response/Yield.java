package com.affirm.loans.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Yield {
    int facility_id;
    float expected_yield;

    public int getExpected_yield() {
        return Math.round(expected_yield);
    }
}
