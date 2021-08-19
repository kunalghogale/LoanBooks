## Local Development

Follow the below instructions to run the cli locally.

### Prerequisites
- Install [Maven] (https://maven.apache.org/install.html)

### Building
- mvn clean install

### Running
#### Intellij
- Import project into intellij
- Run Main.java
- This generates output in output dir with small dataset
- To run using large data set click on edit configurations and pass `large` as a command line argument.

#### Command line
- Open command line terminal
- Navigate to LoanBooks
- Run `mvn exec:java  -Dexec.mainClass=com.affirm.loans.Main` for small dataset
- Run `mvn exec:java  -Dexec.mainClass=com.affirm.loans.Main -Dexec.args="large"`

### Questions
1. How long did you spend working on the problem? What did you find to be the most difficult part?
- About 4-5 hours. Some time was making sure the build/annotations worked as expected. Some of it was maintainability. Logic itself was pretty simple.  
2. How would you modify your data model or code to account for an eventual introduction of new, as-of-yet unknown types of covenants, beyond just maximum default likelihood and state restrictions?
- I have built the validator framework. Each new covenant can just extend the Bipredicate and be added to the Set of predicates. No change in core logic required. 
3. How would you architect your solution as a production service wherein new facilities can be introduced at arbitrary points in time. Assume these facilities become available by the finance team emailing your team and describing the addition with a new set of CSVs.
- Ideally I would like to push these facilities in a store like dynamodb and read from there at execution time. If CSVs is the only way the team can support the new facilities, then a S3 bucket to upload these and read from there. We would have a process that validates the files and rejects invalid ones upon upload to avoid runtime failures. 
4. Your solution most likely simulates the streaming process by directly calling a method in your code to process the loans inside of a for loop. What would a REST API look like for this same service? Stakeholders using the API will need, at a minimum, to be able to request a loan be assigned to a facility, and read the funding status of a loan, as well as query the capacities remaining in facilities.
- A store would come in helpful in this situation. We can have multiple APIs: 
  - requestLoan: This will be called to assign loans to facilities. Error if it couldn't be assigned or return a 404 status code. If we want to batch, then we just keep this in a table to be processed at end of day and return 200 to ack the request. 
  - requestStatus - This can pass in a loan id and it will return status as "FUNDED", "NON_FUNDED" along with facility associated with it (if any).
  - queryCapacity - This can pass in a facility id and return amount remaining along with loans already funded and other metada. 
5. How might you improve your assignment algorithm if you were permitted to assign loans in batch rather than streaming? We are not looking for code here, but pseudo code or description of a revised algorithm appreciated.
- Right now the max yield is only based on current loans association with facility. If you could batch process, you can check across loans to see which loans gave maximum yield at which facility. Even perhaps being smart about it by prioritizing larger amounts at best rate facilities. 
6. Discuss your solution’s runtime complexity.
- If there are `l` loans, `b` banks, `f` facilities and `c` covenants at max the runtime right now is O(lbfc) as we have to query each bank's facilities and ensure we meet all their covenants. This has to happen for all loans.  

