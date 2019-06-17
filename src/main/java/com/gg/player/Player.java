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
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Player(String x, String y){
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
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