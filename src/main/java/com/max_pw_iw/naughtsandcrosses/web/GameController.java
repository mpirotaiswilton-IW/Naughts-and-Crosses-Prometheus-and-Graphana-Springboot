package com.max_pw_iw.naughtsandcrosses.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.max_pw_iw.naughtsandcrosses.dto.GameRequest;
import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.exception.ErrorResponse;
import com.max_pw_iw.naughtsandcrosses.service.GameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


import lombok.AllArgsConstructor;

@Tag(name = "Game Controller", description = "Create, Join, Play and Delete games of naughts and crosses")
@AllArgsConstructor
@RestController
@RequestMapping("/game")
public class GameController {

    GameService gameService;

	@Operation(summary = "Retrieves Game", description = "Provides a specified game as a JSON object by the game's Id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of game", content = @Content(schema = @Schema(implementation = Game.class))),
        @ApiResponse(responseCode = "404", description = "Game of specified id doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@GetMapping("/{id}")
	public ResponseEntity<Game> GetGameById(@PathVariable Long id) {
		return new ResponseEntity<>(gameService.getGame(id), HttpStatus.OK);
	}

	@Operation(summary = "Retrieves All Games", description = "Provides a list of JSON ojects of all games in the app database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieval of all games", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Game.class))))
	@GetMapping("/")
	public ResponseEntity<List<Game>> GetAllGames() {
		return new ResponseEntity<>(gameService.getAllGames(), HttpStatus.OK);
	}

	@Operation(summary = "Create New Game", description = "Creates a new game based on a user's JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successful game creation",
						content = @Content(schema = @Schema(implementation = Game.class))),
		@ApiResponse(responseCode = "400", description = "Bad request body",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/")
	public ResponseEntity<Game> createGame(@Valid @RequestBody GameRequest gameRequest, Authentication authentication) {
		Game game = new Game(gameRequest.getDoesPrimaryUserStart(), gameRequest.getIsPrimaryUserOs());
		
		return new ResponseEntity<>(gameService.createGame(game, authentication.getName()), HttpStatus.CREATED);
	}

	@Operation(summary = "Join Game", description = "Join a created game based on it's id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully joined game",
						content = @Content(schema = @Schema(implementation = Game.class))),
		@ApiResponse(responseCode = "400", description = "Game already has been joined/User is trying to join game they created",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "Game doesn't exist",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@PutMapping("join/{id}")
	public ResponseEntity<Game> joinGame(@PathVariable Long id, Authentication authentication) {
		return new ResponseEntity<>(gameService.joinGame(id, authentication.getName()), HttpStatus.OK);
	}

	@Operation(summary = "Play a Move", description = "Make a move in the game specified by it's id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully played a move in game",
						content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
		@ApiResponse(responseCode = "400", description = "Illegal move/attempted a move when not user's turn or not joined game/Game has not started or has finished",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "Game doesn't exist",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@PutMapping("/{id}/play/{move}")
	public ResponseEntity<String[]> playMove(@PathVariable("id") Long id, @PathVariable("move") Integer move, Authentication authentication) {
		return new ResponseEntity<>(gameService.addMove(id, move, authentication.getName()).getMoves(),HttpStatus.OK);
	}

	@Operation(summary = "Forfeit Game", description = "Hands victory to adversary in game specified by it's id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully forfeited game",
						content = @Content(schema = @Schema(implementation = Game.class))),
		@ApiResponse(responseCode = "400", description = "",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "Game doesn't exist",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@PutMapping("forfeit/{id}")
	public ResponseEntity<Game> forfeitGame(@PathVariable Long id, Authentication authentication) {
		return new ResponseEntity<>(gameService.forfeitGame(id, authentication.getName()), HttpStatus.OK);
	}

	@Operation(summary = "Delete Game", description = "Deletes a created game based on it's id. \"player\" level users can only delete games they created, \"admin\" level users can delete any game in the app")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted game"),
		@ApiResponse(responseCode = "401", description = "Unauthorized, no JWT entered/unauthenticated",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "User not cleared to delete game because of access level",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "Invalid token",
		 				content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteGame(@PathVariable Long id, Authentication authentication) {
		gameService.deleteGame(id, authentication.getName(), authentication.getAuthorities());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
