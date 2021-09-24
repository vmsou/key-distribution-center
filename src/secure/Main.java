package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = true;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        Lambda[] lambdas = {
                x -> x + 3,
                x -> x * 3
        };

        UserEntity bob = engine.user;
	    UserEntity alice = engine.getUser(2);

	    engine.user.setLambda(alice.getId(), lambdas[0]);
	    alice.setLambda(bob.getId(), lambdas[0]);

	    engine.send(alice.getId(), "MESSAGE");

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
