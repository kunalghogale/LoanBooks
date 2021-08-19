package com.affirm.loans.validators;

import com.affirm.loans.model.request.Covenant;
import com.affirm.loans.model.request.Loan;
import java.util.function.BiPredicate;

public class GeographicPredicate implements BiPredicate<Loan, Covenant> {
    @Override
    public boolean test(final Loan loan, final Covenant covenant) {
        if (!covenant.getBanned_state().equalsIgnoreCase(loan.getState())) {
            System.out.println("Loan geography acceptable");
            return true;
        }
        System.out.println("Loan banned in state");
        return false;
    }
}
