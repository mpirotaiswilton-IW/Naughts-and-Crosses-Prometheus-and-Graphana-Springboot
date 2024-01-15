package com.max_pw_iw.naughtsandcrosses.exception;

import com.max_pw_iw.naughtsandcrosses.entity.GameState;

public class GameNotActiveException extends RuntimeException{
    public GameNotActiveException(Long id, GameState state){
        super("The game of id "+ id +" is not in an Active state: " + state.toString());
    }    
}
