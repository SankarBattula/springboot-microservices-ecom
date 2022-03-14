package io.sb.orderservice.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class Order {
    private String orderId;
    private String orderStatus;
    private Double orderAmount;
    private String paymentReferenceNumber;

}
