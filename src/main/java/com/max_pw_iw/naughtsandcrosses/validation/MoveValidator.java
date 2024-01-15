package com.max_pw_iw.naughtsandcrosses.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class MoveValidator implements ConstraintValidator<Move,Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        } else if(value > 0 && value < 10) {
            return true;
        }
        return false;
    }
    
}
