package io.sb.orderservice.repository;

import io.sb.orderservice.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order,String> {

}
