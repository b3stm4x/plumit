package com.example.application.data.service;

import com.example.application.data.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleUserRepository extends JpaRepository<RoleUser, Long>, JpaSpecificationExecutor<RoleUser> {

}
