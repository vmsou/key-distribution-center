package secure;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    // Engine engine = new Engine();

        for (int i = 0; i < 5; ++i)
            System.out.println(new String(KDC.genKey(16)));

    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
