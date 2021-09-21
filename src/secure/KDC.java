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
    private HashMap<Integer, MasterKey> masterKeys; // DON'T EXPOSE IN THE WEB
    private SessionKey sessionKey;

    // Constructors
    public KDC() {
        super("KDC");
        setMasterKeys(new HashMap<>());
        setSessionKey(new SessionKey(genKey(32)));
    }

    // Methods
    public SessionMessage receive(ProofMessage proofMessage) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int sender = proofMessage.getSender();
        MasterKey senderKey = getMaster(sender);

        byte[] decryptedReceiver = AES.decrypt(proofMessage.getReceiver(), senderKey);
        int receiver = ByteBuffer.wrap(decryptedReceiver).getInt();
        MasterKey receiverKey = getMaster(receiver);

        // Verify proof
        if (proofMessage.getProof() == AES.encrypt(sender, senderKey)) {
            // receives sessionKey encrypted with sender keys
            Message session1 = new Message(
                    getId(),
                    AES.encrypt(sessionKey, senderKey)
            );

            // receives sessionKey encrypted with receiver keys
            Message session2 = new Message(
                    getId(),
                    AES.encrypt(sessionKey, receiverKey)
            );
            return new SessionMessage(proofMessage, session1, session2);
        }
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

    // Getters and Setters
    public HashMap<Integer, MasterKey> getMasterKeys() { return masterKeys; }

    public void setMasterKeys(HashMap<Integer, MasterKey> masterKeys) { this.masterKeys = masterKeys; }

    public SessionKey getSessionKey() { return sessionKey; }

    public void setSessionKey(SessionKey sessionKey) { this.sessionKey = sessionKey; }
}
