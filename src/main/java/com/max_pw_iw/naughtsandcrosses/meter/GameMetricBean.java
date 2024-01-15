package com.max_pw_iw.naughtsandcrosses.meter;

import java.util.function.Supplier;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.entity.GameState;
import com.max_pw_iw.naughtsandcrosses.repository.GameRepository;
// import com.max_pw_iw.naughtsandcrosses.service.GameService;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class GameMetricBean {
    @Lazy
    @Autowired
    protected GameRepository gameRepository;


    public Supplier<Number> fetchGameCount() {
        return ()->((List<Game>) gameRepository.findAll()).size();
    }

    public Supplier<Number> fetchGameCountByState(GameState gameState) {
        return ()->{
            return ((List<Game>) gameRepository.findAllByGameState(gameState)).size();
        
        
        };
    }

    public GameMetricBean(MeterRegistry registry){
        Gauge.builder("number.of.games",fetchGameCount())
        .tag("type","total")        
        .register(registry);

        for (int i = 0; i < GameState.values().length; i++) {
            Gauge.builder(("number.of.games"),
                            fetchGameCountByState(GameState.values()[i]))
            .tag("type", GameState.values()[i].toString().toLowerCase())        
            .register(registry);
        }
    }
}
