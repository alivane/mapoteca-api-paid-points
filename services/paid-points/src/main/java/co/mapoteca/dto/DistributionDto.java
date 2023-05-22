package co.mapoteca.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributionDto {


    private String id;
    private Double latitude;
    private Double longitude;
}
