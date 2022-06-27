package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    public void addUser(User user);

    public void deleteUser(Long id);

    public User getUserById(Long id);

    public List<User> listOfUsers();
}
