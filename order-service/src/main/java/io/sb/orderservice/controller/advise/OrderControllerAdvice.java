package io.sb.orderservice.controller.advise;

import io.sb.orderservice.constants.OrderConstants;
import io.sb.orderservice.exception.OrderFailedException;
import io.sb.orderservice.model.ErrorVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class OrderControllerAdvice {

    @ExceptionHandler(OrderFailedException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public @ResponseBody
    ErrorVO handleOrderException(){
        return ErrorVO.builder().errorCode(OrderConstants.PAYMENT_FAILED_ERROR_CODE).build();
    }
}
