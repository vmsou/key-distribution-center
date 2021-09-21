package secure;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.SynchronousQueue;

public class Entity {
    static private int count = 0;
    private int id;
    private String name;

    public Entity(String name) {
        setId(count);
        setName(name);
        ++count;
    }

    // Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}

class UserEntity extends Entity {
    private MasterKey masterKey;

    public UserEntity(String name, MasterKey masterKey) {
        super(name);
        setMasterKey(masterKey);
    }

    public ProofMessage send(UserEntity receiver, String message) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // KDC Verifies identity
        return new ProofMessage(
                getId(),
                AES.encrypt(getId(), masterKey),
                AES.encrypt(receiver.getId(), masterKey),
                AES.encrypt(message, masterKey));
    }

    public Message send(SessionMessage sessionMessage) {
        // Send session key
        return new Message(
                getId(),
                sessionMessage.getSession2().getMessage());
    }

    public NonceMessage send(int nonce, SessionKey sessionKey) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return new NonceMessage(
                getId(),
                AES.encrypt(nonce, sessionKey));
    }

    public SessionKey receive(SessionMessage sessionMessage) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] encryptedSessionKey;
        if (sessionMessage.getIdMessage().getSender() == getId())
            encryptedSessionKey = sessionMessage.getSession1().getMessage();
        else
            encryptedSessionKey = sessionMessage.getSession2().getMessage();

        return new SessionKey(AES.decrypt(encryptedSessionKey, masterKey));
    }

    public NonceMessage receive(NonceMessage nonceMessage, SessionKey sessionKey) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] decryptedNonce = AES.decrypt(nonceMessage.getMessage(), sessionKey);
        int nonce = ByteBuffer.wrap(decryptedNonce).getInt();
        int newNonce = nonceFunc(nonce);
        return new NonceMessage(
                getId(),
                AES.encrypt(newNonce, sessionKey));
    }

    public boolean verifyNonce(NonceMessage oldNonce, NonceMessage newNonce, SessionKey sessionKey) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int nonce1 = ByteBuffer.wrap(oldNonce.getMessage()).getInt();
        int nonce2 = ByteBuffer.wrap(newNonce.getMessage()).getInt();
        int correctNonce = nonceFunc(nonce1);

        return nonce2 == correctNonce;
    }

    public MasterKey getMasterKey() { return masterKey; }

    public void setMasterKey(MasterKey masterKey) { this.masterKey = masterKey; }

    public int nonceFunc(int nounce) {
        return nounce + 1;
    }
}
