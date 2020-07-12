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
    public String confirmToken(@PathVariable String token, Model model) {
        log.info("confirmToken(" + token +") called");
        if(!notificationService.confirm(token)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, token);
        model.addAttribute("tokenResult", "You've successfully accepted the team invitation!");
        return "token";
    }

    @GetMapping("/reject/{token}")
    public String rejectToken(@PathVariable String token, Model model) {
        log.info("rejectToken(" + token +") called");
        if(!notificationService.reject(token)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, token);
        model.addAttribute("tokenResult",  "You've successfully rejected the team invitation");
        return "token";
    }
}
