package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.dictionaries.AddressDto;
import com.afa.core.dto.integrations.geo.GeoNamesTimezoneResponseDto;
import com.afa.core.dto.integrations.post.PostCalcParcelDto;
import com.afa.core.dto.integrations.post.PostCalcTariffRequest;
import com.afa.core.dto.integrations.post.PostCalcTariffResponse;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.DevicerErrors;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.integrations.post.PostCalcApiConnector;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class PostDeliveryCalculator extends BaseDeliveryCalculator<PostCalcApiConnector> implements DeliveryCalculator {

    public PostDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final GeoNamesTimezoneResponseDto toAddressTimezone,
            final PostCalcApiConnector connector) {
        super(order, amounts, toAddressTimezone, connector);
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        return postCalc();
    }

    private DeliveryCalcParcelDto postCalc() {

        if (StringUtils.isEmpty(order.getDelivery().getAddress().getPostCode())) {
            return DeliveryCalcParcelDto.createEmpty();
        }

        final PostCalcTariffRequest request = PostCalcTariffRequest.builder()
                .calculateDate(LocalDate.now())
                .totalAmount(amounts.get(AmountTypes.TOTAL))
                .to(AddressDto.builder()
                        .postCode(order.getDelivery().getAddress().getPostCode())
                        .build())
                .paymentType(order.getPaymentType())
                .weightOfG(amounts.get(AmountTypes.TOTAL_WEIGHT_GRAM).intValue())
                .build();
        final PostCalcTariffResponse postCalcTariffResponse = connector.calculateTariff(request);
        return convertPostCalcDataToDeliveryServiceResult(postCalcTariffResponse);
    }

    private DeliveryCalcParcelDto convertPostCalcDataToDeliveryServiceResult(
            final PostCalcTariffResponse response) {

        final DeliveryCalcParcelDto result;
        if (StringUtils.equalsIgnoreCase(response.getStatus(), "BAD_TO_INDEX")) {
            result = DeliveryCalcParcelDto.createEmpty();
            result.setTermText("неизвестно");
            result.setTo(DevicerErrors.INTEGRATION_POST_CALC_TO_INDEX_ERROR.getErrorMessage());
            result.setParcelType(response.getStatus());
            return result;
        } else if (StringUtils.equalsIgnoreCase(response.getStatus(), "ERROR_TESTKEY_LIMIT_50")) {
            result = DeliveryCalcParcelDto.createEmpty();
            result.setTermText(DevicerErrors.INTEGRATION_POST_CALC_TEST_KEY_LIMIT_50_ERROR.getErrorMessage());
            result.setTo("Превышено число попыток");
            result.setParcelType(response.getStatus());
            return result;
        }

        final String parcelDataName = connector.getParcelDataName(order.getDelivery().getDeliveryType());
        final PostCalcParcelDto postCalcParcelDto = response.getParcels().get(parcelDataName);
        if (postCalcParcelDto == null) {
            return DeliveryCalcParcelDto.createEmpty();
        }
        result = DeliveryCalcParcelDto.createEmpty();
        result.setPostpayAmount(postCalcParcelDto.getFullValuation());
        result.setDeliveryAmount(postCalcParcelDto.getDelivery());
        result.setDeliverySellerSummary(postCalcParcelDto.getDelivery());
        result.setDeliveryCustomerSummary(postCalcParcelDto.getDelivery());
        result.setTermText(postCalcParcelDto.getDeliveryTerm());
        result.setTo(response.getTo().getIndex() + ", " + response.getTo().getAddress());
        result.setParcelType(postCalcParcelDto.getName());
        result.setWeightText(amounts.get(AmountTypes.TOTAL_WEIGHT_GRAM).intValue() + " г.");
        return result;
    }
}
