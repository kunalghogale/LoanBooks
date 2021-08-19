package com.affirm.loans.writers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class CSVWriter {

    public static <T> void writeFile(final String fileName, final Class<T> clazz, final List<T> data) throws Exception {
        final CsvMapper mapper = new CsvMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        final CsvSchema.Builder schemaBuilder = CsvSchema.builder().setUseHeader(true);
        for (final Field field : clazz.getDeclaredFields()) {
            schemaBuilder.addColumn(field.getName());
        }
        final CsvSchema schema = schemaBuilder.build().withLineSeparator("\n").withColumnSeparator(',');
        final ObjectWriter writer = mapper.writerFor(clazz).with(schema);
        writer.writeValues(new File("output/" + fileName)).writeAll(data);
    }
}
