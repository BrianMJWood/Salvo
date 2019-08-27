package com.codeoftheweb.salvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;



import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository repo;

    @Autowired
    private PlayerRepository prepo;

    @Autowired
    private GamePlayerRepository gprepo;

    @Autowired
    private ShipRepository shipRepository;


    @RequestMapping(value = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestBody Player player) {
        if (player.getUsername().isEmpty() || player.getPassword().isEmpty()) {

            return new ResponseEntity<>("Missing data. Please ensure both fields are filled out.",
                    HttpStatus.FORBIDDEN);
        }
        if (prepo.findByUsername(player.getUsername()) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        prepo.save(new Player(player.getUsername(), passwordEncoder.encode(player.getPassword())));
        return new ResponseEntity<>("User created! Please log in.",HttpStatus.CREATED);
    }



    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication) {
        Player currentUser = findPlayerFromAuth(authentication);
        if (currentUser == null) {
            return new ResponseEntity<>(makeMap("error", "Please create an account and log in to make a new game."),
                    HttpStatus.CONFLICT);
        } else {
            Game game = new Game();
            GamePlayer gamePlayer = new GamePlayer(currentUser, game);
            repo.save(game);
            gprepo.save(gamePlayer);

        return new ResponseEntity<>(makeMap("Game created!", gamePlayer.getId()),HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@PathVariable long gameId, Authentication authentication) {

        Player currentUser = findPlayerFromAuth(authentication);
        Game game = repo.getOne(gameId);
        GamePlayer gamePlayer = new GamePlayer(currentUser, game);

        if (currentUser == null) {
            return new ResponseEntity<>(makeMap("error", "Please log in to join a game."), HttpStatus.UNAUTHORIZED);
        }
        if (repo.getOne(gameId) == null) {
            return new ResponseEntity<>(makeMap("error", "Game does not exist. Do you?"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() > 1) {
            return new ResponseEntity<>(makeMap("error", "No room!"), HttpStatus.FORBIDDEN);
        }
        gprepo.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gameId", gamePlayer.getId()), HttpStatus.OK);
        }

    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Object> receiveShips(
            @PathVariable long gamePlayerId, Authentication authentication,
            @RequestBody Set<Ship> ships
    ) {
        Player currentUser = findPlayerFromAuth(authentication);
        GamePlayer gamePlayer = gprepo.getOne(gamePlayerId);

            if (Objects.isNull(gamePlayerId)) {
                return new ResponseEntity<>(makeMap("error", "GamePlayer Not Found"), HttpStatus.CONFLICT);
            }
            if (currentUser == null) {
            return new ResponseEntity<>(makeMap("error", "Please log in to place ships."),
                        HttpStatus.UNAUTHORIZED);
            }
            if (!gamePlayer.getPlayer().equals(currentUser) ){
                return new ResponseEntity<>(makeMap("error", "You are not authorized to place ships in this game."),
                        HttpStatus.FORBIDDEN);
            }
            if ((gamePlayer.getShips().size() > 0) && (gamePlayer.getShips().size() > 6)) {
                return new ResponseEntity<>(makeMap("error", "Ships already placed."),
                        HttpStatus.FORBIDDEN);
            }
            ships.forEach(ship -> {
                gamePlayer.addShip(ship);
                shipRepository.save(ship);
            });

        return new ResponseEntity<>(makeMap("success:", "ships placed"), HttpStatus.CREATED);
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gamePlayerInfo(@PathVariable("gamePlayerId") GamePlayer gamePlayer, Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (gamePlayer == null) {
            return new ResponseEntity<>(makeMap("error", "GamePlayer Not Found"), HttpStatus.CONFLICT);
        }else if(findPlayerFromAuth(authentication).getId() != gamePlayer.getPlayer().getId()) {
            return new ResponseEntity<>(makeMap("error", "Forbidden. Stop cheating."), HttpStatus.UNAUTHORIZED);
        }
        else {
            dto.put("id", gamePlayer.getId());
            dto.put("player", playerDTO(gamePlayer.getPlayer()));
            dto.put("created", gamePlayer.getGame().getDate());
            dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                    .stream()
                    .map(gamePlayer1 -> gamePlayerDTO(gamePlayer1))
                    .collect(Collectors.toList()));
            dto.put("ships", gamePlayer.getShips()
                    .stream()
                    .map(ship -> shipDTO(ship))
                    .collect(Collectors.toList()));

            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }
    @RequestMapping("/games")
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (!isGuest(authentication)) {
            Player player = prepo.findByUsername(authentication.getName());
                dto.put("player", playerDTO(player));
            } else {
                dto.put("player", "guest");
            }
            dto.put("games", repo.findAll()
                .stream()
                .map(game -> gameDTO(game))
                .collect(Collectors.toList()));

return dto;
    }
    @RequestMapping("/players")
    public List<Map> getAllPlayers() {
        return prepo
                .findAll()
                .stream()
                .map(player -> playerDTO(player))
                .collect(Collectors.toList());
    }

        @RequestMapping("/gamePlayers")
    public List<GamePlayer> getAllGp() {
        return gprepo.findAll();
    }

    public Map<String, Object> gameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getDate());
        dto.put("gamePlayers", gamePlayerList(game.getGamePlayers()));
        dto.put("scores", game
                        .getScore()
                        .stream()
                        .map(score -> scoreDTO(score))
                        .collect(Collectors.toList()));
        return dto;
    }

    public List<Map> gamePlayerList(Set<GamePlayer> gameplayers) {
        return gameplayers
                .stream()
                .map(gp -> gamePlayerDTO(gp))
                .collect(Collectors.toList());
    }

    public Map<String, Object> gamePlayerDTO(GamePlayer gameplayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gameplayer.getId());
        dto.put("created", gameplayer.getDate());
        dto.put("player", playerDTO(gameplayer.getPlayer()));
        dto.put(
                "salvos",
                gameplayer
                        .getSalvos()
                        .stream()
                        .map(salvo -> salvoDTO(salvo))
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private Map<String, Object> shipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    public Map<String, Object> playerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("username", player.getUsername());
        dto.put("scores", player
                .getScore()
                .stream()
                .map(score -> scoreDTO(score))
                .collect(Collectors.toList()));

        return dto;
    }
    private Map<String, Object> salvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("locations", salvo.getLocations());
        return dto;
    }
    private Map<String, Object> scoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("score", score.getScore());
        dto.put("finishDate", score.getFinishDate());
        dto.put("player",  score.getPlayer().getUsername());
        return dto;
    }
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    public Player findPlayerFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else {
            Player player = prepo.findByUsername(authentication.getName());
            return player;
        }
    }
}
