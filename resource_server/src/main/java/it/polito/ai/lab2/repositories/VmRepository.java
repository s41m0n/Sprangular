package it.polito.ai.lab2.repositories;

import it.polito.ai.lab2.entities.Vm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VmRepository extends JpaRepository<Vm, Long> {
}
