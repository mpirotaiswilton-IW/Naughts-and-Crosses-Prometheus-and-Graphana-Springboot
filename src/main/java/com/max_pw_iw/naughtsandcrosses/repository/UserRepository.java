package com.max_pw_iw.naughtsandcrosses.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.max_pw_iw.naughtsandcrosses.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
