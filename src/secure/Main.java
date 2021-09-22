package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        UserEntity bob = engine.create("Bob");
        UserEntity alice = engine.create("Alice");
        engine.setUser(bob);

        engine.send(alice.getId(), "MENSAGEM TESTE");

        engine.save(engine.global.messages, "data/messages.json");

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
