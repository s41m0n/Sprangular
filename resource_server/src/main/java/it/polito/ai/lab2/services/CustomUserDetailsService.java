package it.polito.ai.lab2.services;

import it.polito.ai.lab2.entities.Role;
import it.polito.ai.lab2.entities.User;
import it.polito.ai.lab2.exceptions.UserNotVerifiedException;
import it.polito.ai.lab2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

  // UserDetails = username, password and authorities
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findById(username).orElseThrow(
        () -> new UsernameNotFoundException("User not found with id: " + username));
    if (!user.isVerified())
      throw new UserNotVerifiedException("User " + username + " not verified yet. Check your email.");
    return new org.springframework.security.core.userdetails.User(
        user.getId(), user.getPassword(), getAuthorities(user));
  }

  private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
    String[] userRoles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);
    return AuthorityUtils.createAuthorityList(userRoles);
  }
}
