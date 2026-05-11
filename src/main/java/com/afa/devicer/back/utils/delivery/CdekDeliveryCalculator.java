package com.afa.devicer.back.utils.delivery;

import com.afa.core.dto.delivery.DeliveryCalcParcelDto;
import com.afa.core.dto.integrations.cdek.CdekCalcPackageRequestDto;
import com.afa.core.dto.integrations.cdek.CdekCalculatorLocationDto;
import com.afa.core.dto.integrations.cdek.CdekCalculatorTariffRequest;
import com.afa.core.dto.integrations.cdek.CdekCalculatorTariffResponse;
import com.afa.core.enums.*;
import com.afa.core.exceptions.DevicerException;
import com.afa.core.utils.NumericHelper;
import com.afa.devicer.back.entities.orders.Order;
import com.afa.devicer.back.integrations.cdek.CdekApiConnector;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

@Slf4j
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal"})
public class CdekDeliveryCalculator extends BaseDeliveryCalculator<CdekApiConnector> implements DeliveryCalculator {

    private static final String CDEK_ADAPTER_PVZ_ECONOMY_ERROR = "Невозможно осуществить доставку по этому направлению при заданных условиях";

    public CdekDeliveryCalculator(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final CdekApiConnector anyConnector) {
        super(order, amounts, anyConnector);
    }

    @Override
    public DeliveryCalcParcelDto calc() {
        final DeliveryPaymentMethods deliveryPaymentMethod = getDeliveryPaymentMethod(order);
        final BigDecimal totalAmount = amounts.get(AmountTypes.TOTAL);

        boolean isDmcPvz;
        boolean isDmcEconomyPvz;
        if (isDeliveryToMoscow(order)) {
            isDmcPvz = true;
            isDmcEconomyPvz = false;
        } else {
            try {
                final DeliveryCalcParcelDto fakeResultEconomy = calcCdekByApiTariff(order, amounts, DeliveryTypes.CDEK_PVZ_ECONOMY);
                if (CDEK_ADAPTER_PVZ_ECONOMY_ERROR.equalsIgnoreCase(fakeResultEconomy.getErrorText())) {
                    isDmcPvz = true;
                    isDmcEconomyPvz = false;
                } else {
                    isDmcPvz = false;
                    isDmcEconomyPvz = true;
                }
            } catch (DevicerException e) {
                isDmcPvz = false;
                isDmcEconomyPvz = false;
                log.info(DevicerErrors.ORDER_DELIVERY_CALCULATE_CDEK_ECONOMY_NOT_EXIST.getErrorMessage());
            }
        }
        try {
            final DeliveryCalcParcelDto result = calcCdekByApiTariff(order, amounts, order.getDelivery().getDeliveryType());

            //GeoNamesApi.GeoNamesBean geoNamesBean = geoNamesApi.getLocalTimeByCity(to.getAddress(), new Date());
            //result.setLocalTimeText(geoNamesBean.textLocalTime());

            BigDecimal deliveryPrice = result.getDeliverySellerSummary();
            if (order.getDelivery().getDeliveryPaymentType() == DeliveryPaymentTypes.CUSTOMER) {

                if (order.getPaymentType() == OrderPaymentTypes.POSTPAY) {
                    if (deliveryPaymentMethod == DeliveryPaymentMethods.FULL) {
                        // FULL
                        deliveryPrice = result.getDeliverySellerSummary();

                    } else if (deliveryPaymentMethod == DeliveryPaymentMethods.PVZ_FREE) {
                        // PVZ
                        deliveryPrice = result.getDeliverySellerSummary();

                    } else {
                        // CURRENT
                        deliveryPrice = result.getDeliveryCustomerSummary();
                    }

                } else {
                    deliveryPrice = result.getDeliveryPrice();
                }
            }

            if (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_PVZ_TYPICAL && isDeliveryToMoscow(order)) {

                if (deliveryPaymentMethod == DeliveryPaymentMethods.FULL) {
                    //deliveryPrice = result.getDeliverySellerSummary();

                    if (deliveryPrice.compareTo(MOSCOW_PARCEL_DELIVERY_PRICE) < 0) {
                        deliveryPrice = MOSCOW_PARCEL_DELIVERY_PRICE;
                    }

                } else if (deliveryPaymentMethod == DeliveryPaymentMethods.CURRENT) {
                    deliveryPrice = MOSCOW_PARCEL_DELIVERY_PRICE;
                    if (totalAmount.compareTo(MIN_GOOD_MOSCOW_PARCEL_IS_FREE) > 0) {
                        deliveryPrice = BigDecimal.ZERO;
                    }
                }
                result.setDeliveryFullPrice(deliveryPrice);
            } else if (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_COURIER && isDeliveryToMoscow(order)) {
                deliveryPrice = DeliveryPriceTypes.COURIER_MOSCOW_TYPICAL.getPrice();
                if (totalAmount.compareTo(MIN_GOOD_MOSCOW_COURIER_IS_FREE) > 0) {
                    deliveryPrice = BigDecimal.ZERO;
                }
                result.setDeliveryFullPrice(deliveryPrice);
                result.setDeliveryCustomerSummary(deliveryPrice);
            } else if (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_PVZ_TYPICAL && isDmcPvz
                    && deliveryPaymentMethod == DeliveryPaymentMethods.PVZ_FREE) {
                deliveryPrice = BigDecimal.ZERO;
            } else if (order.getDelivery().getDeliveryType() == DeliveryTypes.CDEK_PVZ_ECONOMY && isDmcEconomyPvz
                    && deliveryPaymentMethod == DeliveryPaymentMethods.PVZ_FREE) {
                deliveryPrice = BigDecimal.ZERO;
            }
            result.setDeliveryFullPrice(deliveryPrice);
            result.setDeliveryCustomerSummary(deliveryPrice);
            return result;

        } catch (DevicerException e) {
            log.error(DevicerErrors.ORDER_DELIVERY_CALCULATE_ERROR.getErrorMessage(), e);
        }
        return DeliveryCalcParcelDto.createEmpty();
    }

