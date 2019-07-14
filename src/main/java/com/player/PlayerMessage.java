package com.player;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// currently represents a message sent from the client to the server
// TODO: use CommandEvents instead

@Deprecated
public class PlayerMessage {
    private int x;
    private int y;
    Logger log = LoggerFactory.getLogger(this.getClass());

    public PlayerMessage(){

    }

    public PlayerMessage(int x){
        this.x = x;
    }

    public PlayerMessage(JSONObject cords) throws JSONException {
        this.x = cords.getInt("x");
        this.y =  cords.getInt("y");
        log.info(String.format("X: %d, Y: %d", this.x, this.y));
    }

    public int updateX(int x){
        this.x = x;
        return this.x;
    }

    public int updateY(int y){
        this.y = y;
        return this.y;
    }


    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

}