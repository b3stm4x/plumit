package com.example.application.data.service;

import com.example.application.data.entity.RoleUser;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RoleUserService {

    private final RoleUserRepository repository;

    public RoleUserService(RoleUserRepository repository) {
        this.repository = repository;
    }

    public Optional<RoleUser> get(Long id) {
        return repository.findById(id);
    }

    public RoleUser update(RoleUser entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<RoleUser> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<RoleUser> list(Pageable pageable, Specification<RoleUser> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
