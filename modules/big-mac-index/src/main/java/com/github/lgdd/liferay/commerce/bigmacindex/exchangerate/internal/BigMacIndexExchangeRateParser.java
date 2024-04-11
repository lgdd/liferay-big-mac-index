package com.github.lgdd.liferay.commerce.bigmacindex.exchangerate.internal;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.osgi.service.component.annotations.Component;

@Component(service = BigMacIndexExchangeRateParser.class)
public class BigMacIndexExchangeRateParser {

  Map<String, BigDecimal> toMap(String csvAsString) throws IOException {
    final Reader rawIndexData = new StringReader(csvAsString);
    final Iterable<CSVRecord> rawIndexRecords = CSVFormat.Builder.create(
    ).setHeader(
    ).build(
    ).parse(
            rawIndexData
    );
    final int yearDiff = csvAsString.contains(
            LocalDate.now(
            ).getYear() + "-") ? 0 : 1;

    final Map<String, BigDecimal> rawIndexMap = new HashMap<>();
    StreamSupport.stream(
            rawIndexRecords.spliterator(), false
    ).filter(
            record ->
                    LocalDate.parse(
                            record.get("date")
                    ).getYear() >=
                            (LocalDate.now(
                            ).getYear() - yearDiff)
    ).sorted(
            Comparator.comparing(record -> LocalDate.parse(record.get("date")))
    ).forEach(
            record -> rawIndexMap.put(
                    record.get(
                            "currency_code"
                    ).toUpperCase(),
                    new BigDecimal(record.get("dollar_ex")))
    );

    return rawIndexMap;
  }

}