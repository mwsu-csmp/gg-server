package hello;

public class Player  {
    private int x;

    public Player(){

    }

    public Player(int x){
        this.x = x;

        //TODO: ENABLE setX();
    }

    public int updateX(int x){
        this.x = x;
        return this.x;
    }

    public int getX(){
        return x;
    }

    public void setX(){
        x += 40;
    }
}