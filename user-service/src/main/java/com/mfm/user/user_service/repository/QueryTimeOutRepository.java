package com.mfm.user.user_service.repository;

import com.mfm.user.user_service.domain.RootEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryTimeOutRepository extends JpaRepository<RootEntity, Integer> {

    @Query(value = "SELECT PG_SLEEP(3.5)", nativeQuery = true)
    void getTime();

    @QueryHints(
            @QueryHint(name = "jakarta.persistence.query.timeout", value = "4000")
    )
    @Query(value = "SELECT PG_SLEEP(3.5)", nativeQuery = true)
    void getTimeCustom();
}
