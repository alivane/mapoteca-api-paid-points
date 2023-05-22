package co.mapoteca.dto.directopago;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DirectoPagoCheckoutResponse {
    private String id;
    private int amount;
    private String currency;
    private String country;
    private Date createdDate;
    private String status;
    private String orderId;
    private String successUrl;
    private String backUrl;
    private String redirectUrl;
}
