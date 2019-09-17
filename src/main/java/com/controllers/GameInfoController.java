package com.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.missouriwestern.csmp.gg.base.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GameInfoController {
    Logger log = LoggerFactory.getLogger(GameInfoController.class);


    private Gson gson;

    @Autowired
    private GameMapping game;

    public GameInfoController() {
        var builder = new GsonBuilder();
        gson = builder.create();
    }

    /** displays a JSON description of a board */
    @GetMapping("/board/{boardId}")
    @ResponseBody
    public String getBoard(@PathVariable String boardId) {
        var board = game.getBoard(boardId);
        return board != null ? board.toString() : "ERROR"; // TODO: error more gracefully
    }

    /** displays a JSON description of the specified entity */
    @GetMapping("/entity/{entityId}")
    @ResponseBody
    public String getEntityDescription(@PathVariable int entityId) {
        var entity = game.getEntity(entityId);
        return entity != null ? entity.toString() : "ERROR"; // TODO: error more gracefully
    }

    /** displays a JSON description of the specified tile */
    @GetMapping("/tile/{boardId}/{column}/{row}")
    @ResponseBody
    public String getTileDescription(@PathVariable String boardId,
                                     @PathVariable int column,
                                     @PathVariable int row) {
        var board = game.getBoard(boardId);
        var contents = new ArrayList<Integer>(); // contained entity ID's
        if(board != null) {
            var tile = board.getTile(column, row);
            if(tile != null) { // add all entity ID's to the list of ID's
                return tile.toString();
            }
        }
        return "ERROR";
    }

    /** displays a JSON list of id's of contained entities */
    @GetMapping("/container/tile/{boardId}/{column}/{row}")
    @ResponseBody
    public String getTileContents(@PathVariable String boardId,
                                  @PathVariable int column,
                                  @PathVariable int row) {
        var board = game.getBoard(boardId);
        var contents = new ArrayList<Integer>(); // contained entity ID's
        if(board != null) {
            var tile = board.getTile(column, row);
            if(tile != null) { // add all entity ID's to the list of ID's
                tile.getEntities().forEach(entity -> contents.add(entity.getID()));
            }
        }
        return gson.toJson(contents);
    }

    /** returns a list of all entity id's held by tiles on the board */
    @GetMapping("/container/board/{boardId}")
    @ResponseBody
    public String getBoardContents(@PathVariable String boardId) {
        return gson.toJson(game.getBoard(boardId).getTileStream()
                .flatMap(tile -> tile.getEntities())
                .map(entity -> entity.getID())
                .collect(Collectors.toList()));
    }

    /** displays a JSON list of id's of contained entities */
    @GetMapping("/container/entity/{entityId}")
    @ResponseBody
    public String getEntityContents(@PathVariable int entityId) {
        var entity = game.getEntity(entityId);
        var contents = new ArrayList<Integer>(); // contained entity ID's
        if(entity != null && entity instanceof Container) {// add all entity ID's to the list of ID's
            var container = (Container)entity;
            container.getEntities().forEach(ent ->  contents.add(ent.getID()));
        }
        return gson.toJson(contents);
    }

    /** displays a JSON list of id's of contained entities */
    @GetMapping("/container/player/{playerId}")
    @ResponseBody
    public String getPlayerContents(@PathVariable String playerId) {
        var player = game.getPlayer(playerId);
        var contents = new ArrayList<Integer>(); // contained entity ID's
        if(player != null) {// add all entity ID's to the list of ID's
            player.getEntities().forEach(ent ->  contents.add(ent.getID()));
        }
        return gson.toJson(contents);
    }

    /** returns description of player's avatar entity */
    @GetMapping("/player-avatar/{playerId}")
    @ResponseBody
    public String getPlayerAvatar(@PathVariable String playerId) {
        var player = game.getPlayer(playerId);
        if(player != null && player instanceof StompClient) {
            return ((StompClient)player).getAvatar().toString();
        }
        return "ERROR";
    }


    @GetMapping("/game/client")
    public String loadClientGUI(Map<String, Object> model,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        var username = request.getUserPrincipal().getName();
        model.put("username", username);

        return "gameclient.html";
    }

}
