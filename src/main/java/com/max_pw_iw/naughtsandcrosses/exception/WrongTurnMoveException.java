package com.max_pw_iw.naughtsandcrosses.exception;

import com.max_pw_iw.naughtsandcrosses.entity.User;

public class WrongTurnMoveException extends RuntimeException {

    public WrongTurnMoveException(User user) {
        super("the user " + user.getUsername() + " tried to make a move when it was not their turn");
    }
    
}
