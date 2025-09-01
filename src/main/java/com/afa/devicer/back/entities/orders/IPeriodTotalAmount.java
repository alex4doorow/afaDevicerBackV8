package com.afa.devicer.back.entities.orders;

import com.afa.core.enums.AmountTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IPeriodTotalAmount extends JpaRepository<PeriodTotalAmount, Long>, JpaSpecificationExecutor<PeriodTotalAmount> {

    @Query("SELECT SUM(pta.amount) " +
            "FROM PeriodTotalAmount pta " +
            "WHERE pta.amountType = :amountType " +
            "AND pta.startDate = :startDate AND pta.endDate = :endDate")
    BigDecimal sumAmountsByTypeAndPeriod(@Param("amountType") AmountTypes amountType,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);



}