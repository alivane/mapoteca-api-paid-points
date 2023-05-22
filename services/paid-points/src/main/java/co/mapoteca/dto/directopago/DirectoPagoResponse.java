package co.mapoteca.dto.directopago;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DirectoPagoResponse {
    private String clientId;
    private Long transactionId;
    private String invoiceId;
    private String country;
    private String currency;
    private Double usdAmount;
    private Double localAmount;
    private String paymentMethodType;
    private String status;
}
