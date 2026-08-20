package org.example.mozika.services.interfaces;

import org.example.mozika.models.Like;
import org.example.mozika.models.dto.LikeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface LikeService {
    Page<Like> getAllLike(Pageable pageable);

    Page<Like> getAllLike(Pageable pageable, LikeSearch object);

    Like getLikeById(Long id);

    public String exportLikeToCSV(List<Like> like);

    

    Like createLike(Like like);

    Like updateLike(Long id, Like like);

    void deleteLike(Long id);
    

}
