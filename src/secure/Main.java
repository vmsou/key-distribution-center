package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        // UserEntity bob = engine.getUser(0);
        // UserEntity alice = engine.getUser(1);

        UserEntity bob = engine.create("Bob");
        UserEntity alice = engine.create("Alice");
        engine.setUser(bob);

        engine.send(alice.getId(), "TESTE");

        alice.showMessages();

        // engine.close();
    }
    public static int genNonce() {
        return new Random().nextInt();
    }
}
