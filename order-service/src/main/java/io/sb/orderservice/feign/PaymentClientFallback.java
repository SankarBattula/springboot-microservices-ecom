package io.sb.orderservice.feign;


import io.sb.orderservice.exception.OrderFailedException;
import io.sb.orderservice.model.PaymentResponseVO;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallback implements PaymentClient{

    @Override
    public PaymentResponseVO getPaymentStatus(String orderId) throws OrderFailedException {
        throw new OrderFailedException();
    }
}
