package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Like;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;



public interface LikeRepository extends JpaRepository<Like, Long>, JpaSpecificationExecutor<Like> {

List<Like> findByUseridUsers(User useridUsers);



}
