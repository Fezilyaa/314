package ru.kata.spring.boot_security.demo.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.rep.RoleRepository;
import ru.kata.spring.boot_security.demo.rep.UserRepository;

import java.util.*;

@Component
public class SetUpDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final static long ROLE_ADMIN = 1;
    private final static long ROLE_USER = 2;

    private final UserRepository repo;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public SetUpDataLoader(UserRepository repo, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Iterable<User> users = repo.findAll();
        if (users.iterator().hasNext() == false) {
            Role adminRole = new Role(ROLE_ADMIN, "ROLE_ADMIN");
            Role userRole = new Role(ROLE_USER, "ROLE_USER");
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            User admin = new User();
            admin.setUsername("admin");
            admin.setUserJob("Admin");
            admin.setUserAge(34);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(adminRoles);
            repo.save(admin);
        }
    }

}
