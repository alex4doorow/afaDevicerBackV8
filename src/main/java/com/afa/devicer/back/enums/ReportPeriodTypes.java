package com.afa.devicer.back.enums;

import com.afa.devicer.back.utils.DateHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum ReportPeriodTypes {

    CURRENT_MONTH(1, "текущий месяц", "месяц"),
    CURRENT_QUARTER(2, "текущий квартал", "квартал"),
    CURRENT_HALF_YEAR(105, "текущее полугодие", "полугодие"),
    CURRENT_YEAR(3, "текущий год", "год"),

    ANY_MONTH(4, "произвольный месяц", "месяц"),
    ANY_QUARTER(102, "произвольный квартал", "квартал"),
    ANY_HALF_YEAR(103, "произвольное полугодие", "полугодие"),
    ANY_YEAR(104, "произвольный год", "год"),
    ANY_PERIOD(5, "произвольный период", ""),
    PRIOR_MONTH(6, "предыдущий месяц", ""),
    LAST_7_DAYS(11, "последние 7 дней", ""),
    LAST_30_DAYS(12, "последние 30 дней", ""),
    LAST_90_DAYS(13, "последние 90 дней", "");

    private final int id;
    private final String annotation;
    private final String text;

    public static ReportPeriodTypes[] getListOrderValues() {
        final Set<ReportPeriodTypes> result = new HashSet<>();

        result.add(CURRENT_MONTH);
        result.add(CURRENT_QUARTER);
        result.add(CURRENT_HALF_YEAR);
        result.add(CURRENT_YEAR);
        result.add(ANY_MONTH);
        result.add(ANY_PERIOD);
        result.add(PRIOR_MONTH);
        result.add(LAST_7_DAYS);
        result.add(LAST_30_DAYS);
        result.add(LAST_90_DAYS);
        return result.toArray(new ReportPeriodTypes[0]);
    }

    public static ReportPeriodTypes[] getReportValues() {
        final Set<ReportPeriodTypes> result = new HashSet<>();

        result.add(ANY_MONTH);
        result.add(ANY_QUARTER);
        result.add(ANY_HALF_YEAR);
        result.add(ANY_YEAR);
        return result.toArray(new ReportPeriodTypes[0]);
    }

    public static Pair<LocalDate, LocalDate> findPeriodByType(final ReportPeriodTypes type) {
        final LocalDate today = LocalDate.now();

        final LocalDate start;
        final LocalDate end;

        if (type == CURRENT_MONTH) {
            start = DateHelper.firstDayOfMonth(today);
            end = DateHelper.lastDayOfMonth(start);
        } else if (type == PRIOR_MONTH) {
            final DateHelper.LocalDateRange localDateRange = DateHelper.previousMonth();
            start = localDateRange.from();
            end = localDateRange.to();
        } else if (type == CURRENT_QUARTER) {
            start = DateHelper.firstDayOfQuarter(today);
            end = DateHelper.lastDayOfQuarter(start);
        } else if (type == CURRENT_YEAR) {
            start = DateHelper.firstDayOfYear(today);
            end = DateHelper.lastDayOfYear(start);
        } else if (type == LAST_7_DAYS) {
            final DateHelper.LocalDateRange localDateRange = DateHelper.last7Days();
            start = localDateRange.from();
            end = localDateRange.to();
        } else if (type == LAST_30_DAYS) {
            final DateHelper.LocalDateRange localDateRange = DateHelper.last30Days();
            start = localDateRange.from();
            end = localDateRange.to();
        } else if (type == LAST_90_DAYS) {
            final DateHelper.LocalDateRange localDateRange = DateHelper.last90Days();
            start = localDateRange.from();
            end = localDateRange.to();
        } else if (type == CURRENT_HALF_YEAR) {
            start = DateHelper.firstDayOfHalfYear(today);
            end = DateHelper.lastDayOfHalfYear(start);
        } else {
            start = today;
            end = today;
        }
        return Pair.of(start, end);
    }
}
