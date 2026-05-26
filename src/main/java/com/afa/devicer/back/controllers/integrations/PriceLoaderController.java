package com.afa.devicer.back.controllers.integrations;

import com.afa.core.dto.BaseResponse;
import com.afa.devicer.back.integrations.suppliers.PricesLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.afa.devicer.back.controllers.internal.ControllerConstants.INTEGRATIONS_SUPPLIERS;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(INTEGRATIONS_SUPPLIERS)
public class PriceLoaderController {

    private final PricesLoaderService allPricerService;

    //http://localhost:8000/api/v8/integrations/suppliers/feed/all
    //http://localhost:8000/api/v8/integrations/suppliers/feed/sititek
    @PostMapping("/feed/all")
    public ResponseEntity<BaseResponse> loadPrices() {

        allPricerService.update();
        return ResponseEntity.ok(new BaseResponse());
    }


}
