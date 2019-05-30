package KeyboardInput;

public class keyDistribute {

    public static void mainParse(Keyboard a){
        //convert from ascii to char
        //for now just a temp
        String key=a.getKey();

        switch (key){
            case "a":
            case "s":
            case "d":
            case "w":
                movementAccess(key);
                break;
            case " ":
                enterAccess(key);
            default:

        }

    }

    public static void movementAccess(String key){

    }

    public static void enterAccess(String key){

    }

}
