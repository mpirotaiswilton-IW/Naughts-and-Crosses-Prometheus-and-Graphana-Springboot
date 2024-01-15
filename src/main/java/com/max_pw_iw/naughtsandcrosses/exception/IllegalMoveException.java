package com.max_pw_iw.naughtsandcrosses.exception;

import com.max_pw_iw.naughtsandcrosses.entity.User;

public class IllegalMoveException extends RuntimeException{
    public IllegalMoveException(int move, User user) {
        super("The attempted move (square " + move + " by user " + user.getUsername() + ") is not possible");
    }
}
