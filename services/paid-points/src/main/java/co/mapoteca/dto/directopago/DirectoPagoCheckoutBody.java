package co.mapoteca.dto.directopago;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class DirectoPagoCheckoutBody {


    @NonNull
    private String amount;

    @NonNull
    private String currency;

    private String country;

    @NonNull
    private String orderId;

    //    private Object payer;
    private String description;

    @NonNull
    private String successUrl;

    @NonNull
    private String backUrl;

    @NonNull
    private String notificationUrl;
}
