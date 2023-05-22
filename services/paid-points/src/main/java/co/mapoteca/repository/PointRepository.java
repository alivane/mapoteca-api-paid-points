package co.mapoteca.repository;

import co.mapoteca.dto.payment.PaymentStatus;
import co.mapoteca.dto.point.PointDto;
import co.mapoteca.dto.point.PointSummary;
import co.mapoteca.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointRepository extends JpaRepository<Point, UUID> {
    @Query(
            """
                        SELECT po
                        FROM Point po
                        LEFT join PointDate pd on pd.point.id = po.id
                        LEFT JOIN Payment py on py.id = pd.payment.id
                        WHERE py.status = :status
                        AND po.organizationId = :organizationId
                        AND po.deleted = false
                        AND pd.pointInformation is not null
                        GROUP BY po.id
                    """
    )
    List<PointDto> findAllByOrganizationIdAndPaymentSucceeded(UUID organizationId, PaymentStatus status);

    List<PointSummary> findAllByOrganizationIdAndDeletedIsFalse(UUID id);

    Optional<Point> findByOrganizationIdAndLatitudeAndLongitude(UUID organizationId, Double latitude, Double longitude);

    List<Point> findAllByOrganizationId(UUID fromString);
}