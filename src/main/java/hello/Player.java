package hello;

public class Player  {
    private String x;

    public Player(){
        setX();
    }

    public Player(String x){
        this.x = x;
        setX();
    }

    public String getX(){
        return x;
    }

    public void setX(){
        int temp = Integer.parseInt(x);
        temp += 40;
        x = Integer.toString(temp);
    }
}