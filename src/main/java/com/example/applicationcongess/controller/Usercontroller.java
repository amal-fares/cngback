package com.example.applicationcongess.controller;

import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class Usercontroller {
    @Autowired
    UserService userService;


}
