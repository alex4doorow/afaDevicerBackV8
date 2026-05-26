package com.afa.devicer.back.services;

import com.afa.devicer.back.integrations.suppliers.PricesLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final PricesLoaderService allPricerService;

    @Scheduled(cron = "${scheduler.integrations.suppliers}")
    public void pricesLoading() {
        allPricerService.update();
    }
}
