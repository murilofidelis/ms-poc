package com.mfm.user.user_service.repository;

import com.mfm.user.user_service.domain.User;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface UserRepository extends JpaRepository<User, Integer> {
}
