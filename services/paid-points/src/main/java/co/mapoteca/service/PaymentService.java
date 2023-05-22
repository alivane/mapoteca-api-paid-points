package co.mapoteca.service;

import co.mapoteca.config.StripeConfig;
import co.mapoteca.dto.payment.PaymentStatus;
import co.mapoteca.dto.payment.PaymentStatusDto;
import co.mapoteca.entity.Payment;
import co.mapoteca.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.AfterExpiration;
import com.stripe.param.checkout.SessionCreateParams.AfterExpiration.Recovery;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final StripeConfig stripeConfig;
    private final PaymentRepository paymentRepository;
    private final PointUpdateService pointUpdateService;

    @Transactional(rollbackFor = {RuntimeException.class, StripeException.class, SignatureVerificationException.class})
    public Session createSession(Integer pointsQuantity, String currency, UUID paymentId) throws StripeException {
        Stripe.apiKey = stripeConfig.getSecretKey();

        SessionCreateParams params = SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeConfig.getSuccessUrl() + "/" + paymentId.toString())
                .setCancelUrl(stripeConfig.getCancelUrl())
                .addLineItem(createLineItem(pointsQuantity))
                .setCurrency(currency)
                .setAfterExpiration(AfterExpiration
                        .builder()
                        .setRecovery(Recovery.builder().setEnabled(true).build())
                        .build())
                .setExpiresAt(Instant
                        .now()
                        .plus(30, ChronoUnit.MINUTES)
                        .getEpochSecond()) // Session expires in 30 minutes
                .build();

        return Session.create(params);
    }

    public void stripeWebhook(String payload, String signatureHeader) throws Exception {
        Event event = Webhook.constructEvent(payload, signatureHeader, stripeConfig.getWebhookSecret());

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

                String sessionId = getSessionId(Objects.requireNonNull(session));

                Optional<Payment> optionalPayment = paymentRepository.findByPaymentId(sessionId);
                if (optionalPayment.isPresent() && session.getPaymentStatus().equals("paid")) {
                    Payment payment = optionalPayment.get();
                    payment.setStatus(PaymentStatus.SUCCEEDED);
                    payment.setPaymentIntent(session.getPaymentIntent());
                    paymentRepository.save(payment);
                    pointUpdateService.updateStatus(sessionId);
                }
            }
            case "checkout.session.async_payment_succeeded" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                String sessionId = getSessionId(Objects.requireNonNull(session));

                Optional<Payment> optionalPayment = paymentRepository.findByPaymentId(sessionId);
                if (optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    payment.setStatus(PaymentStatus.SUCCEEDED);
                    paymentRepository.save(payment);
                    payment.setPaymentIntent(session.getPaymentIntent());
                    pointUpdateService.updateStatus(sessionId);
                }
            }
            case "checkout.session.async_payment_failed" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                //todo: handle failure
            }
            case "checkout.session.expired" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session == null || session.getAfterExpiration() == null) return;
                Optional<Payment> optionalPayment = paymentRepository.findByPaymentId(getSessionId(Objects.requireNonNull(
                        session)));
                String recoveryUrl = session.getAfterExpiration().getRecovery().getUrl();
                if (recoveryUrl != null && optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    payment.setPaymentRecoveryUrl(recoveryUrl);
                    paymentRepository.save(payment);
                }

            }
        }
    }

    public PaymentStatusDto validatePayment(UUID id) {
        return paymentRepository
                .findById(id, PaymentStatusDto.class)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    /**
     * Refer to <a href="https://stripe.com/docs/payments/checkout/abandoned-carts#track-conversion">Track Conversion</a>
     *
     * @param session Stripe session to search id
     * @return original or new session id
     */
    private String getSessionId(Session session) {
        if (session.getRecoveredFrom() != null) return session.getRecoveredFrom();
        return session.getId();
    }

    private LineItem createLineItem(Integer pointsQuantity) {
        return LineItem.builder().setPrice(stripeConfig.getPriceId()).setQuantity(pointsQuantity.longValue()).build();
    }
}
