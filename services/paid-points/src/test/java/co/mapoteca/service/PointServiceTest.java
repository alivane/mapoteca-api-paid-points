package co.mapoteca.service;

import co.mapoteca.config.GeneralConfig;
import co.mapoteca.dto.point.CreatePointDto;
import co.mapoteca.dto.directopago.DirectoPagoStatus;
import co.mapoteca.entity.Payment;
import co.mapoteca.entity.Point;
import co.mapoteca.entity.PointDate;
import co.mapoteca.repository.PaymentRepository;
import co.mapoteca.repository.PointDateRepository;
import co.mapoteca.repository.PointRepository;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {PointService.class})
@ExtendWith(MockitoExtension.class)
//todo update test to api v4
class PointServiceTest {
    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PointDateRepository pointDateRepository;
    @Mock
    private DirectoPagoService directoPagoService;
    @Mock
    private GeneralConfig generalConfig;
    @Mock
    private RestTemplate restTemplate;

    @Test
    void canSaveAListOfNewPoints() throws Exception {
        //given
        final Integer basePrice = 10;
        UUID organizationId = UUID.randomUUID();
        CreatePointDto createPointDto = CreatePointDto.builder()
                .latitude(8.978332)
                .longitude(-79.5255283)
                .range(300)
                .year(2022)
                .month(12)
                .day(12)
                .countryCode("CL")
                .address("Avenida panama 24")
                .build();

        CreatePointDto createPointDto2 = CreatePointDto.builder()
                .latitude(98.978332)
                .longitude(-78.5255283)
                .range(300)
                .year(2022)
                .month(12)
                .day(12)
                .countryCode("CL")
                .address("Pajaritos 1234")
                .build();

        //new List with createPointDto2 and createPointDto
        List<CreatePointDto> createPointDtos = new ArrayList<>();
        createPointDtos.add(createPointDto);



        //when
        when(generalConfig.getBasePrice()).thenReturn(basePrice);
        when(pointRepository.findByOrganizationIdAndLatitudeAndLongitude(organizationId,
                createPointDto.getLatitude(),
                createPointDto.getLongitude())).thenReturn(Optional.empty());
        pointService.save(organizationId, createPointDtos);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(pointRepository, times(1)).findByOrganizationIdAndLatitudeAndLongitude(any(), anyDouble(), anyDouble());
        verify(pointDateRepository, times(1)).saveAll(any());

    }

    @Test
    void canSaveAListOfExistingPointsInCurrentPeriod() throws Exception {
        //given
        final Boolean expectedProcessed = false;
        final Integer basePrice = 10;
        UUID organizationId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CreatePointDto createPointDto = CreatePointDto.builder()
                .latitude(8.978332)
                .longitude(-79.5255283)
                .range(300)
                .address("Avenida panama 24")
                .build();

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .total(BigDecimal.valueOf(10))
                .status(String.valueOf(DirectoPagoStatus.PAID))
                .transactionId(UUID.randomUUID())
                .createdAt(now.minusDays(2))
                .build();


        PointDate pointDate = PointDate.builder()
                .range(400)
                .dateTime(now.minusDays(2))
                .processed(true)
                .payment(payment)
                .day(12)
                .month(12)
                .year(2022)
                .build();

        Point point = Point.builder()
                .id(UUID.randomUUID())
                .latitude(createPointDto.getLatitude())
                .longitude(createPointDto.getLongitude())
                .address(createPointDto.getAddress())
                .organizationId(organizationId)
                .pointDates(Collections.singleton(pointDate))
                .build();

        when(generalConfig.getBasePrice()).thenReturn(basePrice);
        when(pointRepository.findByOrganizationIdAndLatitudeAndLongitude(organizationId,
                createPointDto.getLatitude(),
                createPointDto.getLongitude())).thenReturn(Optional.of(point));
        pointService.save(organizationId, Collections.singletonList(createPointDto));

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(pointRepository, times(1)).findByOrganizationIdAndLatitudeAndLongitude(any(), anyDouble(), anyDouble());
//        assertEquals(expectedProcessed, pointDate.isProcessed());

    }

