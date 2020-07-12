package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
