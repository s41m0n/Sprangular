package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.exceptions.UserNotFoundException;
import it.polito.ai.lab2.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Log(topic = "UserController")
@RequestMapping("/API/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}/confirmEmail")
    public ResponseEntity<?> confirmEmail(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userService.confirmEmail(id));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
