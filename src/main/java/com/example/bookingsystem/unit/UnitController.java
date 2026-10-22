package com.example.bookingsystem.unit;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/units")
public class UnitController {
    private final UnitService service;
    public UnitController(UnitService service) { this.service = service; }

    @GetMapping public List<Unit> getAll(@RequestParam(required = false) String city) { return service.getAll(city); }
    @GetMapping("/{id}") public Unit getById(@PathVariable Long id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Unit create(@Valid @RequestBody Unit unit) { return service.create(unit); }
    @PutMapping("/{id}") public Unit update(@PathVariable Long id, @Valid @RequestBody Unit unit) { return service.update(id, unit); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
