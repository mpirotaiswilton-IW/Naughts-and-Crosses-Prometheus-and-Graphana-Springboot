package com.max_pw_iw.naughtsandcrosses.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    
    @NotBlank(message =  "username cannot be blank")
    @NonNull
	private String username;

	@NotBlank(message =  "password cannot be blank")
    @NonNull
	private String password;

}
