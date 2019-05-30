package PlayerInfo;

public class Player  {
    private int x;
    private int y;

    public Player(){

    }

    public Player(int x){
        this.x = x;
        this.y = y;
        //TODO: ENABLE setX();
    }

    public int updateX(int x){
        this.x = x;
        return this.x;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}