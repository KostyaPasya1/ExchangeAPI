package com.example.demo.contrillers;

import com.example.demo.models.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegistrationController {


    private final UserService userService;
    public RegistrationController(UserService userService) {
        this.userService = userService;

    }



    @GetMapping("/registration")
    public String registration () {
        return ("registration");
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model, BindingResult bindingResult) {



        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        if (!userService.saveUser(user)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }





        userService.saveUser(user);


        return ("redirect:/login");
    }
}
