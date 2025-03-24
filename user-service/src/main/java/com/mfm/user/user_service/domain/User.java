package com.mfm.user.user_service.domain;

import com.mfm.user.user_service.domain.dto.DUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "TB_USER")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "USER_SEQ", sequenceName = "USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @Column(name = "ID")
    private Integer id;

    @NotNull
    @Size(max = 60)
    @Column(name = "NAME")
    private String name;

    @Size(max = 200)
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ACCESS_ID")
    private Integer idAccess;

    public User(DUser dUser) {
        this.id = dUser.getId();
        this.name = dUser.getName();
        this.email = dUser.getEmail();
    }
}
