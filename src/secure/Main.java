package secure;

import java.util.Random;

public class Main {
    public static void send(Entity a, Entity b, String message) {

    }

    public static void main(String[] args) {
	    KDC kdc = new KDC();
	    UserEntity alice = kdc.createUser("alice");
        UserEntity bob = kdc.createUser("bob");

        try {
            // Bob sends message to KDC with id, AES(id), AES(receiver), AES(message)
            ProofMessage msg = bob.send(alice, "Teste");
            System.out.println("Bob enviou mensagem para KDC");

            // KDC sends to Bob his session key and alice's session key
            SessionMessage sessionMessage = kdc.receive(msg);
            System.out.println("KDC enviou as chaves de sessões para bob");

            if (sessionMessage != null) {   // Proof was correct
                System.out.println("Bob comprovou que tinha a chave mestre");
                // Bob receive his sessionKey and sends alice's sessionKey
                SessionKey bobSessionKey = bob.receive(sessionMessage);
                SessionKey aliceSessionKey = alice.receive(sessionMessage);
                System.out.println("bob recebe sua chave de sessão e envia para alice");

                // Alice sends nonce to bob
                NonceMessage nonceMessage = alice.send(genNounce(), aliceSessionKey);
                System.out.println("Alice manda nonce para bob");

                // Bob receives and updates nonce number
                NonceMessage bobNonce = bob.receive(nonceMessage, bobSessionKey);
                System.out.println("Bob atualiza o nonce");

                // Alice updates her nonce and compares with bob nonce
                System.out.println("Alice esta verificando o nonce");
                if (alice.verifyNonce(nonceMessage, bobNonce, aliceSessionKey)) {
                    System.out.println("Nonce correto.");
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
