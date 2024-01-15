package com.max_pw_iw.naughtsandcrosses.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.entity.GameState;



public interface GameRepository extends CrudRepository<Game, Long> {
    Optional<Game> findById(long id);
    Iterable<Game> findAllByGameState(GameState gameState);
}
