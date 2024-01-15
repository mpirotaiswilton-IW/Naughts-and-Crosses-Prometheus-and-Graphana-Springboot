package com.max_pw_iw.naughtsandcrosses.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.max_pw_iw.naughtsandcrosses.dto.UserRequest;
import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.entity.User;
import com.max_pw_iw.naughtsandcrosses.exception.ErrorResponse;
import com.max_pw_iw.naughtsandcrosses.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "User Controller", description = "Registers, retrieves and deletes users")
@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

	@Operation(summary = "Retrieves User's Username", description = "Provides a specified user's username by the user's Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retreival of user username", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@GetMapping("/{id}")
	public ResponseEntity<String> findById(@PathVariable Long id) {
		String userName = userService.getUser(id).getUsername();
		return new ResponseEntity<>(userName, HttpStatus.OK);
	}

	@Operation(summary = "Retrieves All Users", description = "Provides a list of JSON ojects of all users in the app database (without their passwords)")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
	@GetMapping("/")
	public ResponseEntity<List<User>> getAllUserName() {
		return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
	}

	@Operation(summary = "Retrieves All User's Games", description = "Provides a list of JSON ojects of all games in the app database played by a user, filtered with their Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retreival of user's games", 
						content = @Content(array = @ArraySchema(schema = @Schema(implementation = Game.class)))),
        @ApiResponse(responseCode = "404", description = "User doesn't exist", 
						content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
	@GetMapping("/{id}/games")
	public ResponseEntity<List<Game>> getAllGamesFromUser(@PathVariable Long id) {
		return new ResponseEntity<>(userService.getAllGamesFromUser(id), HttpStatus.OK);
	}

	@Operation(summary = "Registers User", description = "Returns a JSON object of the newly registered user (without the encoded password)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created new user", 
						content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid UserRequest object sent/No body sent/username submitted already is in the database", 
						content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/register")
	public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest user) {
		userService.saveUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "Delete User Self", description = "Deletes an authorized \"player\" level user's own account from the API's database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successful user's self deletion"),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden, user does not have \"player\" role",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "User doesn't exist",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@PreAuthorize("hasRole('PLAYER')")
	@DeleteMapping("/self")
	public ResponseEntity<User> deleteUserSelf(Authentication authentication) {
		userService.deleteUser(authentication.getName());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(summary = "Delete User Any", description = "Deletes an authorized user from the API's database as an authorized \"admin\" level user ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successful user deletion"),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden, user does not have \"admin\" role",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "User doesn't exist",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<User> createUser(@PathVariable Long id , Authentication authentication) {
		userService.deleteUser(id, authentication.getName());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}



}
