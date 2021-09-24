package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = true;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        Lambda[] lambdas = {
                new Lambda() { public int perform(int x) { return x + 3; }},
                new Lambda() { public int perform(int x) { return x * 3; }}
        };

        UserEntity bob = engine.getUser(1);
	    UserEntity alice = engine.getUser(2);

	    engine.user.setLambda(alice.getId(), lambdas[0]);
	    alice.setLambda(bob.getId(), lambdas[1]);

	    engine.send(alice.getId(), "MESSAGE");

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
