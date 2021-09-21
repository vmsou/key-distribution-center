package secure;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
	    KDC kdc = new KDC();
	    UserEntity alice = kdc.createUser("alice");
        UserEntity bob = kdc.createUser("bob");

        try {
            // Bob sends message to KDC with id, AES(id), AES(receiver), AES(message)
            ProofMessage msg = bob.send(alice, "Teste");

            // KDC sends to Bob his session key and alice's session key
            SessionMessage sessionMessage = kdc.receive(msg);

            if (sessionMessage != null) {   // Proof was correct
                // Bob receive his sessionKey and sends alice's sessionKey
                SessionKey bobSessionKey = bob.receive(sessionMessage);
                SessionKey aliceSessionKey = alice.receive(sessionMessage);

                // Alice sends nonce to bob
                NonceMessage nonceMessage = alice.send(genNounce(), aliceSessionKey);

                // Bob receives and updates nonce number
                NonceMessage bobNonce = bob.receive(nonceMessage, bobSessionKey);

                // Alice updates her nonce and compares with bob nonce
                if (alice.verifyNonce(nonceMessage, bobNonce, aliceSessionKey)) {
                    System.out.println(new String(msg.getMessage()));
                } else {
                    System.out.println("Nonce falhou.");
                }
            } else
                System.out.println("Prova de identidade falhou");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro. " + e.getMessage());
        }

    }
    public static int genNounce() {
        return new Random().nextInt();
    }
}
