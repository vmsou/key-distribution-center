package secure;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public abstract class Entity {
    private int id;
    private String name;

    public Entity(int id) {
        setId(id);
    }

    public Entity(int id, String name) {
        setId(id);
        setName(name);
    }

    public abstract String toSave();

    // Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}

class UserEntity extends Entity {
    static int count = 1;
    private MasterKey masterKey;
    private Messages messages;

    public UserEntity(int id, String name, MasterKey masterKey) {
        super(id, name);
        setMasterKey(masterKey);
        setMessages(new Messages());
        ++count;
    }

    // Methods
    public void add(Message m) { messages.put(m.getId(), m); }

    public void showMessages() {
        System.out.println("Mensagens de " + getName());
        for (Message m : messages.values())
            System.out.println(m.decryptedText(masterKey));
    }

    public ProofMessage send(UserEntity receiver, String message) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return new ProofMessage(
                getId(),                                    // sender
                AES.encrypt(getId(), masterKey),            // proof
                AES.encrypt(receiver.getId(), masterKey),   // receiver
                AES.encrypt(message, masterKey));           // message

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
        if (sessionMessage.getProofMessage().getSender() == getId())
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
        byte[] decryptedOldNonce = AES.decrypt(oldNonce.getMessage(), sessionKey);
        byte[] decryptedNewNonce = AES.decrypt(newNonce.getMessage(), sessionKey);

        int nonce1 = ByteBuffer.wrap(decryptedOldNonce).getInt();
        int nonce2 = ByteBuffer.wrap(decryptedNewNonce).getInt();
        int correctNonce = nonceFunc(nonce1);

        return nonce2 == correctNonce;
    }

    public int nonceFunc(int nonce) {
        return nonce + 1;
    }

    // Getters and Setters
    public MasterKey getMasterKey() { return masterKey; }

    public void setMasterKey(MasterKey masterKey) { this.masterKey = masterKey; }

    public Messages getMessages() { return messages; }

    public void setMessages(Messages messages) { this.messages = messages; }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", masterKey=" + masterKey +
                ", messages=" + messages +
                '}';
    }

    public String toSave() {
        return getId() + "," + getName() + "," + masterKey;
    }

}
