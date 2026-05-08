package com.afa.devicer.back.entities.orders;

import com.afa.core.enums.OrderStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings({"PMD.MethodNamingConventions"})
public interface IOrder extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("select max(o.orderNum) from Order o")
    Long findMaxOrderNum();
    List<Order> findByOrderNum(Long orderNum);

    boolean existsByOrderNumAndIdNot(Long orderNum, Long id);

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

    @Query("select MIN(o.orderDate) " +
            "from Order o " +
            "where o.postpayAmount > 0 " +
            "and o.status not in :excludedStatuses")
    LocalDate findMinOrderDateWithPostpayExcludingStatuses(@Param("excludedStatuses") List<OrderStatusTypes> excludedStatuses);

}
