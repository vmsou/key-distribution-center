package secure;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
	    Engine engine = new Engine();

        System.out.println(engine.user);

    }
    public static int genNounce() {
        return new Random().nextInt();
    }
}
