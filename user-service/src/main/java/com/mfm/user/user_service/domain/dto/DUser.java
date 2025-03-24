package com.mfm.user.user_service.domain.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mfm.user.user_service.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "name is null")
    @NotEmpty(message = "name is empty")
    @NotBlank(message = "is blank")
    @Size(max = 60)
    private String name;

    @Size(max = 200)
    private String email;

    @Size(max = 25)
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
