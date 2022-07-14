package ru.kata.spring.boot_security.demo.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.rep.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UsersRestController {
    private final RoleRepository roleRepository;

    private final UserServiceImpl userService;


    @GetMapping("/api/getroles")
    private List<Role> allRoles() {
        return userService.listRoles();
    }


    @GetMapping("/api/principal")
    public User getPrincipalInfo(Principal principal) {
        return userService.findByUserName(principal.getName());
    }

    @GetMapping("/api/{id}")
    public User findOneUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        return user;
    }

    @PostMapping("/api")
    public void addNewUser(@RequestBody User user) {
        userService.addUser(user);
    }
    @GetMapping("/api")
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok().body(userService.listOfUsers());
    }
    @PutMapping("/api/{id}")
    public void updateUser(@RequestBody User user) {
        userService.saveAndFlush(user);
    }
    @DeleteMapping("/api/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

}
