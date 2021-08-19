package com.affirm.loans.model.internal;

import com.affirm.loans.model.request.Bank;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FinancialInstitution {
    Bank bank;
    List<FacilityCovenants> facilityCovenants;
}
