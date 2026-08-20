package org.example.mozika.specification;

import org.example.mozika.models.Client;
import org.example.mozika.models.dto.ClientSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ClientSpecification {

    public static Specification<Client> filter(ClientSearch object) {
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
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
