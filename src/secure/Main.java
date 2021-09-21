package secure;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
	    Engine engine = new Engine();

        UserEntity user = engine.create("Bob");
        engine.create("Alice");
        engine.setUser(user);

        for (UserEntity u : engine.global.users.values())
            System.out.println(u);
        // engine.send(2, "TESTE");

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }
}
