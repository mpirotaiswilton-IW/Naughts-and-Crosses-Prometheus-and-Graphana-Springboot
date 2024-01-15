package com.max_pw_iw.naughtsandcrosses;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.max_pw_iw.naughtsandcrosses.entity.Role;
import com.max_pw_iw.naughtsandcrosses.entity.User;
import com.max_pw_iw.naughtsandcrosses.repository.RoleRepository;
import com.max_pw_iw.naughtsandcrosses.repository.UserRepository;

import lombok.AllArgsConstructor;

@SpringBootApplication @AllArgsConstructor
public class NaughtsAndCrossesApplication implements CommandLineRunner{

	UserRepository userRepository;
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(NaughtsAndCrossesApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(String... args) throws Exception {
		
		BCryptPasswordEncoder tempBCryptPasswordEncoder = new BCryptPasswordEncoder();

		Role playerRole = new Role("PLAYER");
		Role adminRole = new Role("ADMIN");

		User admin = new User("DEV_ADMIN", tempBCryptPasswordEncoder.encode("admin_pass"));
		
		// roleRepository.save(playerRole);
		// roleRepository.save(adminRole);

		List<Role> roleList = Arrays.asList(playerRole,adminRole);
		Set<Role> roles = new HashSet<Role>(roleList);

		admin.setRoles(roles);
		try{
			userRepository.save(admin);
		} catch (RuntimeException e){
			
		} 
	}
}
