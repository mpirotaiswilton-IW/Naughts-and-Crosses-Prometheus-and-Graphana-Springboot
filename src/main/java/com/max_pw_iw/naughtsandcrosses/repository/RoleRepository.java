package com.max_pw_iw.naughtsandcrosses.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.max_pw_iw.naughtsandcrosses.entity.Role;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
