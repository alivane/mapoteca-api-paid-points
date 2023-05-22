package co.mapoteca.repository;

import co.mapoteca.dto.payment.PaymentStatus;
import co.mapoteca.dto.point.PointDto;
import co.mapoteca.dto.directopago.DirectoPagoStatus;
import co.mapoteca.entity.Payment;
import co.mapoteca.entity.Point;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PointRepositoryTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @Sql(value = "classpath:sql/data.sql")
    void itShouldGetAllPointsPayedByOrganizationId() {
        //given
        LocalDate now = LocalDate.now();
        UUID organizationId = UUID.fromString("0cb13956-dd21-4b10-8846-74cc735fd842");

        Point point = Point.builder()
                .latitude(2.0023123)
                .longitude(-75.009213123)
                .address("Av. 123")
                .deleted(false)
                .organizationId(organizationId)
                .build();

        Payment payment = Payment.builder()
                .status(DirectoPagoStatus.PAID.toString())
                .createdAt(LocalDateTime.now().minusMonths(2))
                .transactionId(UUID.randomUUID())
                .total(BigDecimal.valueOf(40))
                .build();

        pointRepository.save(point);
        paymentRepository.save(payment);
        //when
        List<PointDto> pointsExpected = pointRepository.findAllByOrganizationIdAndPaymentSucceeded(organizationId, PaymentStatus.SUCCEEDED);

        //then
        assertEquals(4,pointsExpected.size());


    }
}