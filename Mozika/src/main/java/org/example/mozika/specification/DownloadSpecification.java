package org.example.mozika.specification;

import org.example.mozika.models.Download;
import org.example.mozika.models.dto.DownloadSearch;
import org.example.mozika.utils.WebUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class DownloadSpecification {

    public static Specification<Download> filter(DownloadSearch object) {
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
            
            System.out.println("🔍 TEMPLATE DEBUG: name=playlistidPlaylists | columnType=[long] | isText=false | isNumeric=true");

            
            if(object.getPlaylistidPlaylists()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("playlistidPlaylists"), object.getPlaylistidPlaylists()
                    )
                );
            }
            
            System.out.println("🔍 TEMPLATE DEBUG: name=downloadedAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getDownloadedAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("downloadedAt"), object.getDownloadedAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("downloadedAt"), object.getDownloadedAtMin(), object.getDownloadedAtMax(), predicates);
            
            System.out.println("🔍 TEMPLATE DEBUG: name=expiresAt | columnType=[timestamp] | isText=false | isNumeric=false");

            
            if(object.getExpiresAt()!=null){
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("expiresAt"), object.getExpiresAt()
                    )
                );
            }
            WebUtils.addInterval(criteriaBuilder, root.get("expiresAt"), object.getExpiresAtMin(), object.getExpiresAtMax(), predicates);
            
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
