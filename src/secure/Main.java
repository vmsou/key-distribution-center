package secure;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
	    Engine engine = new Engine();

        // engine.create("Bob");
        // engine.create("Alice");

        for (UserEntity u : engine.global.users.values())
            System.out.println(u);

        engine.save(engine.global.users, "data/users.csv");

    }
    public static int genNonce() {
        return new Random().nextInt();
    }
}