    private DeliveryCalcParcelDto calcCdekByApiTariff(
            final Order order,
            final Map<AmountTypes, BigDecimal> amounts,
            final DeliveryTypes deliveryType) {

        final Integer cdekTariffId = connector.getCdekTariffId(deliveryType);

        final CdekCalculatorTariffRequest request = CdekCalculatorTariffRequest.builder()
                .date(order.getOrderDate()
                        .atStartOfDay()
                        .atOffset(ZoneOffset.systemDefault()
                                .getRules()
                                .getOffset(Instant.now())))
                .type(1)
                .tariffCode(cdekTariffId)
                .fromLocation(CdekCalculatorLocationDto.builder()
                        .code(connector.getFromLocationCityCode())
                        .build())
                .toLocation(CdekCalculatorLocationDto.builder()
                        .code(Integer.valueOf(order.getDelivery().getAddress().getCityCode()))
                        .build())
                .packages(Set.of(CdekCalcPackageRequestDto.builder()
                        .weight(amounts.get(AmountTypes.TOTAL_WEIGHT_GRAM).intValue())
                        .length(amounts.get(AmountTypes.TOTAL_LENGTH_CM).intValue())
                        .width(amounts.get(AmountTypes.TOTAL_WIDTH_CM).intValue())
                        .height(amounts.get(AmountTypes.TOTAL_HEIGHT_CM).intValue())
                        .build()))
                .build();
        final CdekCalculatorTariffResponse tariffResponse = connector.calculateTariff(request);
        return convertCdekTariffDataToDeliveryServiceResult(tariffResponse, cdekTariffId);
    }

    private boolean isDeliveryToMoscow(final Order order) {
        return String.valueOf(connector.getFromLocationCityCode()).equals(order.getDelivery().getAddress().getCityCode());
    }

