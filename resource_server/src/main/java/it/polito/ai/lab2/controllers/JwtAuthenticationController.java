package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.config.JwtTokenUtil;
import it.polito.ai.lab2.dtos.UserDTO;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.RegistrationDetails;
import it.polito.ai.lab2.services.CustomUserDetailsService;
import it.polito.ai.lab2.services.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;

@RestController
@Log(topic = "JwtAuthenticationController")
@CrossOrigin
@RequestMapping("/API/authentication")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDTO authenticationRequest) {
        log.info("createAuthenticationToken(" + authenticationRequest + ")");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getId(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User " + authenticationRequest.getId() + " is disabled");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials {" + authenticationRequest.getId() + "," + authenticationRequest.getPassword() + "}");
        } catch (InternalAuthenticationServiceException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        UserDetails userDetails;
        try {
             userDetails = customUserDetailsService
                .loadUserByUsername(authenticationRequest.getId());
        } catch (UserNotVerifiedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AbstractMap.SimpleEntry<>("id_token", token));
    }

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@ModelAttribute RegistrationDetails registrationDetails) {
        log.info("Registration attempt for student " + registrationDetails.getId());
        try {
            return ResponseEntity.ok(userService.registerStudent(registrationDetails));
        } catch (InvalidIdEmailException | UserAlreadyRegisteredException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("register/professor")
    public ResponseEntity<?> registerProfessor(@ModelAttribute RegistrationDetails registrationDetails) {
        log.info("Registration attempt for student " + registrationDetails.getId());
        try {
            return ResponseEntity.ok(userService.registerProfessor(registrationDetails));
        } catch (InvalidIdEmailException | UserAlreadyRegisteredException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}/confirmEmail/{token}")
    public ResponseEntity<?> confirmEmail(@PathVariable String id, @PathVariable String token) {
        try {
            return ResponseEntity.ok(userService.confirmEmail(id, token));
        } catch (UserNotFoundException | ConfirmEmailTokenNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
