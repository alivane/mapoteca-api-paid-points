package co.mapoteca.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class PointInformationResponse {
    private Double longitude;
    private Double latitude;
    private String message;
    private HttpStatus status;
    private Integer statusCode;

    @Builder
    public PointInformationResponse(Double longitude, Double latitude, String message, HttpStatus status, Integer statusCode) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.message = message;
        this.status = status;
        this.statusCode = status.value();
    }
}
