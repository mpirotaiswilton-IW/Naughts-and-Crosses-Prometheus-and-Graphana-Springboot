package com.max_pw_iw.naughtsandcrosses.web;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.max_pw_iw.naughtsandcrosses.dto.AuthRequest;
import com.max_pw_iw.naughtsandcrosses.exception.ErrorResponse;
import com.max_pw_iw.naughtsandcrosses.security.TokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "Authentication Controller", description = "Authenticates users to have access to send requests other than get requests")
@AllArgsConstructor
@RestController
public class AuthController {

    private AuthenticationManager authenticationManager;
    private TokenProvider jwtTokenUtil;

    @Operation(summary = "Authenticate User", description = "Returns a JWT token for a registered user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful authentication", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid UserRequest object sent/No body sent", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/authenticate")
	public ResponseEntity<String> createUser(@Valid @RequestBody AuthRequest user) {
		final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return new ResponseEntity<>(token, HttpStatus.OK);
	}



}

