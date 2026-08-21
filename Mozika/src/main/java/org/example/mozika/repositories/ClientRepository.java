package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Client;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;



public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

List<Client> findByUseridUsers(User useridUsers);



}
