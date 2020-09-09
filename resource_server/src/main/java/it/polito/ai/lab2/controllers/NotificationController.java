package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.services.NotificationService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Log(topic = "NotificationController")
@RequestMapping("/API/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{token}")
    public boolean confirmToken(@PathVariable String token) {
        log.info("confirmToken(" + token + ") called");
        if (!notificationService.confirm(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, token);
        }
        return true;
    }

    @GetMapping("/reject/{token}")
    public boolean rejectToken(@PathVariable String token) {
        log.info("rejectToken(" + token + ") called");
        if (!notificationService.reject(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, token);
        }
        return true;
    }
}
