package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.UserStatusHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;



public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistory, Long>, JpaSpecificationExecutor<UserStatusHistory> {

List<UserStatusHistory> findByUseridUsers(User useridUsers);



}
