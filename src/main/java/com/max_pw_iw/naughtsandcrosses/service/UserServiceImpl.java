package com.max_pw_iw.naughtsandcrosses.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.max_pw_iw.naughtsandcrosses.dto.UserRequest;
import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.entity.Role;
import com.max_pw_iw.naughtsandcrosses.entity.User;
import com.max_pw_iw.naughtsandcrosses.exception.EntityNotFoundException;
import com.max_pw_iw.naughtsandcrosses.exception.UserDeletingSelfException;
import com.max_pw_iw.naughtsandcrosses.meter.UserMetricsBean;
import com.max_pw_iw.naughtsandcrosses.repository.RoleRepository;
import com.max_pw_iw.naughtsandcrosses.repository.UserRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
@Service(value = "userService")
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    //private UserMetricsBean userMetricsBean;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Counter historicUserCounter;

    public UserServiceImpl(MeterRegistry meterRegistry){
        this.historicUserCounter = meterRegistry.counter("historic.number.of.users");
    }

    @Override
    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return unwrapUser(user, id);
    }

    @Override
    public User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return unwrapUser(user, username);
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public List<Game> getAllGamesFromUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        User unwrappedUser = unwrapUser(user, id);
        List<Game> games = unwrappedUser.getCreatedGames();
        games.addAll(unwrappedUser.getJoinedGames());
        return games;
    }

    @Override
    public User saveUser(UserRequest userRequest) {
        User user = new User(userRequest.getUsername(), userRequest.getPassword());
        user.addRole(unwrapRole(roleRepository.findByName("PLAYER"), 404L));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        historicUserCounter.increment();
        return savedUser;
    }

    @Override
    public User addRoleToUser(Long id, Long roleId) {
        Optional<User> user = userRepository.findById(id);
        User unwrappedUser = unwrapUser(user, id);
        Role role = unwrapRole(roleRepository.findById(roleId), roleId);
        unwrappedUser.addRole(role);
        return userRepository.save(unwrappedUser);
    }

    @Override
    public void deleteUser(Long id, String username) {
        Optional<User> user = userRepository.findById(id);
        User unwrappedUser = unwrapUser(user, id);
        User authenticatedUser = getUser(username);
        if(unwrappedUser == authenticatedUser) {
            throw new UserDeletingSelfException(id);
        }
        userRepository.delete(unwrappedUser);
    }

    @Override
    public void deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        User unwrappedUser = unwrapUser(user, username);
        userRepository.delete(unwrappedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        User unwrappedUser = unwrapUser(user, username);
        return new org.springframework.security.core.userdetails.User(
            unwrappedUser.getUsername(),
            unwrappedUser.getPassword(),
            getAuthority(unwrappedUser)
        );
    }

    static User unwrapUser(Optional<User> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, User.class);
    }

    static User unwrapUser(Optional<User> entity, String username) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(username, User.class);
    }

    static Role unwrapRole(Optional<Role> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, Role.class);
    }

    // Get user authorities
    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }
}

