package zm.zra.zra_digital_fortress_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}