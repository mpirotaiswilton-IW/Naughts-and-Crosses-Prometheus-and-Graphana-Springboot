package com.max_pw_iw.naughtsandcrosses.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class GameRequest {
    
    // @NonNull
    // @NotNull(message = "opponent humanity must be decided")
    // private Boolean isOpponentHuman;

    @NonNull
    @NotNull(message = "player order must be defined")
    private Boolean doesPrimaryUserStart;

    @NonNull
    @NotNull(message = "player identity must be defined")
    private Boolean isPrimaryUserOs;

}
