package com.example.springgpt.utils;

import org.apache.commons.csv.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvUtil {
    public static List<CSVRecord> readCsv(String classpathFile) throws IOException {
        try (InputStream input = new ClassPathResource(classpathFile).getInputStream();
             Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim();
            return format.parse(reader).getRecords();
        }
    }
}
