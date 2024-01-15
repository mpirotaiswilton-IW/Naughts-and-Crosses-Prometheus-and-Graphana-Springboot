package com.max_pw_iw.naughtsandcrosses.exception;

import com.max_pw_iw.naughtsandcrosses.entity.User;

public class IllegalUserJoinException extends RuntimeException {
        public IllegalUserJoinException(User user) {
            super("User " + user.getUsername() + " attempted joining a game they created");
        }
    
}
