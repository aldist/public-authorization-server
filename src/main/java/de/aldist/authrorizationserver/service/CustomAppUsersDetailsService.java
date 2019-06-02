package de.aldist.authrorizationserver.service;

import de.aldist.authrorizationserver.model.Users;
import de.aldist.authrorizationserver.model.AppUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
Implements UserDetailsService in order to define our own custom loadUserbyUsername function.
The UserDetailsService interface is used to retrieve user-related data. It has one method named
loadUserByUsername() which finds a user entity based on the username and can be overridden to
customize the process of finding the user.
 */

@Service
public class CustomAppUsersDetailsService implements UserDetailsService {

  private final AppUsersRepository userRepository;

  @Autowired
  public CustomAppUsersDetailsService(AppUsersRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final Users user = this.userRepository.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    return org.springframework.security.core.userdetails.User
        .withUsername(username)
        .password(user.getPassword())
        .authorities(user.getRoles())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }
}
