package com.kemboishoppingcartapp.orderservice.repository;

import com.kemboishoppingcartapp.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
