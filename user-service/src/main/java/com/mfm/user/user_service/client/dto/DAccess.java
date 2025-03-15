package com.mfm.user.user_service.client.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DAccess {

    private Integer id;

    private String userName;

    private String password;

    public DAccess(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
