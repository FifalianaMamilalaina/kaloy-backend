package org.example.mozika.specification;

import org.example.mozika.models.Artist;
import org.example.mozika.models.dto.ArtistSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ArtistSpecification {

    public static Specification<Artist> filter(ArtistSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=artisttypeidArtistTypes | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getArtisttypeidArtistTypes()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("artisttypeidArtistTypes"), object.getArtisttypeidArtistTypes()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=stageName | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("stageName"), object.getStageName(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=activeSinceYear | columnType=[int4] | isText=false | isNumeric=true");

            
            if(object.getActiveSinceYear()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("activeSinceYear"), object.getActiveSinceYear()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("activeSinceYear"), object.getActiveSinceYearMin(), object.getActiveSinceYearMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=photoUrl | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("photoUrl"), object.getPhotoUrl(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=bio | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("bio"), object.getBio(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=verificationstatusidVerificationStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getVerificationstatusidVerificationStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("verificationstatusidVerificationStatuses"), object.getVerificationstatusidVerificationStatuses()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=verifiedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getVerifiedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("verifiedAt"), object.getVerifiedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("verifiedAt"), object.getVerifiedAtMin(), object.getVerifiedAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=isCertified | columnType=[bool] | isText=false | isNumeric=false");

            
            if(object.getIsCertified()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("isCertified"), object.getIsCertified()
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
