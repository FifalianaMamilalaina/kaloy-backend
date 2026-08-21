package org.example.mozika.specification;

import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.models.dto.UserStatusHistorySearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class UserStatusHistorySpecification {

    public static Specification<UserStatusHistory> filter(UserStatusHistorySearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=previousstatusidUserStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getPreviousstatusidUserStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("previousstatusidUserStatuses"), object.getPreviousstatusidUserStatuses()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=newstatusidUserStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getNewstatusidUserStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("newstatusidUserStatuses"), object.getNewstatusidUserStatuses()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=reason | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("reason"), object.getReason(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=changedbyuseridUsers | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getChangedbyuseridUsers()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("changedbyuseridUsers"), object.getChangedbyuseridUsers()
                    )
                );
            }
            
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
