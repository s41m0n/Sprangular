package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.AssignmentSolution;
import it.polito.ai.lab2.utility.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSolutionRepository extends JpaRepository<AssignmentSolution, Long> {

  List<AssignmentSolution> findByAssignmentIdAndStatus(Long assignmentId, AssignmentStatus status);

  Optional<AssignmentSolution> findByAssignmentIdAndStudentId(Long assignmentId, String studentId);

  List<AssignmentSolution> findByStudentId(String studentId);
}
