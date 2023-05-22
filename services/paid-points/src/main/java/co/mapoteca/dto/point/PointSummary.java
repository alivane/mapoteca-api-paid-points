package co.mapoteca.dto.point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface PointSummary {
    UUID getId();

    Double getLatitude();

    Double getLongitude();

    UUID getOrganizationId();

    String getAddress();

    Set<PointDateSummary> getPointDates();


    interface PointDateSummary {
        UUID getId();
        boolean isProcessed();
        Integer getRange();
        Integer getDay();
        Integer getMonth();
        Integer getYear();
        LocalDateTime getDateTime();
        LocalDateTime getCreatedAt();
        LocalDateTime getUpdatedAt();
        LocalDate getFullPointDate();

        PaymentInformation getPayment();
        interface PaymentInformation {
            String getPaymentRecoveryUrl();

            String getStatus();

            LocalDateTime getCreatedAt();
        }

    }


}
