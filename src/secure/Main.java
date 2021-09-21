package secure;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
	    KDC kdc = new KDC();
	    UserEntity alice = kdc.createUser("alice");
        UserEntity bob = kdc.createUser("bob");

        kdc.send(bob, alice, "Teste 123");
    }
    public static int genNounce() {
        return new Random().nextInt();
    }
}
