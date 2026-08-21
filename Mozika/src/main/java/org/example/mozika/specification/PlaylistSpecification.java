package org.example.mozika.specification;

import org.example.mozika.models.Playlist;
import org.example.mozika.models.dto.PlaylistSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSpecification {

    public static Specification<Playlist> filter(PlaylistSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=owneruseridUsers | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getOwneruseridUsers()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("owneruseridUsers"), object.getOwneruseridUsers()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=name | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("name"), object.getName(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=visibilityidPlaylistVisibilities | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getVisibilityidPlaylistVisibilities()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("visibilityidPlaylistVisibilities"), object.getVisibilityidPlaylistVisibilities()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=shareToken | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("shareToken"), object.getShareToken(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=createdAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getCreatedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("createdAt"), object.getCreatedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("createdAt"), object.getCreatedAtMin(), object.getCreatedAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=updatedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getUpdatedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("updatedAt"), object.getUpdatedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("updatedAt"), object.getUpdatedAtMin(), object.getUpdatedAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
