package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.users.ERole;
import com.MOF_ITMD.DepWeb.models.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
