package com.max_pw_iw.naughtsandcrosses.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.entity.GameState;
import com.max_pw_iw.naughtsandcrosses.entity.User;
import com.max_pw_iw.naughtsandcrosses.exception.EntityNotFoundException;
import com.max_pw_iw.naughtsandcrosses.exception.GameNotActiveException;
import com.max_pw_iw.naughtsandcrosses.exception.IllegalMoveException;
import com.max_pw_iw.naughtsandcrosses.exception.IllegalUserJoinException;
import com.max_pw_iw.naughtsandcrosses.exception.UserNotPlayerException;
import com.max_pw_iw.naughtsandcrosses.exception.WrongTurnMoveException;
import com.max_pw_iw.naughtsandcrosses.repository.GameRepository;
// import com.max_pw_iw.naughtsandcrosses.repository.UserRepository;
//import com.max_pw_iw.naughtsandcrosses.validation.Move;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameServiceImpl implements GameService{

    private GameRepository gameRepository;
    private UserService userService;

    @Override
    public Game getGame(long id) {
        Optional<Game> game = gameRepository.findById(id);
        return unwrapGame(game, id);
    }

    @Override
    public List<Game> getAllGames() {
        return (List<Game>) gameRepository.findAll();
    }

    @Override
    public Game createGame(Game game, String username) {
        User user = userService.getUser(username);
        game.setPrimaryUser(user);
        return gameRepository.save(game);
    }

    @Override
    public Game joinGame(long id, String username) {
        User user = userService.getUser(username);
        Optional<Game> game = gameRepository.findById(id);
        Game unwrappedGame = unwrapGame(game, id);
        if(user == unwrappedGame.getPrimaryUser() || unwrappedGame.getGameState() != GameState.INITIALIZED){
            throw new IllegalUserJoinException(user);
        }
        unwrappedGame.setSecondaryUser(user);
        unwrappedGame.setGameState(GameState.ACTIVE);
        unwrappedGame.setDateStarted(LocalDate.now());
        unwrappedGame.setLastActionDate(LocalDate.now());
        return gameRepository.save(unwrappedGame);

    }

    @Override
    public Game addMove(long id, int move, String username) {

        User user = userService.getUser(username);

        if(move > 9 || move < 1){
            throw new IllegalMoveException(move, user);
        }

        Optional<Game> game = gameRepository.findById(id);
        Game unwrappedGame = unwrapGame(game, id);
        if (unwrappedGame.getGameState() != GameState.ACTIVE){
            throw new GameNotActiveException(id, unwrappedGame.getGameState());
        } /*else if(user == unwrappedGame.getPrimaryUser()) */
        return gameRepository.save(updateGameMoves(unwrappedGame, move, user));
    }


    @Override
    public Game forfeitGame(long id, String username) {
        User user = userService.getUser(username);
        Optional<Game> game = gameRepository.findById(id);
        Game unwrappedGame = unwrapGame(game, id);
        if(unwrappedGame.getGameState() != GameState.ACTIVE){
            throw new GameNotActiveException(id, unwrappedGame.getGameState());
        } else if(user == unwrappedGame.getPrimaryUser()){
            unwrappedGame.setLastActionDate(LocalDate.now());
            unwrappedGame.setDateEnded(LocalDate.now());
            unwrappedGame.setGameState(GameState.SECONDARY_WIN);
        } else if(user == unwrappedGame.getSecondaryUser()){
            unwrappedGame.setLastActionDate(LocalDate.now());
            unwrappedGame.setDateEnded(LocalDate.now());
            unwrappedGame.setGameState(GameState.PRIMARY_WIN);
        }else{
            throw new UserNotPlayerException(user);
        }
        return unwrappedGame;
    } 

    @Override
    public void deleteGame(long id, String username, Collection<? extends GrantedAuthority> authorities) {

        Optional<Game> game = gameRepository.findById(id);
        Game unwrappedGame = unwrapGame(game, id);

        if(authorities.toString().contains("ADMIN")){
            gameRepository.delete(unwrappedGame);
            return;
        }

        User user = userService.getUser(username);
        
        if(user == unwrappedGame.getPrimaryUser()){
            gameRepository.delete(unwrappedGame);
        } else {
            throw new UserNotPlayerException(user);
        }
    }



    static Game unwrapGame(Optional<Game> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, Game.class);
    }

    // move format: (squareNum) + "_" +(primaryUsersTurn==true ? "1" : "2") + (isOsTurn == true ? "O" : "X")

    static Game updateGameMoves(Game game, int move, User user){
        
        String stringMove = move + "_";

        for (int i = 0; i < game.getMoves().length; i++) {
            if(game.getMoves()[i] == null){
                if(i  == 0){
                    // if this is the first move,
                    // check for who starts boolean and
                    // check which user is the authenticated user

                    if(!game.isDoesPrimaryUserStart() && game.getSecondaryUser() == user){
                        stringMove = stringMove.concat("2");
                        stringMove = stringMove.concat(!game.isPrimaryUserOs() ? "O" : "X");
                    } else if (game.isDoesPrimaryUserStart()  && game.getPrimaryUser() == user){
                        stringMove = stringMove.concat("1");
                        stringMove = stringMove.concat(game.isPrimaryUserOs() ? "O" : "X");

                    } else if (user != game.getSecondaryUser() && user != game.getPrimaryUser()){
                        throw new UserNotPlayerException(user);
                    } else {
                        throw new WrongTurnMoveException(user);
                    }
                } else {
                    // else,
                    // check previous move user and
                    // check which user is not the previous authenticated user

                    if(game.getMoves()[i-1].contains("_1") && game.getSecondaryUser() == user){
                        stringMove = stringMove.concat("2");
                        stringMove = stringMove.concat(!game.isPrimaryUserOs() ? "O" : "X");
                    } else if (game.getMoves()[i-1].contains("_2") && game.getPrimaryUser() == user){
                        stringMove = stringMove.concat("1");
                        stringMove = stringMove.concat(game.isPrimaryUserOs() ? "O" : "X");

                    } else if (user != game.getSecondaryUser() && user != game.getPrimaryUser()){
                        throw new UserNotPlayerException(user);
                    } else {
                        throw new WrongTurnMoveException(user);
                    }
                }

                // String finalMove = stringMove;

                game.getMoves()[i] = stringMove;
                game.setGameState(checkGameVictory(game.getMoves(), game.isPrimaryUserOs()));
                game.setLastActionDate(LocalDate.now());
                if(game.getGameState() != GameState.ACTIVE){
                    game.setDateEnded(LocalDate.now());
                }
                break;
            } else if(game.getMoves()[i].contains(stringMove)){
                throw new IllegalMoveException(move, user);
            }
        }

        return game;

    }

    static GameState checkGameVictory(String[] moves, boolean isPrimaryUserOs){


        //order moves from chronologically played to top left -> bottom right from left to right

        String[] orderedMoves = orderMoves(moves);

        //Check for columns
        for (int i = 0; i < 3; i++) {
            //for Os
            if (orderedMoves[i].contains("O")) {
                if (orderedMoves[i+3].contains("O")) {
                    if (orderedMoves[i+6].contains("O")) {
                        return (isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                    }
                }
            }
            //And for Xs
            else if (orderedMoves[i].contains("X")) {
                if (orderedMoves[i + 3].contains("X")) {
                    if (orderedMoves[i + 6].contains("X")) {
                        return (!isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                    }
                }
            }
        }
        //Check for rows
        for (int i = 0; i < 3; i++) {
            //for Os
            if (orderedMoves[i * 3].contains("O")) {
                if (orderedMoves[i * 3 + 1].contains("O")) {
                    if (orderedMoves[i * 3 + 2].contains("O")) {
                        return (isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                    }
                }
            }
            //And for Xs
            else if (orderedMoves[i * 3].contains("X")) {
                if (orderedMoves[i * 3 + 1].contains("X")) {
                    if (orderedMoves[i * 3 + 2].contains("X")) {
                        return (!isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                    }
                }
            }
        }
        //Check for both diagonals
        //For Os
        if (orderedMoves[0].contains("O")) {
            if (orderedMoves[4].contains("O")) {
                if (orderedMoves[8].contains("O")) {
                    return (isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                }
            }
        }
        if (orderedMoves[6].contains("O")) {
            if (orderedMoves[4].contains("O")) {
                if (orderedMoves[2].contains("O")) {
                    return (isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                }
            }
        }
         //And for Xs
        if (orderedMoves[0].contains("X")) {
            if (orderedMoves[4].contains("X")) {
                if (orderedMoves[8].contains("X")) {
                    return (!isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                }
            }
        }
        if (orderedMoves[6].contains("X")) {
            if (orderedMoves[4].contains("X")) {
                if (orderedMoves[2].contains("X")) {
                    return (!isPrimaryUserOs ? GameState.PRIMARY_WIN : GameState.SECONDARY_WIN);
                }
            }
        }
        //Check if moves are full when no victory state has been returned
        if(moves[moves.length-1] != null){
            return GameState.DRAW;
        }

        //if here, Game must still be in progress
        return GameState.ACTIVE;
    } 

    static String[] orderMoves(String[] moves){
        List<String> movesList = new ArrayList<String>(Arrays.asList(moves));
        String[] orderedMoves = new String[moves.length];
        for (int i = 0; i < orderedMoves.length; i++) {
            orderedMoves[i] = "";
            for (String move : movesList) {
                if(move != null && move.contains( (i+1) + "_" )){
                    orderedMoves[i] = move;
                }
            }
        }
        return orderedMoves;
    }


}
