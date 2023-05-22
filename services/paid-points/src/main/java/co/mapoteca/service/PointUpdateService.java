package co.mapoteca.service;

import co.mapoteca.config.GeneralConfig;
import co.mapoteca.dto.point.CreatePointDto;
import co.mapoteca.dto.response.PointData;
import co.mapoteca.dto.response.PointInformationResponse;
import co.mapoteca.entity.Payment;
import co.mapoteca.entity.PointDate;
import co.mapoteca.repository.PaymentRepository;
import co.mapoteca.repository.PointDateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointUpdateService {
    private final GeneralConfig generalConfig;
    private final PointDateRepository pointDateRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    public PointData updateStatus(String paymentId) throws Exception {
        log.info("Updating point");
        Payment payment = paymentRepository
                .findByPaymentId(paymentId)
                .orElseThrow(() -> new Exception("Payment not found"));
        Set<PointDate> pointDates = payment.getPointDates();
        List<PointInformationResponse> pointInformationResponses = new ArrayList<>();
        for (PointDate pointDate : pointDates) {
            if (pointDate.isProcessed()) continue;
            pointInformationResponses.add(processPointInformation(pointDate));
        }
        pointDateRepository.saveAll(pointDates);
        return new PointData(pointInformationResponses);
    }

    public PointInformationResponse processPointInformation(
            PointDate pointDate) {
        try {
            String pointInformation = getPointInformation(pointDate);
            pointDate.setProcessed(true);
            pointDate.setPointInformation(pointInformation);
            return PointInformationResponse.builder().latitude(pointDate.getPoint().getLatitude()).longitude(pointDate
                    .getPoint()
                    .getLongitude()).message("Processed").status(HttpStatus.OK).build();
        } catch (JsonProcessingException | RestClientException e) {
            pointDate.setProcessed(pointDate.getPointInformation() != null);
            return PointInformationResponse.builder().latitude(pointDate.getPoint().getLatitude()).longitude(pointDate
                    .getPoint()
                    .getLongitude()).message(e.getMessage()).status(HttpStatus.BAD_REQUEST).build();
        }
    }


    public String getPointInformation(
            PointDate pointDate) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();

        String token = generalConfig.getAuxToken();

        headers.set("Authorization", "Bearer " + token);
        log.info("Capturing Point");

        ResponseEntity<String> response = restTemplate.exchange(generalConfig.getApiUrl() + "/analysis/location",
                HttpMethod.POST,
                new HttpEntity<>(CreatePointDto
                        .builder()
                        .latitude(pointDate.getPoint().getLatitude())
                        .longitude(pointDate.getPoint().getLongitude())
                        .range(pointDate.getRange())
                        .type("isoarea")
                        .year(pointDate.getYear())
                        .month(pointDate.getMonth())
                        .day(pointDate.getDay())
                        .build(), headers),
                String.class);
        String analysisData = response.getBody();
        assert analysisData != null;
        log.info("Point captured");

        return analysisData;

//        PointInformation information = PointInformation
//                .builder()
//                .id(pointDate.getPointInformation() != null ? pointDate
//                        .getPointInformation()
//                        .getId() : null)
//                //Demographic
//                .population(validateDoubleValue(iso.getData(), "getPopulation"))
//                .households(validateDoubleValue(iso.getData(), "getHouseholds"))
//                .density(validateDoubleValue(iso.getData(), "getDensity"))
//                .workers(validateDoubleValue(iso.getData(), "getWorkers"))
//                .residents(validateDoubleValue(iso.getData(), "getResidents"))
//                // Traffic
//                .traffic(validateDoubleValue(iso.getData(), "getTraffic"))
//                .motorizedTraffic(validateDoubleValue(iso.getData(), "getMotorizedTraffic"))
//                .pedestrianTraffic(validateDoubleValue(iso.getData(), "getPedestrianTraffic"))
//                .motorizedTrafficPerQuarterHour(validateDoubleValue(iso.getData(), "getMotorizedTrafficPerQuarterHour"))
//                .pedestrianTrafficPerQuarterHour(validateDoubleValue(iso.getData(),
//                        "getPedestrianTrafficPerQuarterHour"))
//                .pointsOfInterestNames(mapper.writeValueAsString(validateStringValue(iso.getData(),
//                        "getPointsOfInterestNames")))
//                .pointsOfInterestClasses(mapper.writeValueAsString(validateStringValue(iso.getData(),
//                        "getPointsOfInterestClasses")))
//                .ages(mapper.writeValueAsString(validateStringValue(iso.getData(), "getAges")))
//                .socioeconomicClassification(mapper.writeValueAsString(validateStringValue(iso.getData(),
//                        "getSocioeconomicClassification")))
//
//                // New
//                .audienceRate(mapper.writeValueAsString(validateStringValue(iso.getData(), "getAudienceRate")))
//                .distribution(mapper.writeValueAsString(validateStringValue(iso.getData(), "getDistribution")))
//                .secFromOrigin(mapper.writeValueAsString(validateStringValue(iso.getData(), "getSecFromOrigin")))
//                .origin(mapper.writeValueAsString(validateStringValue(iso.getData(), "getOrigin")))
//                .destination(mapper.writeValueAsString(validateStringValue(iso.getData(), "getDestination")))
//
//                .tma(mapper.writeValueAsString(validateStringValue(iso.getData(), "getTma")))
//                .area(mapper.writeValueAsString(iso.getArea()))
//                .pointDate(pointDate)
//                .year(pointDate.getYear())
//                .month(pointDate.getMonth())
//                .day(pointDate.getDay())
//                .build();
//
//        informationRepository.save(information);
    }


    public <T> Double validateDoubleValue(
            T value, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return value != null ? (Double) value.getClass().getMethod(method).invoke(value) : null;
    }

    public <T> Object validateStringValue(
            T value, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return value != null ? value.getClass().getMethod(method).invoke(value) : "";
    }


}
