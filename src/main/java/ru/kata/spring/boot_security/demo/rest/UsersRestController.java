package ru.kata.spring.boot_security.demo.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.rep.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UsersRestController {

    private final UserServiceImpl userService;

    public UsersRestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> apiGetAllUsers() {
        List<User> users = userService.listOfUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> update(@RequestBody User user) {
        userService.addUser(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/principal")
    public User getPrincipalInfo(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user;
    }

    @PostMapping("/add")
    public List<User> addUser(@RequestBody User user) {
        userService.addUser(user);
        return userService.listOfUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(userService.getUserById(id).getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
