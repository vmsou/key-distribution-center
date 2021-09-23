package secure;

import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
    public abstract JSONObject toJSON();

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

    public UserEntity(JSONObject obj) {
        super(obj.getInt("id"), obj.getString("name"));
        setMasterKey(new MasterKey(obj.getString("masterKey").getBytes(StandardCharsets.UTF_8)));
        setMessages(new Messages());
    }

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

    public Message send(SessionsMessage sessionMessage) {
        // Send session key
        return new Message(
                getId(),
                sessionMessage.getSession2().toBytes());
    }

    public NonceMessage send(int nonce, SessionKey sessionKey) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return new NonceMessage(
                getId(),
                AES.encrypt(nonce, sessionKey));
    }

    public SessionKey receive(SessionsMessage message) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return new SessionKey(AES.decrypt(message.getSession1().toBytes(), masterKey));
    }

    public SessionKey receive(SessionMessage message) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return new SessionKey(AES.decrypt(message.getMessage(), masterKey));
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

    public String toSave() { return toJSON().toString(1); }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("masterKey", masterKey.toString());
        return obj;
    }

}
