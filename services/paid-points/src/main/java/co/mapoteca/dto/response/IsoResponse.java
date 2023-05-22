package co.mapoteca.dto.response;

import co.mapoteca.dto.DataSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IsoResponse {
    private Double latitude;
    private Double longitude;
    private String type;
    private Double range;
    private DataSummary data;
    private HashMap<String, Object> area;

}
