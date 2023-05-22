package co.mapoteca.service;

import co.mapoteca.config.DirectoPagoConfig;
import co.mapoteca.config.GeneralConfig;
import co.mapoteca.dto.directopago.*;
import co.mapoteca.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
@Slf4j
public class DirectoPagoService {
    private final PaymentRepository paymentRepository;

    private final DirectoPagoConfig directoPagoConfig;

    private final PointService pointService;
    private final PointUpdateService pointUpdateService ;
    private final GeneralConfig generalConfig;

    private final RestTemplate restTemplate;


    public DirectoPagoService(
            DirectoPagoConfig directoPagoConfig,
            @Lazy PointService pointService,
            GeneralConfig generalConfig,
            RestTemplate restTemplate,
            PaymentRepository paymentRepository, PointUpdateService pointUpdateService) {
        this.directoPagoConfig = directoPagoConfig;
        this.pointService = pointService;
        this.generalConfig = generalConfig;
        this.restTemplate = restTemplate;
        this.paymentRepository = paymentRepository;
        this.pointUpdateService = pointUpdateService;
    }

    @Transactional
    public DirectoPagoCheckoutResponse createCheckoutUrl(
            DirectoPagoCheckoutItemDto dto, UUID orderId) throws Exception {

        DirectoPagoCheckoutBody checkoutBody = DirectoPagoCheckoutBody
                .builder()
                .amount(dto.getPrice().toString())
                .currency("USD")
//                .country(dto.getCountryCode())
                .orderId(orderId.toString())
                .successUrl(generalConfig.getClientUrl() + "/map")
                .backUrl(generalConfig.getClientUrl() + "/map")
                .notificationUrl(generalConfig.getServerUrl() + "/directopago/webhook")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + directoPagoConfig.getBearer());
        HttpEntity<DirectoPagoCheckoutBody> requestEntity = new HttpEntity<>(checkoutBody, headers);

        ResponseEntity<DirectoPagoCheckoutResponse> response = restTemplate.exchange(directoPagoConfig.getBaseUrl() + "v1/payments",
                HttpMethod.POST,
                requestEntity,
                DirectoPagoCheckoutResponse.class);

        if (response.getStatusCode().value() != 200) throw new Exception("No se pudo realizar el pago");

        return response.getBody();
    }

    public void webhook(HttpServletRequest request, DirectoPagoWebhook directoPagoWebhook) throws Exception {
//        log.info("Webhook de DirectoPago");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + directoPagoConfig.getBearer());
//        HttpEntity<DirectoPagoCheckoutResponse> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<DirectoPagoCheckoutResponse> response = restTemplate.exchange(directoPagoConfig.getBaseUrl() + "v1/payments/" + directoPagoWebhook.getPaymentId(),
//                HttpMethod.GET,
//                requestEntity,
//                DirectoPagoCheckoutResponse.class);
//
//        DirectoPagoCheckoutResponse body = response.getBody();
//        assert body != null;
//
//        log.info("transactionId {}", body.getOrderId());
//        log.info("updateStatus {}", body.getStatus());
//
//        Payment payment = paymentRepository
//                .findByTransactionId(UUID.fromString(body.getOrderId()))
//                .orElseThrow(() -> new Exception("payment not found"));
////        payment.setStatus(String.valueOf(DirectoPagoStatus.PAID));
//        paymentRepository.save(payment);
//
//        try {
//            pointUpdateService.updateStatus(request, body.getOrderId());
//        } catch (Exception e) {
//            log.info("Capturing point failed");
//            log.info(e.getMessage());
//        }
    }
}
