package it.polito.ai.lab2.services;

import it.polito.ai.lab2.entities.*;
import it.polito.ai.lab2.exceptions.*;
import it.polito.ai.lab2.pojos.RegistrationDetails;
import it.polito.ai.lab2.repositories.ConfirmEmailTokenRepository;
import it.polito.ai.lab2.repositories.RoleRepository;
import it.polito.ai.lab2.repositories.UserRepository;
import it.polito.ai.lab2.utility.Utility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  ConfirmEmailTokenRepository confirmEmailTokenRepository;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  NotificationService notificationService;

  @Override
  public boolean registerStudent(RegistrationDetails sDetails) {
    if (!sDetails.getEmail().substring(0, sDetails.getEmail().indexOf("@")).equals(sDetails.getId())
        || !sDetails.getEmail().matches("^s[1-9][0-9]*@studenti.polito.it$"))
      throw new InvalidIdEmailException("Student format: id -> s<numbers>, email -> <id>@studenti.polito.it");
    if (userRepository.existsById(sDetails.getId()))
      throw new UserAlreadyRegisteredException("User " + sDetails.getId() + " already registered");

    Path photoPath = Utility.PHOTOS_DIR.resolve(sDetails.getId());

    Student student = new Student();
    student.setEmail(sDetails.getEmail());
    student.setId(sDetails.getId());
    student.setName(sDetails.getName());
    student.setSurname(sDetails.getSurname());
    student.setPassword(passwordEncoder.encode(sDetails.getPassword()));
    Role role = roleRepository.findByName(Utility.STUDENT_ROLE)
        .orElseThrow(() -> new UserRoleNotFoundException(Utility.STUDENT_ROLE + " not found"));
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

    ConfirmEmailToken confirmEmailToken = new ConfirmEmailToken(UUID.randomUUID().toString(), sDetails.getId());
    confirmEmailTokenRepository.save(confirmEmailToken);

    notificationService.sendMessage(
        sDetails.getEmail(), "Account Creation", getPredefinedRegisterMessage(sDetails.getId(), confirmEmailToken));
    return true;
  }

  @Override
  public boolean registerProfessor(RegistrationDetails pDetails) {
    if (!pDetails.getEmail().substring(0, pDetails.getEmail().indexOf("@")).equals(pDetails.getId())
        || !pDetails.getEmail().matches("^d[1-9][0-9]*@polito.it$"))
      throw new InvalidIdEmailException("Professor format: id -> d<numbers>, email -> <id>@polito.it");
    if (userRepository.existsById(pDetails.getId()))
      throw new UserAlreadyRegisteredException("User " + pDetails.getId() + " already registered");

    Path photoPath = Utility.PHOTOS_DIR.resolve(pDetails.getId());

    Professor professor = new Professor();
    professor.setEmail(pDetails.getEmail());
    professor.setId(pDetails.getId());
    professor.setName(pDetails.getName());
    professor.setSurname(pDetails.getSurname());
    professor.setPassword(passwordEncoder.encode(pDetails.getPassword()));
    Role role = roleRepository.findByName(Utility.PROFESSOR_ROLE)
        .orElseThrow(() -> new UserRoleNotFoundException(Utility.PROFESSOR_ROLE + " not found"));
    professor.addRole(role);
    professor.setVerified(false);
    professor.setPhotoPath(photoPath.toString());
    userRepository.save(professor);

    try {
      Files.copy(pDetails.getPhoto().getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the file: " + e.getMessage());
    }

    ConfirmEmailToken confirmEmailToken = new ConfirmEmailToken(UUID.randomUUID().toString(), pDetails.getId());
    confirmEmailTokenRepository.save(confirmEmailToken);

    notificationService.sendMessage(
        pDetails.getEmail(), "Account Creation", getPredefinedRegisterMessage(pDetails.getId(), confirmEmailToken));
    return true;
  }

  @Override
  public boolean confirmEmail(String id, String token) {
    User user = userRepository.findById(id).orElseThrow(
        () -> new UserNotFoundException("User " + id + " does not exist"));
    ConfirmEmailToken emailToken = confirmEmailTokenRepository.findById(token).orElseThrow(
        () -> new ConfirmEmailTokenNotFoundException("Token " + token + " does not exist"));

    if (emailToken.getUserId().equals(id)) {
      user.setVerified(true);
      userRepository.save(user);
      confirmEmailTokenRepository.delete(emailToken);
      return true;
    }
    return false;
  }

  private String getPredefinedRegisterMessage(String id, ConfirmEmailToken confirmEmailToken) {
    String url = "https://localhost:4200/user/" + id + "/confirmEmail/" + confirmEmailToken.getToken();

    return "Welcome to SpringExample app!\n\n" +
        "Your username to access the system is: " + id +
        "\n\nConfirm your email address clicking this link:\n" + url +
        "\n\nAuthenticate through:\nhttps://localhost:4200/home?doLogin=true" +
        "\n\nBest Regards,\nSpringExample Team";
  }
}
