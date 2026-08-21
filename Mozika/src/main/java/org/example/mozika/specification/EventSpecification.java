package org.example.mozika.specification;

import org.example.mozika.models.Event;
import org.example.mozika.models.dto.EventSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class EventSpecification {

    public static Specification<Event> filter(EventSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=name | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("name"), object.getName(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=description | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("description"), object.getDescription(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=startDate | columnType=[date] | isText=false | isNumeric=false");

            
            if(object.getStartDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("startDate"), object.getStartDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("startDate"), object.getStartDateMin(), object.getStartDateMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=endDate | columnType=[date] | isText=false | isNumeric=false");

            
            if(object.getEndDate()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("endDate"), object.getEndDate()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("endDate"), object.getEndDateMin(), object.getEndDateMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=createdbyartistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getCreatedbyartistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("createdbyartistidArtists"), object.getCreatedbyartistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=moderationstatusidEventModerationStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getModerationstatusidEventModerationStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("moderationstatusidEventModerationStatuses"), object.getModerationstatusidEventModerationStatuses()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=reviewedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getReviewedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("reviewedAt"), object.getReviewedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("reviewedAt"), object.getReviewedAtMin(), object.getReviewedAtMax(), predicates);
            
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
