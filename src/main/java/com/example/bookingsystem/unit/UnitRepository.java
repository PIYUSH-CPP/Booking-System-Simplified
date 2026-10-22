package com.example.bookingsystem.unit;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByCityIgnoreCase(String city);

    /**
     * Loads a unit with a pessimistic write lock (SELECT ... FOR UPDATE).
     * Used when creating bookings so that two requests for the same unit are processed one after another.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Unit u where u.id = :id")
    Optional<Unit> findByIdForUpdate(@Param("id") Long id);
}
