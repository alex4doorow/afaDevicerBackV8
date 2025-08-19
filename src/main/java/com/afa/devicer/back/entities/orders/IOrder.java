package com.afa.devicer.back.entities.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@SuppressWarnings({"PMD.MethodNamingConventions"})
public interface IOrder extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByOrderNum(Long orderNum);
    List<Order> findByDeliveryTrackCode(String trackCode);

    /**
     * CUSTOMER PERSON
     * @param phoneNumber
     * @return
     */
    List<Order> findByCustomerPersonIsNotNullAndCustomerPersonPhoneNumber(String phoneNumber);

    List<Order> findByCustomerPersonIsNotNullAndCustomerPersonEmail(String email);

    /**
     * CUSTOMER COMPANY
     * @param email
     * @return
     */
    List<Order> findByCustomerCompanyIsNotNullAndCustomerCompanyEmail(String email);

    List<Order> findByCustomerCompanyIsNotNullAndCustomerCompanyPhoneNumber(String phoneNumber);
}
