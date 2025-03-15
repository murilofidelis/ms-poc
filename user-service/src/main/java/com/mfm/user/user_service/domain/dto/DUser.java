package com.mfm.user.user_service.domain.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mfm.user.user_service.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DUser {

    private Integer id;

    private String name;

    private String email;

    private String userName;

    private String password;

    public DUser(User user) {
        if (user == null) {
            return;
        }
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
