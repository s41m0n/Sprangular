package it.polito.ai.lab2.controllers;

import it.polito.ai.lab2.config.JwtTokenUtil;
import it.polito.ai.lab2.dtos.UserDTO;
import it.polito.ai.lab2.services.CustomUserDetailsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;

@RestController
@Log(topic = "JwtAuthenticationController")
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @PostMapping("/API/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDTO authenticationRequest) {
        log.info("createAuthenticationToken(" + authenticationRequest + ")");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getId(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User `" + authenticationRequest.getId() + "` is disabled");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials {" + authenticationRequest.getId() + "," + authenticationRequest.getPassword() + "}");
        }

        final UserDetails userDetails = customUserDetailsService
                .loadUserByUsername(authenticationRequest.getId());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AbstractMap.SimpleEntry<>("id_token", token));
    }
}
