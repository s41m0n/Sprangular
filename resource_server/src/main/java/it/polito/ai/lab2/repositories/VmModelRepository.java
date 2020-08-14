package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VmModelRepository extends JpaRepository<VmModel, Long> {
}
