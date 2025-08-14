package com.afa.devicer.back.entities.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IOrderStatusHistory extends JpaRepository<OrderStatusHistory, Long>, JpaSpecificationExecutor<OrderStatusHistory> {

}
