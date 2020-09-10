package it.polito.ai.lab2.services;

import it.polito.ai.lab2.entities.Professor;
import it.polito.ai.lab2.entities.Role;
import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.exceptions.InvalidIdEmailException;
import it.polito.ai.lab2.exceptions.UserAlreadyRegisteredException;
import it.polito.ai.lab2.exceptions.UserNotFoundException;
import it.polito.ai.lab2.exceptions.UserRoleNotFounException;
import it.polito.ai.lab2.pojos.RegistrationDetails;
import it.polito.ai.lab2.repositories.RoleRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    NotificationService notificationService;

    @Override
    public void registerStudent(RegistrationDetails sDetails) {
        if (!sDetails.getEmail().substring(0, 7).equals(sDetails.getId())
                || !sDetails.getEmail().matches("^s[1-9][0-9]*@studenti.polito.it$"))
            throw new InvalidIdEmailException("Student format: id -> s<numbers>, email -> <id>@studenti.polito.it");
        if (userRepository.existsById(sDetails.getId()))
            throw new UserAlreadyRegisteredException("User " + sDetails.getId() + " already registered");
        Path photoPath = Utility.photosDir.resolve(sDetails.getId());
        Student student = new Student();
        student.setEmail(sDetails.getEmail());
        student.setId(sDetails.getId());
        student.setName(sDetails.getName());
        student.setSurname(sDetails.getSurname());
        student.setPassword(passwordEncoder.encode(sDetails.getPassword()));
        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new UserRoleNotFounException("ROLE_STUDENT not found"));
        student.addRole(role);
        student.setVerified(false);
        student.setPhotoPath(photoPath.toString());
        userRepository.save(student);
        try {
            Files.copy(sDetails.getPhoto().getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot store the file: " + e.getMessage());
        }
        notificationService.sendMessage(sDetails.getEmail(), "Account Creation", getPredefinedRegisterMessage(sDetails.getId()));
    }

    @Override
    public void registerProfessor(RegistrationDetails pDetails) {
        if (!pDetails.getEmail().substring(0, 7).equals(pDetails.getId())
                || !pDetails.getEmail().matches("^d[1-9][0-9]*@polito.it$"))
            throw new InvalidIdEmailException("Professor format: id -> d<numbers>, email -> <id>@polito.it");
        if (userRepository.existsById(pDetails.getId()))
            throw new UserAlreadyRegisteredException("User " + pDetails.getId() + " already registered");
        Path photoPath = Utility.photosDir.resolve(pDetails.getId());
        Professor professor = new Professor();
        professor.setEmail(pDetails.getEmail());
        professor.setId(pDetails.getId());
        professor.setName(pDetails.getName());
        professor.setSurname(pDetails.getSurname());
        professor.setPassword(passwordEncoder.encode(pDetails.getPassword()));
        Role role = roleRepository.findByName("ROLE_PROFESSOR")
                .orElseThrow(() -> new UserRoleNotFounException("ROLE_PROFESSOR not found"));
        professor.addRole(role);
        professor.setVerified(false);
        professor.setPhotoPath(photoPath.toString());
        userRepository.save(professor);
        try {
            Files.copy(pDetails.getPhoto().getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Cannot store the file: " + e.getMessage());
        }
        notificationService.sendMessage(pDetails.getEmail(), "Account Creation", getPredefinedRegisterMessage(pDetails.getId()));
    }

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

    private String getPredefinedRegisterMessage(String id) {
        String url = "http://localhost:8080/API/users/" + id + "/confirmEmail";
        return "Welcome to SpringExample app!\n\n" +
                "Your username to access the system is:" + id +
                "\n\nConfirm your email address clicking this link:\n" + url +
                "\n\nAuthenticate through: http://localhost:8080/API/authentication/login" +
                "\n\nBest Regards,\nthe SpringExample Team";
    }
}
