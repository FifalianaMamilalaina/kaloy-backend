package org.example.mozika.services.interfaces;

import org.example.mozika.models.UpNextQueue;
import org.example.mozika.models.dto.UpNextQueueSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface UpNextQueueService {
    Page<UpNextQueue> getAllUpNextQueue(Pageable pageable);

    Page<UpNextQueue> getAllUpNextQueue(Pageable pageable, UpNextQueueSearch object);

    UpNextQueue getUpNextQueueById(Long id);

    public String exportUpNextQueueToCSV(List<UpNextQueue> upNextQueue);

    

    UpNextQueue createUpNextQueue(UpNextQueue upNextQueue);

    UpNextQueue updateUpNextQueue(Long id, UpNextQueue upNextQueue);

    void deleteUpNextQueue(Long id);
    

}
