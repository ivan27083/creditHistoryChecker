package com.userService.repository;

import com.userService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Override
    void deleteById(Integer id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(subscriber) > 0 THEN true ELSE false END " +
            "FROM User subscriber JOIN subscriber.subscribedTo target " +
            "WHERE subscriber.id = :subscriberId AND target.id = :targetId")
    boolean existsSubscription(@Param("subscriberId") Integer subscriberId, @Param("targetId") Integer targetId);
}