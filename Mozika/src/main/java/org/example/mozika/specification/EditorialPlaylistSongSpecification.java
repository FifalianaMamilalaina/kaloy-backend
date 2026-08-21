package org.example.mozika.specification;

import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.models.dto.EditorialPlaylistSongSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class EditorialPlaylistSongSpecification {

    public static Specification<EditorialPlaylistSong> filter(EditorialPlaylistSongSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=editorialplaylistidEditorialPlaylists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getEditorialplaylistidEditorialPlaylists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("editorialplaylistidEditorialPlaylists"), object.getEditorialplaylistidEditorialPlaylists()
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
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
