package com.gg.player;

public class Player  {
    private int x;
    private int y;

    public Player(){

    }

    public Player(int x){
        this.x = x;
    }

    public Player(String x){
        this.x = Integer.parseInt(x);
    }

    public int updateX(int x){
        this.x = x;
        return this.x;
    }


    public int getX(){
        return x;
    }




}