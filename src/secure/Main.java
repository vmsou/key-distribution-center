package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

	    engine.create("Bob");
	    engine.create("Alice");

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
