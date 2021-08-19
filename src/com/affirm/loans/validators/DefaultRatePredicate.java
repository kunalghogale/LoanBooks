package com.affirm.loans.validators;

import com.affirm.loans.model.request.Covenant;
import com.affirm.loans.model.request.Loan;
import java.util.function.BiPredicate;

public class DefaultRatePredicate implements BiPredicate<Loan, Covenant> {
    @Override
    public boolean test(final Loan loan, final Covenant covenant) {
        if (covenant.getMax_default_likelihood() == null || covenant.getMax_default_likelihood() >= loan.getDefault_likelihood()) {
            System.out.println("Loan default rate acceptable");
            return true;
        }
        System.out.println("Loan default rate too high");
        return false;
    }
}
