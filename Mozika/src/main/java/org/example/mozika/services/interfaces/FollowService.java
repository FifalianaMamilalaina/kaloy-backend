package org.example.mozika.services.interfaces;

import org.example.mozika.models.Follow;
import org.example.mozika.models.dto.FollowSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface FollowService {
    Page<Follow> getAllFollow(Pageable pageable);

    Page<Follow> getAllFollow(Pageable pageable, FollowSearch object);

    Follow getFollowById(Long id);

    public String exportFollowToCSV(List<Follow> follow);

    

    Follow createFollow(Follow follow);

    Follow updateFollow(Long id, Follow follow);

    void deleteFollow(Long id);
    

}
