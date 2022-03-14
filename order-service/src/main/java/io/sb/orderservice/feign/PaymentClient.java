package io.sb.orderservice.feign;


import io.sb.orderservice.exception.OrderFailedException;
import io.sb.orderservice.model.PaymentResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Primary
@FeignClient(name = "payment-client", url = "${payment-service-url}", fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @GetMapping("/v1/payments/status")
    public PaymentResponseVO getPaymentStatus(@RequestParam String orderId) throws OrderFailedException;
}
