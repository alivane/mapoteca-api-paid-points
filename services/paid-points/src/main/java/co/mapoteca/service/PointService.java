package co.mapoteca.service;

import co.mapoteca.config.GeneralConfig;
import co.mapoteca.dto.point.CreatePointDto;
import co.mapoteca.dto.Response;
import co.mapoteca.dto.payment.PaymentStatus;
import co.mapoteca.dto.point.PointDto;
import co.mapoteca.dto.point.PointSummary;
import co.mapoteca.dto.response.PointData;
import co.mapoteca.dto.response.PointInformationResponse;
import co.mapoteca.entity.Currency;
import co.mapoteca.entity.Payment;
import co.mapoteca.entity.Point;
import co.mapoteca.entity.PointDate;
import co.mapoteca.repository.CurrencyRepository;
import co.mapoteca.repository.PaymentRepository;
import co.mapoteca.repository.PointDateRepository;
import co.mapoteca.repository.PointRepository;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PointService {
    private final CurrencyRepository currencyRepository;
    private final PointDateRepository pointDateRepository;
    private final GeneralConfig generalConfig;
    private final PointRepository pointRepository;
    private final PaymentRepository paymentRepository;
    private final PointUpdateService pointUpdateService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    //todo test
    public String save(
            UUID organizationId,
            List<CreatePointDto> createPointList) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Set<Point> points = new HashSet<>();
        Set<PointDate> pointDates = new HashSet<>();
        Map<String, List<CreatePointDto>> groupedPoints = createPointList
                .stream()
                .collect(Collectors.groupingBy(el -> el.getLatitude().toString() + " " + el.getLongitude().toString()));

        groupedPoints.forEach((key, value) -> {
            String[] coords = key.split(" ");
            Double latitude = Double.valueOf(coords[0]);
            Double longitude = Double.valueOf(coords[1]);

            Optional<Point> pointOptional = pointRepository.findByOrganizationIdAndLatitudeAndLongitude(organizationId,
                    latitude,
                    longitude);
            Point point;

            if (pointOptional.isPresent()) {
                point = pointOptional.get();

                List<PointDate> dateTimes = point.getPointDates().stream().filter(el ->
                        value.stream().anyMatch(date -> date.getFullPointDate().isEqual(el.getFullPointDate()))
                ).toList();

                if (!dateTimes.isEmpty()) {
                    PointDate actualPointDate = dateTimes.get(0);
                    Payment payments = actualPointDate.getPayment();

                    if (!payments.getStatus().equals(PaymentStatus.PENDING)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Uno de tus puntos ya se encuentra pagado para el periodo actual.");
                    }
                }

                List<PointDate> pointDatesPaid = point.getPointDates().stream().filter(date -> date
                        .getPayment()
                        .getStatus()
                        .equals(PaymentStatus.SUCCEEDED)).toList();

                List<PointDate> pendingPointsDate = point.getPointDates().stream().filter(date -> date
                        .getPayment()
                        .getStatus()
                        .equals(PaymentStatus.PENDING)).toList();

                pointDateRepository.deleteAll(pendingPointsDate);

                UUID id = point.getId();
                point = Point
                        .builder()
                        .id(id)
                        .latitude(latitude)
                        .longitude(longitude)
                        .deleted(false)
                        .address(value.get(0).getAddress())
                        .organizationId(organizationId)
                        .pointDates(new HashSet<>(pointDatesPaid))
                        .build();

            } else {
                point = Point
                        .builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .deleted(false)
                        .address(value.get(0).getAddress())
                        .organizationId(organizationId)
                        .build();
            }

            Point finalPoint = point;
            pointDates.addAll(value.stream().map(date -> PointDate
                    .builder()
                    .point(finalPoint)
                    .processed(false)
                    .dateTime(now)
                    .day(date.getDay())
                    .month(date.getMonth())
                    .year(date.getYear())
                    .range(date.getRange())
                    .build()
            ).toList());

            points.add(point);

        });

        pointRepository.saveAll(points);

        double price = (pointDates.size() * generalConfig.getBasePrice());

        HttpHeaders headers = new HttpHeaders();

        String token = generalConfig.getAuxToken();

        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Response> response = restTemplate.exchange(String.format("%S/org/country/%s",
                generalConfig.getApiUrl(),
                organizationId), HttpMethod.GET, httpEntity, Response.class);

        if (!response.getStatusCode().is2xxSuccessful())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create payment");

        Currency currency = currencyRepository.findByCountry(Objects
                .requireNonNull(response.getBody())
                .getData()
                .toString()).orElse(Currency
                .builder()
                .currency("USD")
                .country(response.getBody().getData().toString())
                .build());

        Payment payment = Payment
                .builder()
                .total(BigDecimal.valueOf(price))
                .build();

        paymentRepository.saveAndFlush(payment);

        Session session = paymentService.createSession(pointDates.size(),
                currency.getCurrency().toLowerCase(),
                payment.getId());

        payment.setPaymentId(session.getId());
        payment.setStatus(PaymentStatus.PENDING);


        for (PointDate pointDate : pointDates) {
            pointDate.setPayment(payment);
        }

        pointDateRepository.saveAll(pointDates);

        return session.getUrl();
    }

    public List<PointDto> getPaidPointsByOrganization(UUID organizationId) {
        //todo validar si existe organización
        return pointRepository.findAllByOrganizationIdAndPaymentSucceeded(organizationId, PaymentStatus.SUCCEEDED);

    }

    public List<PointDto> getPendingPointsByOrganization(UUID organizationId) {
        return pointRepository.findAllByOrganizationIdAndPaymentSucceeded(organizationId, PaymentStatus.PENDING);
    }

    public PointData reNewPointInformation(HttpServletRequest request, UUID organizationId) throws Exception {
        List<PointDate> pointsDate = pointDateRepository.findAllByPoint_OrganizationId(organizationId);
        List<PointInformationResponse> pointInformationResponses = new ArrayList<>();
        for (PointDate pointDate : pointsDate) {
            pointInformationResponses.add(pointUpdateService.processPointInformation(pointDate));
        }
        return new PointData(pointInformationResponses);
    }

    public void deletePaidPoint(UUID pointId) throws Exception {
        Point point = pointRepository.findById(pointId).orElseThrow(() -> new Exception("Point not found"));
        point.setDeleted(true);
        point.setDeletedAt(LocalDate.now());
        pointRepository.save(point);
    }

    public List<PointSummary> getPaidPointsSummaries(UUID organizationId) {
        return pointRepository.findAllByOrganizationIdAndDeletedIsFalse(organizationId);
    }

    public void processPointInformation(
            HttpServletRequest request,
            UUID dateId) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        PointDate pointDate = pointDateRepository.findById(dateId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Punto no encontrado"));

        if (!pointDate.getPayment().getStatus().equals(PaymentStatus.SUCCEEDED)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Fecha del punto no se encuentra pagada");
        }
        pointUpdateService.processPointInformation(pointDate);
    }
}

