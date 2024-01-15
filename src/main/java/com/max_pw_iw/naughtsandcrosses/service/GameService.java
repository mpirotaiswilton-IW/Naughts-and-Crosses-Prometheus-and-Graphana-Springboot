package com.max_pw_iw.naughtsandcrosses.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.max_pw_iw.naughtsandcrosses.entity.Game;

public interface GameService {
    Game getGame(long id);
    List<Game> getAllGames();
    Game createGame(Game game, String username);
    Game joinGame(long id, String username);
    Game addMove(long id, int move, String username);
    Game forfeitGame(long id, String username);
    void deleteGame(long id, String username, Collection<? extends GrantedAuthority> authorities);
}
