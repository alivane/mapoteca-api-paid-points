package co.mapoteca.dto.point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface PointDateDto {
    UUID getId();
    boolean isProcessed();
    Integer getRange();
    Integer getDay();
    Integer getMonth();
    Integer getYear();
    LocalDateTime getDateTime();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    PaymentInformation getPayment();
    Object getPointInformation();

    LocalDate getFullPointDate();
    interface PaymentInformation {
        LocalDateTime getCreatedAt();
        String getPaymentRecoveryUrl();
    }

}
