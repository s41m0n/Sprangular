package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.AssignmentSolution;
import it.polito.ai.lab2.utility.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSolutionRepository extends JpaRepository<AssignmentSolution, Long> {

  List<AssignmentSolution> findByAssignment_IdAndStatus(Long assignmentId, AssignmentStatus status);

  Optional<AssignmentSolution> findByAssignment_IdAndStudent_Id(Long assignmentId, String studentId);

  List<AssignmentSolution> findByStudent_Id(String studentId);
}
