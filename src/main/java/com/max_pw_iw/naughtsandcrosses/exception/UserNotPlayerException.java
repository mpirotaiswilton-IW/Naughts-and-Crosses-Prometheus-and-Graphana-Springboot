package com.max_pw_iw.naughtsandcrosses.exception;

import com.max_pw_iw.naughtsandcrosses.entity.User;

public class UserNotPlayerException extends RuntimeException {
        public UserNotPlayerException(User user) {
            super("User " + user.getUsername() + " attempted an action in a game they are not a part of");
        }
}
