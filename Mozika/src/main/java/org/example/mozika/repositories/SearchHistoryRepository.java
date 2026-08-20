package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.SearchHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;



public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long>, JpaSpecificationExecutor<SearchHistory> {

List<SearchHistory> findByUseridUsers(User useridUsers);



}
