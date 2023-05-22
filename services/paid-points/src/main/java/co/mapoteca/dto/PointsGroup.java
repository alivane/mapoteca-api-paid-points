package co.mapoteca.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PointsGroup {

    private LocalDate initialPeriod;
    private LocalDate finalPeriod;
    private PointInformationDto data;

}
