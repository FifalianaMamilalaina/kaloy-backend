package org.example.mozika.specification;

import org.example.mozika.models.User;
import org.example.mozika.models.dto.UserSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filter(UserSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=email | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("email"), object.getEmail(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=phone | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("phone"), object.getPhone(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=emailVerifiedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getEmailVerifiedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("emailVerifiedAt"), object.getEmailVerifiedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("emailVerifiedAt"), object.getEmailVerifiedAtMin(), object.getEmailVerifiedAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=phoneVerifiedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getPhoneVerifiedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("phoneVerifiedAt"), object.getPhoneVerifiedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("phoneVerifiedAt"), object.getPhoneVerifiedAtMin(), object.getPhoneVerifiedAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=passwordHash | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("passwordHash"), object.getPasswordHash(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=roleidUserRoles | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getRoleidUserRoles()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("roleidUserRoles"), object.getRoleidUserRoles()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=statusidUserStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getStatusidUserStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("statusidUserStatuses"), object.getStatusidUserStatuses()
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
