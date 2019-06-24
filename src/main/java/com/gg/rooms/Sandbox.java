package com.gg.rooms;


public class Sandbox{

    private String room;

    public Sandbox(){
        setRoom();
    }

    public Sandbox(int x){
        setRoom();
    }
    public Sandbox(String room){
        setRoom();
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(){
        String tiles = "0,0,40,0,80,0,120,0,160,0," +
                       "0,40,40,40,80,40,120,40,160,40," +
                       "0,80,40,80,80,80,120,80,160,80," +
                       "0,120,40,120,80,120,120,120,160,120," +
                       "0,160,40,160,80,160,120,160,160,160";

        room = tiles;
    }

}
