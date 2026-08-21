package org.example.mozika.services.interfaces;

import org.example.mozika.models.InstrumentRole;
import org.example.mozika.models.dto.InstrumentRoleSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface InstrumentRoleService {
    Page<InstrumentRole> getAllInstrumentRole(Pageable pageable);

    Page<InstrumentRole> getAllInstrumentRole(Pageable pageable, InstrumentRoleSearch object);

    InstrumentRole getInstrumentRoleById(Long id);

    public String exportInstrumentRoleToCSV(List<InstrumentRole> instrumentRole);

    

    InstrumentRole createInstrumentRole(InstrumentRole instrumentRole);

    InstrumentRole updateInstrumentRole(Long id, InstrumentRole instrumentRole);

    void deleteInstrumentRole(Long id);
    

}
