package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = true;

    public static void main(String[] args) {
	    Engine engine = new Engine();

	    Lambda l1 = new Lambda() { public int perform(int x) { return x + 3; }};
        Lambda l2 = new Lambda() { public int perform(int x) { return x * 3; }};

	    UserEntity alice = engine.getUser(2);

	    engine.user.setLambda(l1);
	    alice.setLambda(l1);

	    engine.send(alice.getId(), "MESSAGE");

        engine.close();

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
