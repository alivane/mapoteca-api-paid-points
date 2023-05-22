package co.mapoteca.dto.directopago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DirectoPagoCheckoutItemDto {
    private Double price;
    private String countryCode;
}
