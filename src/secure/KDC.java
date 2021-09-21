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
    private Engine engine;
    private HashMap<Integer, MasterKey> masterKeys; // DON'T EXPOSE
    private SessionKey sessionKey;

    // Constructors
    public KDC(Engine engine) {
        super(0, "KDC");
        setEngine(engine);
        loadMasterKeys();
        setSessionKey(new SessionKey(genKey(32)));
    }

    // Methods
    public void add(int i, MasterKey masterKey) { masterKeys.put(i, masterKey); }

    public void loadMasterKeys() {
        masterKeys = new HashMap<>();
        for (UserEntity u : engine.global.users.values())
            add(u.getId(), u.getMasterKey());
    }

    public void send(UserEntity from, UserEntity to, String message) {
        try {
            // Bob sends message to KDC with id, AES(id), AES(receiver), AES(message)
            ProofMessage msg = from.send(to, message);
            if (Main.DEBUG) System.out.println(from.getName() + " enviou mensagem para KDC");

            // KDC sends to Bob his session key and alice's session key
            SessionMessage sessionMessage = receive(msg);   // SessionKey refreshed
            if (Main.DEBUG) System.out.println("KDC enviou as chaves de sessões para " +  from.getName());

            if (sessionMessage != null) {   // Proof was correct
                if (Main.DEBUG) System.out.println(from.getName() + " comprovou que tinha a chave mestre");
                // Bob receive his sessionKey and sends alice's sessionKey
                SessionKey fromSessionKey = from.receive(sessionMessage);
                SessionKey toSessionKey = to.receive(sessionMessage);
                if (Main.DEBUG) System.out.println(from.getName() + " recebe sua chave de sessão e envia para " + to.getName());

                // Alice sends nonce to bob
                NonceMessage nonceMessage = to.send(Main.genNonce(), toSessionKey);
                if (Main.DEBUG) System.out.println("to.getName() manda nonce para " + from.getName());

                // Bob receives and updates nonce number
                NonceMessage fromNonce = from.receive(nonceMessage, fromSessionKey);
                if (Main.DEBUG) System.out.println(from.getName() + " atualiza o nonce");

                // Alice updates her nonce and compares with bob nonce
                if (Main.DEBUG) System.out.println(to.getName() + " esta verificando o nonce");
                if (to.verifyNonce(nonceMessage, fromNonce, toSessionKey)) {
                    if (Main.DEBUG) System.out.println("Nonce correto.");
                    byte[] msgSessionDecrypted = sessionMessage.decrypt(toSessionKey);
                    byte[] msgMasterEncrypted = AES.encrypt(msgSessionDecrypted, to.getMasterKey());

                    Message finalMessage = new Message(
                            msg.getSender(),
                            msgMasterEncrypted
                    );
                    to.addMessage(finalMessage);
                } else {
                    System.out.println("Nonce falhou.");
                }
            } else
                System.out.println("Prova de identidade falhou");
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    public SessionMessage receive(ProofMessage proofMessage) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int sender = proofMessage.getSender();
        MasterKey senderKey = getMaster(sender);

        byte[] encryptedReceiver = proofMessage.getReceiver();
        byte[] decryptedReceiver = AES.decrypt(encryptedReceiver, senderKey);
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


    public static byte[] genKey(int len) {
        byte[] newKey = new byte[len];
        new Random().nextBytes(newKey);
        return newKey;
    }

    public void resetSessionKey() {sessionKey.setKey(genKey(32)); }

    // Getters and Setters
    public Engine getEngine() { return engine; }

    public void setEngine(Engine engine) { this.engine = engine; }

    public HashMap<Integer, MasterKey> getMasterKeys() { return masterKeys; }

    public void setMasterKeys(HashMap<Integer, MasterKey> masterKeys) { this.masterKeys = masterKeys; }

    public SessionKey getSessionKey() { return sessionKey; }

    public void setSessionKey(SessionKey sessionKey) { this.sessionKey = sessionKey; }

    @Override
    public String toSave() {
        return "";
    }
}
