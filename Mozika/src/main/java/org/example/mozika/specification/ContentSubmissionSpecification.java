package org.example.mozika.specification;

import org.example.mozika.models.ContentSubmission;
import org.example.mozika.models.dto.ContentSubmissionSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ContentSubmissionSpecification {

    public static Specification<ContentSubmission> filter(ContentSubmissionSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=artistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getArtistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("artistidArtists"), object.getArtistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=sourceFilename | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("sourceFilename"), object.getSourceFilename(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=fileType | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("fileType"), object.getFileType(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=statusidSubmissionStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getStatusidSubmissionStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("statusidSubmissionStatuses"), object.getStatusidSubmissionStatuses()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=errorMessage | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("errorMessage"), object.getErrorMessage(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=submittedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getSubmittedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("submittedAt"), object.getSubmittedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("submittedAt"), object.getSubmittedAtMin(), object.getSubmittedAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=processedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getProcessedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("processedAt"), object.getProcessedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("processedAt"), object.getProcessedAtMin(), object.getProcessedAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
