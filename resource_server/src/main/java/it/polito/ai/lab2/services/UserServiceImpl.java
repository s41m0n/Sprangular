package it.polito.ai.lab2.services;

import it.polito.ai.lab2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean confirmEmail(String id) {
        return false;
    }
}
