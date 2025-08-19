package com.afa.devicer.back.utils;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

@SuppressWarnings("PMD.TooManyMethods")
public final class DateHelper {

    private DateHelper() {
    }

    // ===== МЕСЯЦ =====
    public static LocalDate firstDayOfMonth(final LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate lastDayOfMonth(final LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    // ===== КВАРТАЛ =====
    @SuppressWarnings("PMD.UselessParentheses")
    public static LocalDate firstDayOfQuarter(final LocalDate date) {
        final int firstMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
        return LocalDate.of(date.getYear(), firstMonth, 1);
    }

    public static LocalDate lastDayOfQuarter(final LocalDate date) {
        return firstDayOfQuarter(date).plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    }

    // ===== ГОД =====
    public static LocalDate firstDayOfYear(final LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate lastDayOfYear(final LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    // ===== ПОЛУГОДИЕ =====
    public static LocalDate firstDayOfHalfYear(final LocalDate date) {
        return date.getMonthValue() <= 6
                ? LocalDate.of(date.getYear(), Month.JANUARY, 1)
                : LocalDate.of(date.getYear(), Month.JULY, 1);
    }

    public static LocalDate lastDayOfHalfYear(final LocalDate date) {
        return date.getMonthValue() <= 6
                ? LocalDate.of(date.getYear(), Month.JUNE, 30)
                : LocalDate.of(date.getYear(), Month.DECEMBER, 31);
    }

    // ===== НЕДЕЛЯ (по ISO: понедельник–воскресенье) =====
    public static LocalDate firstDayOfWeek(final LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    public static LocalDate lastDayOfWeek(final LocalDate date) {
        return date.with(DayOfWeek.SUNDAY);
    }

    // ===== ДИАПАЗОНЫ =====
    public static LocalDate daysAgo(final int days) {
        return LocalDate.now().minusDays(days);
    }

    public static LocalDateRange lastNDays(final int days) {
        return new LocalDateRange(LocalDate.now().minusDays(days - 1), LocalDate.now());
    }

    public static LocalDateRange last7Days() {
        return lastNDays(7);
    }

    public static LocalDateRange last30Days() {
        return lastNDays(30);
    }

    public static LocalDateRange last90Days() {
        return lastNDays(90);
    }

    public static LocalDateRange previousMonth() {
        final LocalDate now = LocalDate.now().minusMonths(1);
        return new LocalDateRange(firstDayOfMonth(now), lastDayOfMonth(now));
    }

    // ===== диапазон =====
    public record LocalDateRange(LocalDate from, LocalDate to) {
    }
}
