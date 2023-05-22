package co.mapoteca.dto.payment;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link co.mapoteca.entity.Payment} entity
 */
@Data
public class PaymentStatusDto implements Serializable {
    private final PaymentStatus status;
}