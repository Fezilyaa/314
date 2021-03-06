package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.rep.RoleRepository;
import ru.kata.spring.boot_security.demo.rep.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements  UserService, UserDetailsService {

    private UserRepository repo;
    private RoleRepository roleRepository;
    private  PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository repo, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void addUser(User user) {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         repo.save(user);
    }
    @Override
    public User getUserById(Long id) {
        return repo.findById(id).get();
    }
    @Override
    public List<User> listOfUsers() {
        return repo.findAll();
    }
    @Override
    public void deleteUser(Long id) {
        repo.deleteById(id);
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                getAuthorities(user.getRoles()));
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getAuthority()))
                .collect(Collectors.toSet());
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public void saveAndFlush(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.saveAndFlush(user);
    }

    public Set<Role> getRoles (ArrayList<Long> roles){
        return roleRepository.findByIdIn(roles);
    }

    public ArrayList <Long> rolesToId (Set<Role> roles){
        ArrayList<Long> rolesId = new ArrayList<>();
        for (Role role:roles){
            rolesId.add(Long.valueOf(role.getName()));
        }
        return rolesId;
    }

    public User findByUserName(String userName) {
        return repo.findByUsername(userName);
    }
}
