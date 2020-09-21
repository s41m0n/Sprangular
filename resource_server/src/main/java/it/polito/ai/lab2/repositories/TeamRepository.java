package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Student;
import it.polito.ai.lab2.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

  @Query("SELECT s FROM Student s INNER JOIN s.teams t WHERE t.id=:id")
  List<Student> getTeamMembers(Long id);

}
