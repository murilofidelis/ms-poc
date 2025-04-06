package com.mfm.user.access_service.repository;

import com.mfm.user.access_service.domain.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository extends JpaRepository<Access, Integer> {

    @Query("SELECT count(a) > 0 FROM Access a WHERE a.userName = :userName ")
    boolean exists(@Param("userName") String userName);
}
