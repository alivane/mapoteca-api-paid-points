package co.mapoteca.controller;

import co.mapoteca.dto.Response;
import co.mapoteca.dto.payment.PaymentStatusDto;
import co.mapoteca.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    ResponseEntity<Void> stripeWebhook(
            @RequestBody String payload,
            HttpServletRequest request) throws Exception {
        String signatureHeader = request.getHeader("Stripe-Signature");
        paymentService.stripeWebhook(payload, signatureHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate/{id}")
    @Operation(summary = "Validate if a payment is completed or succeeded")
    ResponseEntity<Response<PaymentStatusDto>> validatePayment(@PathVariable UUID id) {
        PaymentStatusDto payment = paymentService.validatePayment(id);
        return ResponseEntity.ok(Response.data(payment));
    }
}
