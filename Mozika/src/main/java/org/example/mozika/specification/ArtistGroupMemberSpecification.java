package org.example.mozika.specification;

import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.models.dto.ArtistGroupMemberSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ArtistGroupMemberSpecification {

    public static Specification<ArtistGroupMember> filter(ArtistGroupMemberSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=groupartistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getGroupartistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("groupartistidArtists"), object.getGroupartistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=memberartistidArtists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getMemberartistidArtists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("memberartistidArtists"), object.getMemberartistidArtists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=fullName | columnType=[varchar] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("fullName"), object.getFullName(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=roleinstrumentidInstrumentRoles | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getRoleinstrumentidInstrumentRoles()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("roleinstrumentidInstrumentRoles"), object.getRoleinstrumentidInstrumentRoles()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=photoUrl | columnType=[text] | isText=true | isNumeric=false");

            
            WebUtils.addLikeIfPresent(criteriaBuilder, root.get("photoUrl"), object.getPhotoUrl(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=statusidMemberStatuses | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getStatusidMemberStatuses()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("statusidMemberStatuses"), object.getStatusidMemberStatuses()
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
