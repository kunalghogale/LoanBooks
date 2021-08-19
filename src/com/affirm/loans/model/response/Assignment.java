package com.affirm.loans.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Assignment {
    int loan_id;
    int facility_id;
}
