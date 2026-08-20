package org.example.mozika.specification;

import org.example.mozika.models.SearchHistory;
import org.example.mozika.models.dto.SearchHistorySearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class SearchHistorySpecification {

    public static Specification<SearchHistory> filter(SearchHistorySearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=queryText | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("queryText"), object.getQueryText(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=searchedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getSearchedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("searchedAt"), object.getSearchedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("searchedAt"), object.getSearchedAtMin(), object.getSearchedAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
