package co.mapoteca.repository;

import co.mapoteca.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentId(String paymentId);

    <T> Optional<T> findById(UUID uuid, Class<T> clazz);
}