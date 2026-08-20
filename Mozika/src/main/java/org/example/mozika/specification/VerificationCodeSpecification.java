package org.example.mozika.specification;

import org.example.mozika.models.VerificationCode;
import org.example.mozika.models.dto.VerificationCodeSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class VerificationCodeSpecification {

    public static Specification<VerificationCode> filter(VerificationCodeSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=channelidVerificationChannels | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getChannelidVerificationChannels()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("channelidVerificationChannels"), object.getChannelidVerificationChannels()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=destination | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("destination"), object.getDestination(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=code | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("code"), object.getCode(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=expiresAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getExpiresAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("expiresAt"), object.getExpiresAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("expiresAt"), object.getExpiresAtMin(), object.getExpiresAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=consumedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getConsumedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("consumedAt"), object.getConsumedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("consumedAt"), object.getConsumedAtMin(), object.getConsumedAtMax(), predicates);
            
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
