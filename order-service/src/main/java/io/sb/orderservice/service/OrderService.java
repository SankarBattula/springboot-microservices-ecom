package io.sb.orderservice.service;


import feign.FeignException;
import io.sb.orderservice.constants.OrderConstants;
import io.sb.orderservice.entity.Order;
import io.sb.orderservice.exception.OrderFailedException;
import io.sb.orderservice.feign.PaymentClient;
import io.sb.orderservice.model.OrderResponseVO;
import io.sb.orderservice.model.OrderVO;
import io.sb.orderservice.model.PaymentResponseVO;
import io.sb.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OrderService {

    @Autowired
    PaymentClient paymentClient;
    @Autowired
    OrderRepository repository;
    @Value("${order-processing-topic}")
    private String orderProcessingTopic;

    @Autowired
    KafkaTemplate kafkaTemplate;

    public OrderService(PaymentClient paymentClient, OrderRepository repository){
        this.paymentClient=paymentClient;
        this.repository = repository;
    }

    public OrderResponseVO createOrder(OrderVO orderVO) throws OrderFailedException {
        PaymentResponseVO responseVO = null;
        try {
            log.info("Calling payment service for order "+orderVO.getOrderId());
            responseVO = paymentClient.getPaymentStatus(orderVO.getOrderId());
        }catch(FeignException e){
            throw new OrderFailedException();
        }
        OrderResponseVO orderResponseVO = OrderResponseVO
                .builder()
                .orderId(orderVO.getOrderId())

                .build();
        if(OrderConstants.PAYMENT_SUCCESS.equals(responseVO.getPaymentStatus())){
            repository.save(Order.builder()
                    .orderId(orderVO.getOrderId())
                    .orderAmount(orderVO.getOrderAmount())
                    .orderStatus(OrderConstants.ORDER_CREATED)
                    .paymentReferenceNumber(responseVO.getPaymentReferenceNumber())
                    .build());
            orderResponseVO.setOrderStatus(OrderConstants.ORDER_CREATED);
        }else{
            repository.save(Order.builder()
                    .orderId(orderVO.getOrderId())
                    .orderAmount(orderVO.getOrderAmount())
                    .orderStatus(OrderConstants.ORDER_PROCESSING_FAILED)
                    .build());
            orderResponseVO.setOrderStatus(OrderConstants.ORDER_PROCESSING_FAILED);
        }
        //Fetch the user details corresponding to the order
        sendNotification(orderResponseVO);
        return orderResponseVO;
    }

    /**
     * Send the message to notification service via kafka
     * @param orderResponseVO
     */
    private void sendNotification(OrderResponseVO orderResponseVO){
        kafkaTemplate.send(
                MessageBuilder.withPayload(orderResponseVO)
                        .setHeader(KafkaHeaders.TOPIC,orderProcessingTopic)
                        .setHeader("EVENT_TYPE",orderResponseVO.getOrderStatus())
                        .build()
        );
    }

    @KafkaListener(topics = "${order-processing-topic}", groupId = "1")
    private void orderProcessingListener(Message<OrderResponseVO> message){
        System.out.println("Recieved message from Kafka "+message.getPayload());
    }

}
