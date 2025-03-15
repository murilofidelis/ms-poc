package com.mfm.user.access_service.repository;

import com.mfm.user.access_service.domain.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository extends JpaRepository<Access, Integer> {
}
