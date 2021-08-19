package com.affirm.loans;

import com.affirm.loans.model.internal.AssignmentYield;
import com.affirm.loans.model.request.Bank;
import com.affirm.loans.model.request.Covenant;
import com.affirm.loans.model.request.Facility;
import com.affirm.loans.model.request.Loan;
import com.affirm.loans.model.response.Assignment;
import com.affirm.loans.model.response.Yield;
import com.affirm.loans.processor.LoansProcessor;
import com.affirm.loans.readers.CSVReader;
import com.affirm.loans.writers.CSVWriter;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final String mode = args == null || args.length == 0 ? "small" : args[0];
        final LoansProcessor loansProcessor = new LoansProcessor();

        try {
            System.out.println("Starting to read data...");
            final List<Bank> banks = CSVReader.getFileData(mode, Bank.class, "banks.csv");
            final List<Covenant> covenants = CSVReader.getFileData(mode, Covenant.class, "covenants.csv");
            final List<Facility> facilities = CSVReader.getFileData(mode, Facility.class, "facilities.csv");
            final List<Loan> loans = CSVReader.getFileData(mode, Loan.class, "loans.csv");
            System.out.println("Reading all data done...");
            System.out.println("****************************************************************************************");
            final AssignmentYield assignmentYield = loansProcessor.processLoans(banks, facilities, covenants, loans);
            System.out.println("****************************************************************************************");
            CSVWriter.writeFile("assignments.csv", Assignment.class, assignmentYield.getAssignments());
            CSVWriter.writeFile("yields.csv", Yield.class, assignmentYield.getYields());
        } catch (final Exception e) {
            System.out.println("Error handling loans. Process failing.");
            e.printStackTrace();
        }

    }
}
