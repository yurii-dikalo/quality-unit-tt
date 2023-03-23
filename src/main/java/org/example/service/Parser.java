package org.example.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.example.model.Data;
import org.example.model.datatype.Query;
import org.example.model.datatype.Record;

public class Parser {
    private static final int DATATYPE_INDEX = 0;
    private static final int SERVICE_INDEX = 1;
    private static final int QUESTION_INDEX = 2;
    private static final int RESPONSE_TYPE_INDEX = 3;
    private static final int DATE_INDEX = 4;
    private static final int WAITING_TIME_INDEX = 5;
    private static final int DATE_FROM_INDEX = 0;
    private static final int DATE_TO_INDEX = 1;
    private static final String DELIMITER = "-";
    private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("d.MM.yyyy"))
            .toFormatter();
    private final List<Query> queries;
    private final List<Record> records;

    public Parser() {
        queries = new ArrayList<>();
        records = new ArrayList<>();
    }

    public List<String> getReport(List<String> lines) {
        parseData(lines);
        return generateReport();
    }

    private void parseData(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!checkDatatypeValidity(line.charAt(DATATYPE_INDEX))) {
                continue;
            }
            if (line.charAt(DATATYPE_INDEX) == 'C') {
                Record record = parseWaitingTimeLine(line);
                record.setIndex(i);
                records.add(record);
            } else if (line.charAt(DATATYPE_INDEX) == 'D') {
                Query query = parseQueryLine(line);
                query.setIndex(i);
                queries.add(query);
            }
        }
    }

    private List<String> generateReport() {
        List<String> report = new ArrayList<>();
        for (Query query : queries) {
            try {
                int result = (int) records.stream()
                        .filter(Record::isValid)
                        .filter(record -> record.getIndex() < query.getIndex())
                        .filter(new ServicePredicate(query.getServiceId(),
                                query.getVariationId()))
                        .filter(new QuestionPredicate(query.getQuestionId(),
                                query.getCategoryId(),
                                query.getSubCategoryId()))
                        .filter(record -> record.getResponseType() == query.getResponseType())
                        .filter(new DatePredicate(query.getFromDate(),
                                query.getToDate()))
                        .mapToInt(Record::getWaitingTime)
                        .average().getAsDouble();
                report.add(String.valueOf(result));
            } catch (NoSuchElementException e) {
                report.add("-");
            }
        }
        return report;
    }

    private Record parseWaitingTimeLine(String line) {
        Record recordLine = new Record();
        recordLine.setValid(true);
        String[] split = line.split(" ");
        if (split.length != 6) {
            recordLine.setValid(false);
            return recordLine;
        }
        recordLine.setDatatype('C');
        setServiceToLine(recordLine, split[SERVICE_INDEX]);
        setQuestionToLine(recordLine, split[QUESTION_INDEX]);
        if (checkResponseTypeValidity(split[RESPONSE_TYPE_INDEX])) {
            recordLine.setResponseType(split[RESPONSE_TYPE_INDEX].charAt(0));
        } else {
            recordLine.setValid(false);
            return recordLine;
        }
        setDateToRecordLine(recordLine, split[DATE_INDEX]);
        setWaitingTimeToRecordLine(recordLine, split[WAITING_TIME_INDEX]);
        return recordLine;
    }

    private Query parseQueryLine(String line) {
        Query query = new Query();
        query.setValid(true);
        String[] split = line.split(" ");
        if (split.length != 5) {
            query.setValid(false);
            return query;
        }
        query.setDatatype('D');
        setServiceToLine(query, split[SERVICE_INDEX]);
        setQuestionToLine(query, split[QUESTION_INDEX]);
        if (checkResponseTypeValidity(split[RESPONSE_TYPE_INDEX])) {
            query.setResponseType(split[RESPONSE_TYPE_INDEX].charAt(0));
        } else {
            query.setValid(false);
            return query;
        }
        setDateFromAndTo(query, split[DATE_INDEX]);
        return query;
    }

    private void setServiceToLine(Data data, String service) {
        String[] serviceSplit = service.split("\\.");
        if (checkAsterisk(serviceSplit[0])) {
            return;
        }
        try {
            data.setServiceId(Integer.parseInt(serviceSplit[0]));
            if (serviceSplit.length > 1) {
                data.setVariationId(Integer.parseInt(serviceSplit[1]));
            }
        } catch (NumberFormatException e) {
            data.setValid(false);
        }
    }

    private void setQuestionToLine(Data data, String question) {
        try {
            String[] questionSplit = question.split("\\.");
            if (checkAsterisk(questionSplit[0])) {
                return;
            }
            data.setQuestionId(Integer.parseInt(questionSplit[0]));
            if (questionSplit.length > 1) {
                data.setCategoryId(Integer.parseInt(questionSplit[1]));
            }
            if (questionSplit.length > 2) {
                data.setSubCategoryId(Integer.parseInt(questionSplit[2]));
            }
        } catch (NumberFormatException e) {
            data.setValid(false);
        }
    }

    private void setDateToRecordLine(Record recordLine, String dateString) {
        LocalDate date = parseDate(dateString);
        if (date == null) {
            recordLine.setValid(false);
            return;
        }
        recordLine.setDate(date);
    }

    private void setWaitingTimeToRecordLine(Record recordLine, String waitingTime) {
        try {
            recordLine.setWaitingTime(Integer.parseInt(waitingTime));
        } catch (NumberFormatException e) {
            recordLine.setValid(false);
        }
    }

    private void setDateFromAndTo(Query query, String period) {
        String[] dates = period.split(DELIMITER);
        LocalDate from = parseDate(dates[DATE_FROM_INDEX]);
        if (from == null || dates.length > 2) {
            query.setValid(false);
            return;
        }
        query.setFromDate(from);
        if (dates.length > 1) {
            LocalDate to = parseDate(dates[DATE_TO_INDEX]);
            if (to == null) {
                query.setValid(false);
                return;
            }
            query.setToDate(to);
            return;
        }
        query.setToDate(LocalDate.now());
    }

    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean checkDatatypeValidity(char charAt) {
        return Arrays.asList('C', 'D')
                .contains(charAt);
    }

    private boolean checkAsterisk(String asterisk) {
        return "*".equals(asterisk);
    }

    private boolean checkResponseTypeValidity(String responseType) {
        return Arrays
                .asList(new String[]{"P", "N"})
                .contains(responseType);
    }

    private class DatePredicate implements Predicate<Record> {
        private final LocalDate fromDate;
        private final LocalDate toDate;

        public DatePredicate(LocalDate fromDate, LocalDate toDate) {
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        @Override
        public boolean test(Record record) {
            return !record.getDate().isBefore(fromDate)
                    && !record.getDate().isAfter(toDate);
        }
    }

    private class ServicePredicate implements Predicate<Record> {
        private final int serviceId;
        private final int variationId;

        private ServicePredicate(int serviceId, int variationId) {
            this.serviceId = serviceId;
            this.variationId = variationId;
        }

        @Override
        public boolean test(Record record) {
            return (serviceId == 0 || serviceId == record.getServiceId())
                    && (variationId == 0 || variationId == record.getVariationId());
        }
    }

    private class QuestionPredicate implements Predicate<Record> {
        private final int questionId;
        private final int categoryId;
        private final int subCategoryId;

        private QuestionPredicate(int questionId, int categoryId, int subCategoryId) {
            this.questionId = questionId;
            this.categoryId = categoryId;
            this.subCategoryId = subCategoryId;
        }

        @Override
        public boolean test(Record record) {
            return (questionId == 0 || questionId == record.getQuestionId())
                    && (categoryId == 0 || categoryId == record.getCategoryId())
                    && (subCategoryId == 0 || subCategoryId == record.getSubCategoryId());
        }
    }
}
