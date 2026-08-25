package org.example.mozika.specification;

import org.example.mozika.models.UsersInfos;
import org.example.mozika.models.dto.UsersInfosSearch;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class UsersInfosSpecification {

    public static Specification<UsersInfos> filter(UsersInfosSearch object) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (object.getId() != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("id"), object.getId())
                );
            }

            if (object.getName() != null && !object.getName().isEmpty()) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + object.getName().toLowerCase() + "%"
                    )
                );
            }

            if (object.getLastName() != null && !object.getLastName().isEmpty()) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + object.getLastName().toLowerCase() + "%"
                    )
                );
            }

            if (object.getUserName() != null && !object.getUserName().isEmpty()) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("userName")),
                        "%" + object.getUserName().toLowerCase() + "%"
                    )
                );
            }

            if (object.getUser() != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("user"), object.getUser())
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
