package org.example.mozika.specification;

import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ListeningHistorySpecification {

    public static Specification<ListeningHistory> filter(ListeningHistorySearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=useridUsers | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getUseridUsers()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("useridUsers"), object.getUseridUsers()
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=playmodeidPlayModes | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getPlaymodeidPlayModes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("playmodeidPlayModes"), object.getPlaymodeidPlayModes()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=listenedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getListenedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("listenedAt"), object.getListenedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("listenedAt"), object.getListenedAtMin(), object.getListenedAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
