package com.userService.repository;

import com.userService.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(value = "SELECT r.* FROM role r JOIN users_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId", nativeQuery = true)
    List<Role> findByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users_roles (role_id, user_id) VALUES (:roleId, :userId)", nativeQuery = true)
    void insertIntoUserRoles(@Param("userId") int userId, @Param("roleId") int roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users_roles WHERE user_id = :userId", nativeQuery = true)
    void removeAllByUserId(@Param("userId") int userId);

    Optional<Role> findRoleByName(String name);
}