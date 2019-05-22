package hello;

public class Client {
    private int x;
    private int y;

    public Client(){
        x = 80;
        y = 80;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int updateX(int dx){
        x += dx * 40;
        return x;
    }

    public int updateY(int dy){
        y += dy * 40;
        return y;
    }
}
