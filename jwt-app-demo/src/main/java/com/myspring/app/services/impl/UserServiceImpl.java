package com.myspring.app.services.impl;

import com.myspring.app.entities.Role;
import com.myspring.app.entities.User;
import com.myspring.app.repositories.RoleRepository;
import com.myspring.app.repositories.UserRepository;
import com.myspring.app.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public User findByUsername(String username){
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new NullPointerException(String.format("user with username: %s is null", username));
        }
        return user;

    }


    @Override
    public boolean update(Long id, User user) {

        User userFromDb = userRepository.findById(id).get();

        if( userFromDb != null ){

            log.info("User with id: {} has successfully updated", id);

            userFromDb.setUsername(user.getUsername());
            userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
            userFromDb.setEmail(user.getEmail());
            userFromDb.setRoles(user.getRoles());
            userRepository.save(userFromDb);
            return true;
        }
        else {

            log.error("User with id: {} not found for update", id);
            return false;
        }
    }
    @Override
    public boolean save(User user) {
        User userFromDb = userRepository.findByEmail(user.getEmail());

        if(userFromDb == null){

            Role role = roleRepository.findAll()
                    .stream()
                    .filter(s -> s.getName().equals("USER")).findFirst()
            .orElse(new Role("USER"));


            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Set.of(role));
            userRepository.save(user);
            log.info("User with username: {} has successfully saved ", user.getUsername());
            return true;
        }
        else {

            log.error("User with username: {} exists", user.getUsername());
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {

        User userFromDb = userRepository.findById(id).get();

        if( userFromDb == null ) {

            log.info("User with id: {} not found ", id);

            return false;
        }
        else {

            userRepository.deleteById(id);

            log.info("User with id: {} has successfully deleted", id);

            return true;
        }
    }

    @Override
    public User getOneById(Long id) {

        User userFromDb = userRepository.findById(id).get();

        if( userFromDb == null ){

            log.info("User with id: {} not found ", id);
            return null;

        }

        else {

            log.info("User with id: {} has successfully loaded", id);
            return userRepository.findById(id).get();

        }

    }

    @Override
    public List<User> getAll() {
        log.info("List of Users successfully loaded");
        return userRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User with name: " + username + "not found");
        }
        Collection<GrantedAuthority> authorities =
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
                authorities);
    }
}
