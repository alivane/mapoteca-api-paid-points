package co.mapoteca.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class DataSummary {
    private String address;
    private Double population;
    private Double households;
    private Double density;
    private Double workers;
    private Double residents;

    private Double traffic; // ok
    private Double motorizedTrafficPerQuarterHour; // ok
    private Double pedestrianTrafficPerQuarterHour; // ok
    private Double motorizedTraffic; // ok
    private Double pedestrianTraffic;  // ok


    private List<String> pointsOfInterestNames;
    private HashMap<String, Double> pointsOfInterestClasses;
    private HashMap<String, Double> ages;
    private HashMap<String, Double> socioeconomicClassification;

    private List<HashMap<String, String>> audienceRate;
    private List<DistributionDto> distribution;
    private HashMap<String, Double> secFromOrigin;
    private HashMap<String, Double> origin;
    private HashMap<String, Double> destination;

    private List<HashMap<String, Double>> tma;

}
