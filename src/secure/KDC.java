package secure;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

public class KDC extends Entity {
    private HashMap<Integer, MasterKey> masterKeys; // DON'T EXPOSE
    private SessionKey sessionKey;

    // Constructors
    public KDC() {
        super("KDC");
        setMasterKeys(new HashMap<>());
        setSessionKey(new SessionKey(genKey(32)));
    }

    // Methods
    public void send(UserEntity bob, UserEntity alice, String message) {
        try {
            // Bob sends message to KDC with id, AES(id), AES(receiver), AES(message)
            ProofMessage msg = bob.send(alice, "Teste 123");
            System.out.println("Bob enviou mensagem para KDC");

            // KDC sends to Bob his session key and alice's session key
            SessionMessage sessionMessage = receive(msg);   // SessionKey refreshed
            System.out.println("KDC enviou as chaves de sessões para Bob");

            if (sessionMessage != null) {   // Proof was correct
                System.out.println("Bob comprovou que tinha a chave mestre");
                // Bob receive his sessionKey and sends alice's sessionKey
                SessionKey bobSessionKey = bob.receive(sessionMessage);
                SessionKey aliceSessionKey = alice.receive(sessionMessage);
                System.out.println("Bob recebe sua chave de sessão e envia para alice");

                // Alice sends nonce to bob
                NonceMessage nonceMessage = alice.send(Main.genNounce(), aliceSessionKey);
                System.out.println("Alice manda nonce para bob");

                // Bob receives and updates nonce number
                NonceMessage bobNonce = bob.receive(nonceMessage, bobSessionKey);
                System.out.println("Bob atualiza o nonce");

                // Alice updates her nonce and compares with bob nonce
                System.out.println("Alice esta verificando o nonce");
                if (alice.verifyNonce(nonceMessage, bobNonce, aliceSessionKey)) {
                    System.out.println("Nonce correto.");
                    System.out.println(new String(AES.decrypt(sessionMessage.getMessage(), aliceSessionKey)));
                } else {
                    System.out.println("Nonce falhou.");
                }
            } else
                System.out.println("Prova de identidade falhou");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro. " + e.getMessage());
        }
    }

    public SessionMessage receive(ProofMessage proofMessage) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int sender = proofMessage.getSender();
        MasterKey senderKey = getMaster(sender);

        byte[] decryptedReceiver = AES.decrypt(proofMessage.getReceiver(), senderKey);
        int receiver = ByteBuffer.wrap(decryptedReceiver).getInt();
        MasterKey receiverKey = getMaster(receiver);

        byte[] decryptedProof = AES.decrypt(proofMessage.getProof(), senderKey);
        int proof = ByteBuffer.wrap(decryptedProof).getInt();

        // Verify proof
        if (proof == sender) {
            // Update message with session key encryption
            byte[] message = proofMessage.getMessage();
            byte[] decryptedMessage = AES.decrypt(message, senderKey);
            byte[] encryptedMessage = AES.encrypt(decryptedMessage, sessionKey);
            proofMessage.setMessage(encryptedMessage);

            // sessionKey encrypted with sender keys
            Message session1 = new Message(
                    getId(),
                    AES.encrypt(sessionKey, senderKey)
            );

            // sessionKey encrypted with receiver keys
            Message session2 = new Message(
                    getId(),
                    AES.encrypt(sessionKey, receiverKey)
            );
            resetSessionKey();
            return new SessionMessage(proofMessage, session1, session2);
        }
        resetSessionKey();
        return null;
    }

    public MasterKey getMaster(int id) { return masterKeys.get(id); }

    public UserEntity createUser(String name) {
        MasterKey masterKey = new MasterKey(genKey(32));
        UserEntity entity = new UserEntity(name, masterKey);
        masterKeys.put(entity.getId(), masterKey);
        return entity;
    }

    public byte[] genKey(int len) {
        byte[] newKey = new byte[len];
        new Random().nextBytes(newKey);
        return newKey;
    }

    public void resetSessionKey() {sessionKey.setKey(genKey(32)); }

    // Getters and Setters
    public HashMap<Integer, MasterKey> getMasterKeys() { return masterKeys; }

    public void setMasterKeys(HashMap<Integer, MasterKey> masterKeys) { this.masterKeys = masterKeys; }

    public SessionKey getSessionKey() { return sessionKey; }

    public void setSessionKey(SessionKey sessionKey) { this.sessionKey = sessionKey; }
}
