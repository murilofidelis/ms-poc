package com.mfm.user.user_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RootEntity {

    @Id
    private Integer id;
}
