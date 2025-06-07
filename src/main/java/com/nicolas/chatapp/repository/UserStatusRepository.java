package com.nicolas.chatapp.repository;

import com.nicolas.chatapp.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, String> {

    @Query("SELECT u FROM USER_STATUS u WHERE u.userId = :user_id")
    Optional<UserStatus> findById(@Param("user_id") String user_id);
}
