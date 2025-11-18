package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.Machine;

@Repository
public interface MachineDAO extends JpaRepository<Machine, Integer>{

}
