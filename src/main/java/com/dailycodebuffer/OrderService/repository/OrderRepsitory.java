package com.dailycodebuffer.OrderService.repository;

import com.dailycodebuffer.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepsitory extends JpaRepository<Order,Long> {

}
