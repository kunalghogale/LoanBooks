package com.affirm.loans.processor;

import com.affirm.loans.model.internal.AssignmentYield;
import com.affirm.loans.model.internal.FacilityCovenants;
import com.affirm.loans.model.internal.FinancialInstitution;
import com.affirm.loans.model.request.Bank;
import com.affirm.loans.model.request.Covenant;
import com.affirm.loans.model.request.Facility;
import com.affirm.loans.model.request.Loan;
import com.affirm.loans.model.response.Assignment;
import com.affirm.loans.model.response.Yield;
import com.affirm.loans.validators.DefaultRatePredicate;
import com.affirm.loans.validators.GeographicPredicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class LoansProcessor {

    final Set<BiPredicate<Loan, Covenant>> loanPredicates = new HashSet<BiPredicate<Loan, Covenant>>() {{
        add(new DefaultRatePredicate());
        add(new GeographicPredicate());
    }};

    public AssignmentYield processLoans(final List<Bank> bank, final List<Facility> facilities, final List<Covenant> covenants, final List<Loan> loans) {
        final List<FinancialInstitution> financialInstitutions = buildFinancialInstitutions(bank, facilities, covenants);
        final List<Assignment> assignments = new ArrayList<>();
        final List<Yield> yields = new ArrayList<>();
        final Map<Integer, Yield> facilityYieldMap = new HashMap<>();
        /** For data verification
         final Map<Integer, List<Loan>> facilityLoanMap = new HashMap<>();
         * */

        for (final Loan loan : loans) {
            System.out.println("Processing loan " + loan.getId() + " for amount " + loan.getAmount());

            float maxYield = 0;
            Facility bestFacility = null;

            for (final FinancialInstitution financialInstitution : financialInstitutions) {
                System.out.println("Checking institution with bank " + financialInstitution.getBank().getName());
                final List<FacilityCovenants> facilityCovenants = financialInstitution.getFacilityCovenants();
                for (final FacilityCovenants facilityCovenant : facilityCovenants) {
                    System.out.println("Checking facility " + facilityCovenant.getFacility().getId());
                    if (facilityCovenant.getFacility().getAmount() < loan.getAmount()) {
                        System.out.println("Facility cannot support amount " + facilityCovenant.getFacility().getAmount());
                        continue;
                    }
                    final List<Covenant> cvs = facilityCovenant.getCovenants();
                    System.out.println("Facility has " + cvs.size() + " covenants");
                    boolean allCovenantsPassed = true;
                    for (final Covenant covenant : cvs) {
                        allCovenantsPassed = loanPredicates.stream().map(predicate -> predicate.test(loan, covenant))
                                .reduce(allCovenantsPassed, (a, b) -> a && b);
                        System.out.println("Covenant " + covenant + " passed:" + allCovenantsPassed);
                        if (!allCovenantsPassed) {
                            break;
                        }
                    }
                    if (allCovenantsPassed) {
                        float yield = (1 - loan.getDefault_likelihood()) * loan.getAmount() * loan.getInterest_rate()
                                - loan.getDefault_likelihood() * loan.getAmount()
                                - facilityCovenant.getFacility().getInterest_rate() * loan.getAmount();
                        if (yield > maxYield) {
                            maxYield = yield;
                            bestFacility = facilityCovenant.getFacility();
                        }
                    }

                }
            }
            if (bestFacility != null) {
                assignments.add(Assignment.builder()
                        .loan_id(loan.getId())
                        .facility_id(bestFacility.getId())
                        .build());
                if (facilityYieldMap.containsKey(bestFacility.getId())) {
                    final Yield yield = facilityYieldMap.get(bestFacility.getId());
                    yield.setExpected_yield(yield.getExpected_yield() + maxYield);
                } else {
                    final Yield yield = Yield.builder()
                            .expected_yield(maxYield)
                            .facility_id(bestFacility.getId())
                            .build();
                    yields.add(yield);
                    facilityYieldMap.put(bestFacility.getId(), yield);
                }
                System.out.println("Completed underwriting loan " + loan.getId() + " to facility " + bestFacility.getId() + " for amount " + loan.getAmount());
                System.out.println("Facility " + bestFacility.getId() + " amount before loan " + bestFacility.getAmount());
                bestFacility.setAmount(bestFacility.getAmount() - loan.getAmount());
                System.out.println("Facility " + bestFacility.getId() + " amount reduced to " + bestFacility.getAmount());
                System.out.println("****************************************************************************************");
                /** For data verification
                if (!facilityLoanMap.containsKey(bestFacility.getId())) {
                    facilityLoanMap.put(bestFacility.getId(), new ArrayList<>());
                }
                facilityLoanMap.getOrDefault(bestFacility.getId(), new ArrayList<>()).add(loan);
                 */
            } else {
                System.out.println("Could not fund loan...");
                System.out.println("****************************************************************************************");
            }
        }

        /**
         * For data verification
        for (final Map.Entry<Integer, List<Loan>> facilityListEntry : facilityLoanMap.entrySet()) {
            final List<String> loanIds = facilityListEntry.getValue().stream().map(loan -> String.valueOf(loan.getId())).collect(Collectors.toList());
            System.out.println(facilityListEntry.getKey() + ":" + String.join(",", loanIds));
        }
        System.out.println("****************************************************************************************");

        for (final Facility facility : facilities) {
            System.out.println("Id: " + facility.getId() + " Amount Remaining: " + facility.getAmount());
        }
        System.out.println("****************************************************************************************");

        **/
        return AssignmentYield.builder()
                .assignments(assignments)
                .yields(yields)
                .build();
    }

    private List<FinancialInstitution> buildFinancialInstitutions(final List<Bank> banks, final List<Facility> facilities,
                                                                  final List<Covenant> covenants) {
        final List<FinancialInstitution> financialInstitutions = new ArrayList<>();
        for (final Bank bank : banks) {
            System.out.println("Building institution with bank " + bank.getName());
            final List<FacilityCovenants> facilityCovenants = new ArrayList<>();
            for (final Facility facility : facilities) {
                if (facility.getBank_id() == bank.getId()) {
                    System.out.println("Found facility " + facility.getId());
                    final FacilityCovenants facilityCovenant = FacilityCovenants.builder()
                            .facility(facility)
                            .covenants(covenants.stream().filter(covenant -> covenant.getFacility_id() == facility.getId())
                                    .collect(Collectors.toList()))
                            .build();
                    System.out.println("Found covenants " + facilityCovenant.getCovenants().size());
                    facilityCovenants.add(facilityCovenant);
                }
            }
            financialInstitutions.add(FinancialInstitution.builder()
                    .bank(bank)
                    .facilityCovenants(facilityCovenants)
                    .build());
        }
        System.out.println("Built " + financialInstitutions.size() + " financial institutions");
        System.out.println("****************************************************************************************");
        return financialInstitutions;
    }
}
