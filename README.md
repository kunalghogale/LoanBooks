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
