package com.example.bookingsystem.unit;

import com.example.bookingsystem.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UnitService {
    private final UnitRepository repository;

    public UnitService(UnitRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<Unit> getAll(String city) {
        return city == null || city.isBlank() ? repository.findAll() : repository.findByCityIgnoreCase(city.trim());
    }

    @Transactional(readOnly = true)
    public Unit getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + id));
    }

    @Transactional
    public Unit create(Unit unit) { unit.setId(null); return repository.save(unit); }

    @Transactional
    public Unit update(Long id, Unit input) {
        Unit unit = getById(id);
        unit.setName(input.getName());
        unit.setCity(input.getCity());
        unit.setRooms(input.getRooms());
        unit.setPricePerNight(input.getPricePerNight());
        return repository.save(unit);
    }

    /**
     * Deletes a unit. If bookings still reference it, the foreign key makes the database reject the delete;
     * flush() forces that to happen here so ApiExceptionHandler can turn it into a 409 response.
     */
    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
        repository.flush();
    }
}
