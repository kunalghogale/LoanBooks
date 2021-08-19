package com.affirm.loans.readers;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static <T> List<T> getFileData(final String mode, final Class<T> clazz, final String fileName) throws Exception {
        System.out.println("Reading " + fileName);
        final List<T> output = new ArrayList<>();
        try {
            final CsvMapper csvMapper = new CsvMapper();
            final CsvSchema schema = CsvSchema.emptySchema().withHeader().withLineSeparator("\n").withColumnSeparator(',');
            final Reader reader = new FileReader(mode + "/" + fileName);
            final MappingIterator<T> iterator = csvMapper.reader(clazz).with(schema).readValues(reader);
            iterator.forEachRemaining(output::add);
        } catch (final Exception e) {
            System.out.println("Error reading banks data..." + e.getMessage());
            throw e;
        }
        System.out.println("Done with " + output.size() + " records.");
        return output;
    }
}
