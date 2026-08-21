package org.example.mozika.specification;

import org.example.mozika.models.Album;
import org.example.mozika.models.dto.AlbumSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class AlbumSpecification {

    public static Specification<Album> filter(AlbumSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=artistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getArtistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("artistidArtists"), object.getArtistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=title | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("title"), object.getTitle(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=coverUrl | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("coverUrl"), object.getCoverUrl(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=releaseDate | columnType=[date] | isText=false | isNumeric=false");

            
            if(object.getReleaseDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("releaseDate"), object.getReleaseDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("releaseDate"), object.getReleaseDateMin(), object.getReleaseDateMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=createdAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getCreatedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("createdAt"), object.getCreatedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("createdAt"), object.getCreatedAtMin(), object.getCreatedAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