  /*   @Disabled
    @Test
    void canUpdateStatusOfPaymentWithUnprocessedPoints() throws Exception {
        //given
        UUID paymentId = UUID.randomUUID();
        final Boolean expectedProcessed = true;
        final String expectedPaymentStatus = String.valueOf(DirectoPagoStatus.PAID);
        int expectedSuccessful = 1;
        UUID organizationId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        Point point = Point.builder()
                .id(UUID.randomUUID())
                .latitude(8.978332)
                .longitude(-79.5255283)
                .range(300)
                .processed(false)
                .initialCurrentPeriod(now)
                .finalCurrentPeriod(now.plusMonths(1))
                .address("Avenida panama 24")
                .organizationId(organizationId)
                .build();

        Payment payment = Payment.builder()
                .id(paymentId)
                .points(new HashSet<>(Collections.singletonList(point)))
                .status(DirectoPagoStatus.PENDING.toString())

                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "A");
        //when
        when(generalConfig.getAuxToken()).thenReturn("A");
        when(generalConfig.getApiLocation()).thenReturn("https://iso.api.mapoteca.dev/v3/");
        when(paymentRepository.findByTransactionId(paymentId)).thenReturn(Optional.of(payment));
        when(pointRepository.save(point)).thenReturn(point);
        when(pointRepository.saveAll(payment.getPoints())).thenReturn(Collections.singletonList(point));
        when(restTemplate.exchange(
                "https://iso.api.mapoteca.dev/v3/location?type=isoarea&latitude=" + point.getLatitude() + "&longitude=" + point.getLongitude() + "&range=" + point.getRange(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                IsoResponse.class
        )).thenReturn(ResponseEntity.ok(IsoResponse.builder()
                .build()));
        PointData actualPointData = pointService.updateStatus(paymentId.toString(), DirectoPagoStatus.PAID.toString());
        //then
        verify(paymentRepository, times(1)).save(payment);
        verify(pointInformationRepository, times(1)).save(any());
        assertEquals(expectedProcessed, point.getProcessed());
        assertEquals(expectedPaymentStatus, payment.getStatus());
        assertEquals(expectedSuccessful, actualPointData.getSuccessful().size());

    }

    @Disabled
    @Test
    void willThrowWhenUpdateStatusOfPaymentWithRestClientError() throws Exception {
        //given
        UUID paymentId = UUID.randomUUID();
        final Boolean expectedProcessed = false;
        final String expectedPaymentStatus = String.valueOf(DirectoPagoStatus.PAID);
        int expectedErrors = 1;
        Point point = Point.builder()
                .id(UUID.randomUUID())
                .latitude(8.978332)
                .longitude(-79.5255283)
                .range(300)
                .processed(false)
                .initialCurrentPeriod(LocalDate.now())
                .finalCurrentPeriod(LocalDate.now())
                .address("Avenida panama 24")
                .organizationId(UUID.randomUUID())
                .build();

        Payment payment = Payment.builder()
                .id(paymentId)
                .points(new HashSet<>(Collections.singletonList(point)))
                .status(DirectoPagoStatus.PENDING.toString())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "A");
        //when
        when(generalConfig.getAuxToken()).thenReturn("A");
        when(generalConfig.getApiLocation()).thenReturn("https://iso.api.mapoteca.dev/v3/");
        when(paymentRepository.findByTransactionId(paymentId)).thenReturn(Optional.of(payment));
        when(pointRepository.saveAll(payment.getPoints())).thenReturn(Collections.singletonList(point));
        when(restTemplate.exchange(
                "https://iso.api.mapoteca.dev/v3/location?type=isoarea&latitude=" + point.getLatitude() + "&longitude=" + point.getLongitude() + "&range=" + point.getRange(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                IsoResponse.class
        )).thenThrow(RestClientException.class);
        PointData actualPointData = pointService.updateStatus(paymentId.toString(), DirectoPagoStatus.PAID.toString());
        //then
        assertEquals(expectedProcessed, point.getProcessed());
        assertEquals(expectedPaymentStatus, payment.getStatus());
        verify(pointInformationRepository, never()).save(any());
        assertEquals(expectedErrors, actualPointData.getErrors().size());

    }


    @Disabled
    @Test
    void willThrowWhenTheCurrentDateHasAlreadyAPayment() {
        //given
        LocalDate now = LocalDate.now();
        CreatePointDto createPointDto = CreatePointDto.builder()
                .latitude(8.978332)
                .longitude(-79.5255283)
                .address("Avenida panama 24")
                .range(300)
                .build();

        UUID organizationId = UUID.randomUUID();
        Point point = Point.builder()
                .id(UUID.randomUUID())
                .latitude(createPointDto.getLatitude())
                .longitude(createPointDto.getLongitude())
                .address(createPointDto.getAddress())
                .processed(true)
                .initialCurrentPeriod(now)
                .finalCurrentPeriod(now.plusMonths(1))
                .organizationId(organizationId)
                .range(createPointDto.getRange())
                .build();

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .points(new HashSet<>(Collections.singletonList(point)))
                .total(BigDecimal.valueOf(40))
                .status(String.valueOf(DirectoPagoStatus.PAID))
                .transactionId(UUID.randomUUID())
                .createdAt(point.getInitialCurrentPeriod())
                .build();

        point.setPayments(new HashSet<>(Collections.singletonList(payment)));
        when(pointRepository.findByOrganizationIdAndLatitudeAndLongitude(organizationId, createPointDto.getLatitude(), createPointDto.getLongitude())).thenReturn(Optional.of(point));
        assertThatThrownBy(() -> pointService.save(organizationId, Collections.singletonList(createPointDto)))
                .isInstanceOf(Exception.class);
        verify(paymentRepository, never()).save(any());
        verify(pointRepository, never()).saveAll(anyList());
    }*/

}