package co.mapoteca.dto.point;

import java.util.Set;
import java.util.UUID;

public interface PointDto {

    UUID getId();

    Double getLatitude();

    Double getLongitude();

    UUID getOrganizationId();

    String getAddress();

    Set<PointDateDto> getPointDates();


}
