package secure;

import org.json.JSONObject;

import java.util.Random;

public class Main {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
	    // Engine engine = new Engine();

        // engine.close();
    }
    public static int genNonce() {
        return new Random().nextInt();
    }

}
