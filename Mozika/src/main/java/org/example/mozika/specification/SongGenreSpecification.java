package org.example.mozika.specification;

import org.example.mozika.models.SongGenre;
import org.example.mozika.models.dto.SongGenreSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class SongGenreSpecification {

    public static Specification<SongGenre> filter(SongGenreSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=songidSongs | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getSongidSongs()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("songidSongs"), object.getSongidSongs()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=genreidGenres | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getGenreidGenres()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("genreidGenres"), object.getGenreidGenres()
                    )
                );
            }
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
