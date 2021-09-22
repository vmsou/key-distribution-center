package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        engine.create("0");
        engine.create("1");

        engine.save(engine.global.users, "data/users.json");

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
