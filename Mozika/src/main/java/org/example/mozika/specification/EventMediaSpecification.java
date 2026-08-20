package org.example.mozika.specification;

import org.example.mozika.models.EventMedia;
import org.example.mozika.models.dto.EventMediaSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class EventMediaSpecification {

    public static Specification<EventMedia> filter(EventMediaSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=eventidEvents | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getEventidEvents()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("eventidEvents"), object.getEventidEvents()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=uploaderuseridUsers | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getUploaderuseridUsers()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("uploaderuseridUsers"), object.getUploaderuseridUsers()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=mediatypeidMediaTypes | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getMediatypeidMediaTypes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("mediatypeidMediaTypes"), object.getMediatypeidMediaTypes()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=url | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("url"), object.getUrl(), predicates);
            
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
