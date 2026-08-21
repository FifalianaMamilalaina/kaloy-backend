package org.example.mozika.specification;

import org.example.mozika.models.Comment;
import org.example.mozika.models.dto.CommentSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class CommentSpecification {

    public static Specification<Comment> filter(CommentSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=authoruseridUsers | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getAuthoruseridUsers()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("authoruseridUsers"), object.getAuthoruseridUsers()
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=content | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("content"), object.getContent(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=isHidden | columnType=[bool] | isText=false | isNumeric=false");

            
            if(object.getIsHidden()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("isHidden"), object.getIsHidden()
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
