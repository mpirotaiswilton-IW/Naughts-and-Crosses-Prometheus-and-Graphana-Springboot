package com.max_pw_iw.naughtsandcrosses.exception;

public class UserDeletingSelfException extends RuntimeException{
    public UserDeletingSelfException(Long id){
        super("User of id: " + id + " has tried to delete themselves on this endpoint");
    }    
}
