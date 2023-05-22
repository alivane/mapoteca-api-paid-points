package co.mapoteca.dto;

import java.time.LocalDate;
import java.util.UUID;

public interface PointInformationDto {
    UUID getId();

    Double getPopulation();

    Double getHouseholds();

    Double getDensity();
    Double getWorkers();

    Double getResidents();

    Double getTraffic();

    Double getMotorizedTrafficPerQuarterHour();

    Double getPedestrianTrafficPerQuarterHour();

    Double getMotorizedTraffic();

    Double getPedestrianTraffic();

    String getPointsOfInterestNames();

    String getPointsOfInterestClasses();

    String getAges();

    String getSocioeconomicClassification();

    String getTma();

    String getArea();

    LocalDate getPointInitialPeriod();

    LocalDate getPointFinalPeriod();

    LocalDate getCreatedAt();

    PointSummary getPoint();

    interface PointSummary {
        UUID getId();

        Double getLatitude();

        Double getLongitude();

        Integer getRange();

        LocalDate getInitialCurrentPeriod();

        LocalDate getFinalCurrentPeriod();

        UUID getOrganizationId();

        Boolean getProcessed();
    }
}
