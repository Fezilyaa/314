package ru.kata.spring.boot_security.demo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;



@Controller
public class UsersController {
    private final UserServiceImpl userService;
    @Autowired
    public UsersController(UserServiceImpl userService) {
        this.userService = userService;
    }


    @GetMapping("/user")
    public String showUserInfo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("admin/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("users", userService.getUserById(id));
        return "users";
    }

    @GetMapping("admin")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.listOfUsers());
        return "users";
    }

    @GetMapping("admin/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("admin/{id}/edit")
    public String editUser(User user, @PathVariable Long id, @RequestParam(value = "role") String[] roles) {
        user.setRoles(userService.getRoles(roles));
        userService.addUser(user);
        return "redirect:/admin/";
    }


        @GetMapping("admin/add")
    public String userFormPage (Model model) {
        model.addAttribute("user", new User());
        return "add";
    }

    @PostMapping("admin/add")
    public String addUser (@ModelAttribute("user") User user, @RequestParam(value = "role") String[] roles) {
        user.setRoles(userService.getRoles(roles));
        userService.addUser(user);
        return "redirect:/admin/";
    }

    @GetMapping("admin/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/";
    }



}