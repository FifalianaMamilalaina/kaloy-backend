package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.InstrumentRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface InstrumentRoleRepository
        extends JpaRepository<InstrumentRole, Long>, JpaSpecificationExecutor<InstrumentRole> {

    Optional<InstrumentRole> findByLabel(String label);

}
