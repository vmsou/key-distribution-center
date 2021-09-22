package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        for (var msg : engine.global.messages.values())
            System.out.println(msg.toJSON().toString(1));

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
