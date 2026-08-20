package org.example.mozika.specification;

import org.example.mozika.models.Concert;
import org.example.mozika.models.dto.ConcertSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ConcertSpecification {

    public static Specification<Concert> filter(ConcertSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=title | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("title"), object.getTitle(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=description | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("description"), object.getDescription(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=artistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getArtistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("artistidArtists"), object.getArtistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=venueidVenues | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getVenueidVenues()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("venueidVenues"), object.getVenueidVenues()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=startTime | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getStartTime()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("startTime"), object.getStartTime()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("startTime"), object.getStartTimeMin(), object.getStartTimeMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=endTime | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getEndTime()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("endTime"), object.getEndTime()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("endTime"), object.getEndTimeMin(), object.getEndTimeMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=statusidParticipationStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getStatusidParticipationStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("statusidParticipationStatuses"), object.getStatusidParticipationStatuses()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=respondedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getRespondedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("respondedAt"), object.getRespondedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("respondedAt"), object.getRespondedAtMin(), object.getRespondedAtMax(), predicates);
            
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
