package co.mapoteca.repository;

import co.mapoteca.entity.PointDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PointDateRepository extends JpaRepository<PointDate, UUID> {
    List<PointDate> findAllByPoint_OrganizationId(UUID organizationId);

}