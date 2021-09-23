package secure;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Message extends Entity {
    static int count;
    private int sender;
    private byte[] message;

    public Message(int id, String name, int sender, byte[] message) {
        super(id, name);
        setSender(sender);
        setMessage(message);
    }

    public Message(JSONObject obj) {
        super(obj.getInt("id"), obj.getString("name"));
        setSender(obj.getInt("sender"));
        setMessage(obj.getString("message").getBytes(StandardCharsets.UTF_8));
    }

    public Message(int sender, byte[] message) {
        super(count);
        setName("MESSAGE");
        setSender(sender);
        setMessage(message);
        ++count;
    }

    public Message(String name, int sender, byte[] message) {
        super(count, name);
        setSender(sender);
        setMessage(message);
        ++count;
    }

    // Methods
    public byte[] encrypt(SecretKey secretKey) {
        try {
            return AES.encrypt(message, secretKey);
        } catch (Exception e) {
            System.out.println("Couldn't encrypt message");
            return null;
        }
    }

    public byte[] decrypt(SecretKey secretKey) {
        try {
            return AES.decrypt(message, secretKey);
        } catch (Exception e) {
            System.out.println("Couldn't decrypt message");
            return null;
        }
    }

    public String decryptedText(SecretKey secretKey) { return new String(decrypt(secretKey)); }

    // Getters and Setters
    public int getSender() { return sender; }

    public void setSender(int sender) { this.sender = sender; }

    public byte[] getMessage() { return message; }

    public void setMessage(byte[] message) { this.message = message; }

    @Override
    public String toString() {
        return new String(getMessage());
    }

    @Override
    public String toSave() {
        return toJSON().toString(1);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj =  new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("sender", getSender());
        obj.put("message", toString());
        return obj;
    }
}

class ProofMessage extends Message {
    private byte[] proof;
    private byte[] receiver;

    public ProofMessage(JSONObject obj) {
        super(
            obj.getInt("id"),
            obj.getString("name"),
            obj.getInt("sender"),
            obj.getString("message").getBytes(StandardCharsets.UTF_8)
        );
        setProof(obj.getString("proof").getBytes(StandardCharsets.UTF_8));
        setReceiver(obj.getString("receiver").getBytes(StandardCharsets.UTF_8));
    }

    public ProofMessage(int sender, byte[] proof, byte[] receiver, byte[] message) {
        super("PROOF", sender, message);
        setProof(proof);
        setReceiver(receiver);
    }

    public byte[] getProof() {
        return proof;
    }

    public void setProof(byte[] proof) {
        this.proof = proof;
    }

    public byte[] getReceiver() { return receiver; }

    public void setReceiver(byte[] receiver) { this.receiver = receiver; }

    @Override
    public JSONObject toJSON() {
        JSONObject obj =  new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("sender", getSender());
        obj.put("proof", new String(getProof()));
        obj.put("receiver", new String(getReceiver()));
        obj.put("message", toString());
        return obj;
    }

}
class SessionMessage extends Message {
    private int receiver;
    private SessionKey session1, session2;

    public SessionMessage(JSONObject obj) {
        super(
            obj.getInt("id"),
            obj.getString("name"),
            obj.getInt("sender"),
            obj.getString("message").getBytes(StandardCharsets.UTF_8)
        );
        setReceiver(obj.getInt("receiver"));
        setSession1(new SessionKey(obj.getString("session1").getBytes(StandardCharsets.UTF_8)));
        setSession2(new SessionKey(obj.getString("session2").getBytes(StandardCharsets.UTF_8)));
    }

    public SessionMessage(int sender, int receiver, byte[] message, SessionKey session1, SessionKey session2) {
        super("SESSION", sender, message);
        setReceiver(receiver);
        setSession1(session1);
        setSession2(session2);
    }

    public void setReceiver(int receiver) { this.receiver = receiver; }

    public int getReceiver() { return receiver; }

    public SessionKey getSession1() { return session1; }

    public void setSession1(SessionKey session1) { this.session1 = session1; }

    public SessionKey getSession2() { return session2; }

    public void setSession2(SessionKey session2) { this.session2 = session2; }

    @Override
    public JSONObject toJSON() {
        JSONObject obj =  new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("sender", getSender());
        obj.put("receiver", getReceiver());
        obj.put("session1", getSession1());
        obj.put("session2", getSession2());
        obj.put("message", toString());
        return obj;
    }
}

class NonceMessage extends Message {
    public NonceMessage(int sender, byte[] message) {
        super("NONCE", sender, message);
    }
}

class SendMessage extends Message {
    private int receiver;

    // Constructors
    public SendMessage(int sender, int receiver, byte[] message) {
        super("SEND", sender, message);
        setReceiver(receiver);
    }

    // Getters and Setters
    public int getReceiver() { return receiver; }

    public void setReceiver(int receiver) { this.receiver = receiver; }

    @Override
    public JSONObject toJSON() {
        JSONObject obj =  new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("sender", getSender());
        obj.put("receiver", getReceiver());
        obj.put("message", toString());
        return obj;
    }
}
