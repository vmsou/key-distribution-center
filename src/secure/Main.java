package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

	    UserEntity alice = engine.getUser(2);

	    engine.send(alice.getId(), "MENSAGEM");

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
