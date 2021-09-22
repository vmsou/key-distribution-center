package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        UserEntity bob =  engine.create("bob");
        engine.save(bob, "data/data.json");

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
