package com.mfm.user.access_service.domain.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
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
public class DAccess {

    private Integer id;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(max = 25)
    private String userName;

    @NotEmpty
    @NotBlank
    @NotNull
    private String password;

    public DAccess(Integer id) {
        this.id = id;
    }
}
