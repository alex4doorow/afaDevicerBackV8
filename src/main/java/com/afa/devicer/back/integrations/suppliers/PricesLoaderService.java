package com.afa.devicer.back.integrations.suppliers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricesLoaderService {

    private final List<PricerLoaderIF> pricerLoaders;

    @Transactional
    public void update(){
        for (final PricerLoaderIF pricerLoader : pricerLoaders) {
            pricerLoader.update();
        }
    }
}
