package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        UserEntity bob = engine.create("bob");
        UserEntity alice = engine.create("alice");
        engine.setUser(bob);

        engine.send(alice.getId(), "MENSAGEM");

        alice.showMessages();
        System.out.println();

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }
}
