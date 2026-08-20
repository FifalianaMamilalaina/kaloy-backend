package org.example.mozika.specification;

import org.example.mozika.models.NotificationPreference;
import org.example.mozika.models.dto.NotificationPreferenceSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class NotificationPreferenceSpecification {

    public static Specification<NotificationPreference> filter(NotificationPreferenceSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=notificationtypeidNotificationTypes | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getNotificationtypeidNotificationTypes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("notificationtypeidNotificationTypes"), object.getNotificationtypeidNotificationTypes()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=isEnabled | columnType=[bool] | isText=false | isNumeric=false");

            
            if(object.getIsEnabled()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("isEnabled"), object.getIsEnabled()
                    )
                );
            }
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
