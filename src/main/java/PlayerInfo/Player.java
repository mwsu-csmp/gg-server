package PlayerInfo;

public class Player  {
    private int x;
   // private int y;

    public Player(){

    }

    public Player(int x, int option){
        this.x = x;

   //     this.y = y;


        setX(option);
    }

    public int updateX(int x){
        this.x = x;
        return this.x;
    }

    public int getX(){
        return x;
    }
    /*public int getY(){
        return y;
    }*/

    // if option=1 player moves right, if -1 player moves left
    public void setX(int option){
        if (option == 1){
            x =+ 40;
        }
        if (option == -1){
            x =- 40;
        }
    }

}