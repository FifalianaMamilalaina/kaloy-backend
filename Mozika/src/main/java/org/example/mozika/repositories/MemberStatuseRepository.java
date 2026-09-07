package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.MemberStatuse;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MemberStatuseRepository extends JpaRepository<MemberStatuse, Long>, JpaSpecificationExecutor<MemberStatuse> {

    Optional<MemberStatuse> findByName(String name);

}
