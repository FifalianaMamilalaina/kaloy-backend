package org.example.mozika.specification;

import org.example.mozika.models.Report;
import org.example.mozika.models.dto.ReportSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ReportSpecification {

    public static Specification<Report> filter(ReportSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=reporteruseridUsers | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getReporteruseridUsers()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("reporteruseridUsers"), object.getReporteruseridUsers()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=targettypeidInteractionTargets | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getTargettypeidInteractionTargets()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("targettypeidInteractionTargets"), object.getTargettypeidInteractionTargets()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=targetId | columnType=[int8] | isText=false | isNumeric=true");

            
            if(object.getTargetId()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("targetId"), object.getTargetId()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("targetId"), object.getTargetIdMin(), object.getTargetIdMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=reason | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("reason"), object.getReason(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=statusidReportStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getStatusidReportStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("statusidReportStatuses"), object.getStatusidReportStatuses()
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
