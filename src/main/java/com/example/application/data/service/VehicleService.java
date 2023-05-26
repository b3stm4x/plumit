package com.example.application.data.service;

import com.example.application.data.entity.Vehicle;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public Optional<Vehicle> get(Long id) {
        return repository.findById(id);
    }

    public Vehicle update(Vehicle entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Vehicle> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Vehicle> list(Pageable pageable, Specification<Vehicle> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
