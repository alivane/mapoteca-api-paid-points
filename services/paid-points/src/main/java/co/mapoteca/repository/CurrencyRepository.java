package co.mapoteca.repository;

import co.mapoteca.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {
    @Query("""
            select c
            from Currency c
            where upper(c.country) = upper(:country)
            """)
    Optional<Currency> findByCountry(String country);

}