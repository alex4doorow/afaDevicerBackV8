package com.afa.devicer.back.services;

import com.afa.core.enums.AmountTypes;
import com.afa.devicer.back.entities.orders.IPeriodTotalAmount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodTotalAmountService {

    private final IPeriodTotalAmount iPeriodTotalAmount;

    public BigDecimal ejectTotalAmountsByConditions(
            final AmountTypes amountType,
            final Pair<LocalDate, LocalDate> period) {

        final BigDecimal result = iPeriodTotalAmount.sumAmountsByTypeAndPeriod(amountType, period.getFirst(), period.getSecond());
        return result == null ? BigDecimal.ZERO : result;
    }
}
