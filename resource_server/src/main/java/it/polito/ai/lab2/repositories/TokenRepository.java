package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    List<Token> findAllByExpiryDateAfter(Timestamp t);

    List<Token> findAllByTeamId(Long teamId);

}
