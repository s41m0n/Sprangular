package it.polito.ai.lab2.services;

import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.exceptions.UserNotFoundException;
import it.polito.ai.lab2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean confirmEmail(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User " + id + " does not exist"));
        if (!user.isVerified()) { //user not verified
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }
        return false; //user already verified
    }
}
