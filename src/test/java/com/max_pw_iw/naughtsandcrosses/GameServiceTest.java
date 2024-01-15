package com.max_pw_iw.naughtsandcrosses;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
// import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.max_pw_iw.naughtsandcrosses.entity.Game;
import com.max_pw_iw.naughtsandcrosses.entity.GameState;
import com.max_pw_iw.naughtsandcrosses.entity.Role;
import com.max_pw_iw.naughtsandcrosses.entity.User;
import com.max_pw_iw.naughtsandcrosses.exception.GameNotActiveException;
import com.max_pw_iw.naughtsandcrosses.exception.IllegalUserJoinException;
import com.max_pw_iw.naughtsandcrosses.exception.UserNotPlayerException;
import com.max_pw_iw.naughtsandcrosses.repository.GameRepository;
import com.max_pw_iw.naughtsandcrosses.repository.UserRepository;
import com.max_pw_iw.naughtsandcrosses.service.GameServiceImpl;
import com.max_pw_iw.naughtsandcrosses.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    Role playerRole = new Role("PLAYER");
    Role adminRole = new Role("ADMIN");

    @Test
    public void CreateGameTest(){
        User user = new User("bingus", "bingus_password");
        Game game = new Game(true, true);
        when(userService.getUser("bingus")).thenReturn(user);
        when(gameRepository.save(game)).thenReturn(game);

        game.setPrimaryUser(user);

        Game result = gameService.createGame(game, "bingus");

        assertTrue(result.getGameState() == GameState.INITIALIZED);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void JoinGameValidTest(){
        User userPrimary = new User("bingus", "bingus_password");
        User userSecondary = new User("bumble", "bumble_password");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(userService.getUser("bumble")).thenReturn(userSecondary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        Game result = gameService.joinGame(game.getId(), "bumble");

        assertTrue(result.getSecondaryUser() == userSecondary);
        assertTrue(result.getGameState() == GameState.ACTIVE);
        verify(gameRepository, times(2)).save(game);
    }

    @Test
    public void InvalidJoinOwnGameTest(){
        User userPrimary = new User("bingus", "bingus_password");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        assertThrows(IllegalUserJoinException.class, () ->{
            gameService.joinGame(game.getId(), "bingus");
        });

        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void InvalidJoinActiveGameTest(){
        User userPrimary = new User("wingus", "wingus_password");
        User userSecondary = new User("dingus", "dingus_password");

        User userAtFault = new User("MrNaughty", "naughty123");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("wingus")).thenReturn(userPrimary);
        when(userService.getUser("dingus")).thenReturn(userSecondary);
        when(userService.getUser("MrNaughty")).thenReturn(userAtFault);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "wingus");

        Game result = gameService.joinGame(game.getId(), "dingus");

        assertThrows(IllegalUserJoinException.class, () ->{
            gameService.joinGame(game.getId(), "MrNaughty");
        });

        assertTrue(result.getSecondaryUser() == userSecondary);
        verify(gameRepository, times(2)).save(game);
    }

    @Test
    public void PrimaryUserForfeitGameValidTest(){
        User userPrimary = new User("bingus", "bingus_password");
        User userSecondary = new User("bumble", "bumble_password");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(userService.getUser("bumble")).thenReturn(userSecondary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        gameService.joinGame(game.getId(), "bumble");

        Game result = gameService.forfeitGame(game.getId(), "bingus");

        // assertTrue(result.getSecondaryUser() == userSecondary);
        assertTrue(result.getGameState() == GameState.SECONDARY_WIN);
    }

    @Test
    public void SecondaryUserForfeitGameValidTest(){
        User userPrimary = new User("bingus", "bingus_password");
        User userSecondary = new User("bumble", "bumble_password");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(userService.getUser("bumble")).thenReturn(userSecondary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        gameService.joinGame(game.getId(), "bumble");

        Game result = gameService.forfeitGame(game.getId(), "bumble");

        assertTrue(result.getGameState() == GameState.PRIMARY_WIN);
    }

    @Test
    public void InvalidUserForfeitGameTest(){
        User userPrimary = new User("wingus", "wingus_password");
        User userSecondary = new User("dingus", "dingus_password");

        User userAtFault = new User("MrNaughty", "naughty123");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("wingus")).thenReturn(userPrimary);
        when(userService.getUser("dingus")).thenReturn(userSecondary);
        when(userService.getUser("MrNaughty")).thenReturn(userAtFault);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "wingus");

        gameService.joinGame(game.getId(), "dingus");

        assertThrows(UserNotPlayerException.class, () ->{
            gameService.forfeitGame(game.getId(), "MrNaughty");
        });
    }

    @Test
    public void InvalidForfeitInactiveGameTest(){
        User userPrimary = new User("bingus", "bingus_password");
        // User userSecondary = new User("bumble", "bumble_password");

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        // when(userService.getUser("bumble")).thenReturn(userSecondary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        assertThrows(GameNotActiveException.class, () ->{
            gameService.forfeitGame(game.getId(), "bingus");
        });
    }

    @Test
    public void DeleteGameValidTest(){

        User userPrimary = new User("bingus", "bingus_password");
        userPrimary.addRole(playerRole);

        Collection<? extends GrantedAuthority> authorities = GenerateUserRolesAuthorities(userPrimary);

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        gameService.deleteGame(1, "bingus", authorities);

        verify(gameRepository, times(1)).delete(game);
    }

    @Test
    public void DeleteGameAsAdminValidTest(){

        User userPrimary = new User("bingus", "bingus_password");
        userPrimary.addRole(playerRole);

        User userAdmin = new User("BigBoss", "BigBossisme");
        userAdmin.addRole(adminRole);

        Collection<? extends GrantedAuthority> adminAuthorities = GenerateUserRolesAuthorities(userAdmin);

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        gameService.deleteGame(1, "BigBoss", adminAuthorities);

        verify(gameRepository, times(1)).delete(game);
    }

    @Test
    public void InvalidDeleteGameAsOtherUserTest(){

        User userPrimary = new User("bingus", "bingus_password");
        userPrimary.addRole(playerRole);

        User userSecondary = new User("BigBoss", "BigBossisme");
        userSecondary.addRole(playerRole);

        Collection<? extends GrantedAuthority> authorities = GenerateUserRolesAuthorities(userSecondary);

        Game game = new Game(true, true);
        game.setPrimaryUser(userPrimary);
        game.setId(1);

        Optional<Game> optGame = Optional.of(game);

        when(userService.getUser("bingus")).thenReturn(userPrimary);
        when(userService.getUser("BigBoss")).thenReturn(userSecondary);
        when(gameRepository.findById(game.getId())).thenReturn(optGame);
        when(gameRepository.save(game)).thenReturn(game);

        gameService.createGame(game, "bingus");

        assertThrows(UserNotPlayerException.class, () ->{
            gameService.deleteGame(1, "BigBoss", authorities);
        });
    }



    private Collection<? extends GrantedAuthority> GenerateUserRolesAuthorities(User user){
        
        Object[] roleArray = user.getRoles().toArray();

        String[] rolesStringArr = new String[roleArray.length];


        for(int i = 0; i < roleArray.length; i++) {
            rolesStringArr[i] = roleArray[i].toString();
        }

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(rolesStringArr)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return authorities;
    }

}
