package org.example.mozika.specification;

import org.example.mozika.models.Notification;
import org.example.mozika.models.dto.NotificationSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification {

    public static Specification<Notification> filter(NotificationSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=typeidNotificationTypes | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getTypeidNotificationTypes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("typeidNotificationTypes"), object.getTypeidNotificationTypes()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=content | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("content"), object.getContent(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=relatedentitytypeidInteractionTargets | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getRelatedentitytypeidInteractionTargets()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("relatedentitytypeidInteractionTargets"), object.getRelatedentitytypeidInteractionTargets()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=relatedEntityId | columnType=[int8] | isText=false | isNumeric=true");

            
            if(object.getRelatedEntityId()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("relatedEntityId"), object.getRelatedEntityId()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("relatedEntityId"), object.getRelatedEntityIdMin(), object.getRelatedEntityIdMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=isRead | columnType=[bool] | isText=false | isNumeric=false");

            
            if(object.getIsRead()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("isRead"), object.getIsRead()
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
