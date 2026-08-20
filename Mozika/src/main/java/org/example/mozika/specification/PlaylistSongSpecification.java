package org.example.mozika.specification;

import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.dto.PlaylistSongSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongSpecification {

    public static Specification<PlaylistSong> filter(PlaylistSongSearch object) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            System.out.println("🔍 TEMPLATE DEBUG: name=id | columnType=[int8] | isText=false | isNumeric=true");

            
            if(object.getId()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("id"), object.getId()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=playlistidPlaylists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getPlaylistidPlaylists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("playlistidPlaylists"), object.getPlaylistidPlaylists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=songidSongs | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getSongidSongs()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("songidSongs"), object.getSongidSongs()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=position | columnType=[int4] | isText=false | isNumeric=true");

            
            if(object.getPosition()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("position"), object.getPosition()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("position"), object.getPositionMin(), object.getPositionMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=addedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getAddedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("addedAt"), object.getAddedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("addedAt"), object.getAddedAtMin(), object.getAddedAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
