package secure;

import java.util.Arrays;
import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    Engine engine = new Engine();

        UserEntity bob = engine.create("Bob");
        engine.setUser(bob);

        byte[] test = {44, 16};

        System.out.println(new String(test));
        System.out.println(bob.getMasterKey());
        System.out.println(Arrays.toString(bob.getMasterKey().toBytes()));

    }
    public static int genNonce() {
        return new Random().nextInt();
    }
}
