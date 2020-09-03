package it.polito.ai.lab2.controllers;

import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log(topic = "UserController")
@RequestMapping("/API/users")
public class UserController {

    @GetMapping("/{id}/confirmEmail")
    public ResponseEntity<?> confirmEmail(@PathVariable String id){
        return null;
    }
}
