package secure;

import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
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
        for (UserEntity u : engine.client.users.values())
            add(u.getId(), u.getMasterKey());
    }

    public Message send(UserEntity from, UserEntity to, String message) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Bob sends message to KDC with id, AES(id), AES(receiver), AES(message)
        ProofMessage msg = from.send(to, message);
        if (Main.DEBUG) System.out.println(from.getName() + " enviou mensagem para KDC");
        engine.client.send(msg);

        // KDC sends to Bob his session key and alice's session key
        SessionsMessage sessionsMessage = receive(msg);   // SessionKey refreshed

        if (sessionsMessage != null) {   // Proof was correct
            if (Main.DEBUG)  {
                System.out.println(from.getName() + " comprovou que tinha a chave mestre");
                System.out.println("KDC enviou as chaves de sessões para " +  from.getName());
            }

            engine.client.send(sessionsMessage);
            // Bob receive his sessionKey and sends alice's sessionKey
            SessionKey fromSessionKey = from.receive(sessionsMessage);
            SessionMessage aliceSession = new SessionMessage(from.getId(), msg.getReceiver(), sessionsMessage.getSession2().toBytes());
            engine.client.send(aliceSession);
            SessionKey toSessionKey = to.receive(aliceSession);

            if (Main.DEBUG) System.out.println(from.getName() + " recebe sua chave de sessão e envia para " + to.getName());

            // Alice sends nonce to bob
            NonceMessage nonceMessage = to.send(Main.genNonce(), from.getId(), toSessionKey);
            if (Main.DEBUG) System.out.println(to.getName() + " manda nonce para " + from.getName());
            engine.client.send(nonceMessage);

            // Bob receives and updates nonce number
            NonceMessage fromNonce = from.receive(nonceMessage, fromSessionKey);
            if (Main.DEBUG) System.out.println(from.getName() + " atualiza o nonce");
            engine.client.send(fromNonce);

            // Alice updates her nonce and compares with bob nonce
            if (Main.DEBUG) System.out.println(to.getName() + " esta verificando o nonce");
            if (to.verifyNonce(nonceMessage, fromNonce, toSessionKey)) {
                if (Main.DEBUG) System.out.println("Nonce correto.");
                byte[] msgSessionDecrypted = sessionsMessage.decrypt(toSessionKey);
                byte[] msgMasterEncrypted = AES.encrypt(msgSessionDecrypted, to.getMasterKey());

                return new SendMessage(
                        msg.getSender(),
                        to.getId(),
                        msgMasterEncrypted);
            } else {
                System.out.println("Nonce falhou.");
            }
        }
        return null;
    }

    public SessionsMessage receive(ProofMessage proofMessage) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
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

            // sessionKey encrypted with sender keys
            SessionKey session1 = new SessionKey(AES.encrypt(sessionKey, senderKey));

            // sessionKey encrypted with receiver keys
            SessionKey session2 = new SessionKey(AES.encrypt(sessionKey, receiverKey));

            resetSessionKey();

            return new SessionsMessage(getId(), proofMessage.getSender(), encryptedMessage, session1, session2);
        }
        resetSessionKey();
        return null;
    }

    public MasterKey getMaster(int id) { return masterKeys.get(id); }

    public static byte[] genKey1(int len) {
        byte[] newKey = new byte[len];
        new Random().nextBytes(newKey);
        return newKey;
    }

    public static byte[] genKey(int len) {
        String str = UUID.randomUUID().toString().substring(0, len);
        return str.getBytes(StandardCharsets.UTF_8);

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
        return toJSON().toString(1);
    }

    public JSONObject toJSON() { return new JSONObject(); }
}
