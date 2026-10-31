package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Like;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import org.example.mozika.models.User;
import org.example.mozika.models.InteractionTarget;



public interface LikeRepository extends JpaRepository<Like, Long>, JpaSpecificationExecutor<Like> {

List<Like> findByUseridUsers(User useridUsers);

List<Like> findByUseridUsersAndTargettypeidInteractionTargets(
        User user, InteractionTarget targetType);

Optional<Like> findByUseridUsersAndTargettypeidInteractionTargetsAndTargetId(
        User user, InteractionTarget targetType, Long targetId);



}