    private DeliveryCalcParcelDto convertCdekTariffDataToDeliveryServiceResult(
            final CdekCalculatorTariffResponse tariffResponse,
            final Integer cdekTariffId) {

        if (tariffResponse == null || tariffResponse.getTotalSum() == null) {
            return DeliveryCalcParcelDto.createEmpty();
        }
        final BigDecimal totalAmount = amounts.get(AmountTypes.TOTAL);
        final BigDecimal deliveryPrice;
        final BigDecimal deliveryFullPrice;

        BigDecimal deliverySellerSummary;
        BigDecimal deliveryCustomerSummary;

        final BigDecimal deliveryInsurance;
        BigDecimal deliveryPostpayFee = BigDecimal.ZERO;
        BigDecimal postpayAmount = BigDecimal.ZERO;
        final Integer deliveryPeriodMin;
        final Integer deliveryPeriodMax;
        final String errorText = "";

        deliveryPeriodMin = tariffResponse.getPeriodMin();
        deliveryPeriodMax = tariffResponse.getPeriodMax();
        deliveryPrice = tariffResponse.getDeliverySum();
        deliveryInsurance = totalAmount.multiply(new BigDecimal("0.75")).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // страховка = (0.75 * totalAmount) / 100
        // комиссия за наложку = (totalAmount + стоимость доставки) * 0,03
        // доставка = price + страховка + комиссия за наложку

        final boolean isPostpay;
        if (order.getType() == OrderTypes.ORDER) {
            isPostpay = order.getPaymentType() == OrderPaymentTypes.POSTPAY;
        } else {
            isPostpay = true;
        }
        final boolean isPaySeller = order.getDelivery().getDeliveryPaymentType() == DeliveryPaymentTypes.SELLER;

        if (isPostpay) {

            deliverySellerSummary = calcDeliverySellerSummary(
                    totalAmount,
                    deliveryPrice,
                    deliveryInsurance,
                    deliveryPostpayFee
            );

            deliverySellerSummary = deliverySellerSummary.round(new MathContext(2, RoundingMode.HALF_UP));
            deliveryPostpayFee = deliveryPostpayFee.round(new MathContext(2, RoundingMode.HALF_UP));
            deliveryCustomerSummary = deliverySellerSummary.subtract(deliveryPostpayFee);
            if (isPaySeller) {
                postpayAmount = totalAmount.subtract(deliveryPrice).subtract(deliveryInsurance).subtract(deliveryPostpayFee);
                deliveryCustomerSummary = BigDecimal.ZERO;
                deliveryFullPrice = deliverySellerSummary;
            } else {
                postpayAmount = totalAmount.add(deliveryCustomerSummary).subtract(deliveryPrice).subtract(deliveryInsurance).subtract(deliveryPostpayFee);
                deliveryFullPrice = deliveryCustomerSummary;
            }
        } else {
            deliveryCustomerSummary = deliveryPrice.add(deliveryInsurance);
            deliverySellerSummary = deliveryPrice.add(deliveryInsurance).round(new MathContext(0, RoundingMode.HALF_UP)).setScale(0, RoundingMode.CEILING);
            deliveryFullPrice = deliveryCustomerSummary;
        }

        final DeliveryCalcParcelDto result = new DeliveryCalcParcelDto();
        result.setDeliveryAmount(deliveryCustomerSummary);
        result.setDeliveryPrice(deliveryPrice);
        result.setDeliveryInsurance(deliveryInsurance);
        result.setDeliveryPostpayFee(deliveryPostpayFee);

        result.setDeliveryFullPrice(deliveryFullPrice);
        result.setDeliverySellerSummary(deliverySellerSummary);
        result.setDeliveryCustomerSummary(deliveryCustomerSummary);
        result.setDeliveryPeriodMin(deliveryPeriodMin);
        result.setDeliveryPeriodMax(deliveryPeriodMax);
        result.setPostpayAmount(postpayAmount);
        if (deliveryPeriodMin.compareTo(deliveryPeriodMax) == 0) {
            result.setTermText(deliveryPeriodMax + " дн.");
        } else {
            result.setTermText(deliveryPeriodMin + "-" + deliveryPeriodMax + " дн.");
        }
        result.setParcelType("tariffId: " + cdekTariffId);
        result.setTo("receiverCityCode: " + order.getDelivery().getAddress().getCityCode());
        result.setWeightText(NumericHelper.weightG2Kg(amounts.get(AmountTypes.TOTAL_WEIGHT_GRAM).intValue()) + " кг.");
        result.setErrorText(errorText);

        return result;
    }

    private BigDecimal calcDeliverySellerSummary(
            final BigDecimal totalAmount,
            final BigDecimal deliveryPrice,
            final BigDecimal deliveryInsurance,
            final BigDecimal deliveryPostpayFee) {

        BigDecimal currentDeliveryPostpayFee = deliveryPostpayFee;
        BigDecimal oldPostpayAmount = BigDecimal.ZERO;
        BigDecimal deliverySellerSummary = BigDecimal.ZERO;

        for (int ii = 0; ii < 10; ii++) {

            final BigDecimal newDeliverySellerSummary = deliveryPrice.add(deliveryInsurance).add(currentDeliveryPostpayFee);
            final BigDecimal newPostpayAmount = totalAmount.add(newDeliverySellerSummary);
            final BigDecimal delta = newPostpayAmount.subtract(oldPostpayAmount);

            if (delta.abs().compareTo(BigDecimal.ONE) < 0) {
                deliverySellerSummary = newDeliverySellerSummary;
                break;
            }

            currentDeliveryPostpayFee = totalAmount.add(newDeliverySellerSummary)
                    .multiply(BigDecimal.valueOf(3))
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            oldPostpayAmount = newPostpayAmount;
        }
        return deliverySellerSummary;
    }
}
