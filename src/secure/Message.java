package secure;

import org.json.JSONObject;

public class Message extends Entity {
    static int count;
    private int sender;
    private byte[] message;

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
        return getId() + "," + getName() + "," + sender + "," + this;
    }

    @Override
    public JSONObject toJSON() {
        return new JSONObject();
    }
}

class ProofMessage extends Message {
    private byte[] proof;
    private byte[] receiver;

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
    public String toSave() {
        return getId() + "," + getName() + "," + getSender() + "," + new String(getProof()) +
                new String(getReceiver()) + "," + this;
    }

}

class SessionMessage extends Message {
    private ProofMessage proofMessage;
    private Message session1, session2;

    public SessionMessage(ProofMessage proofMessage, Message session1, Message session2) {
        super("SESSION", session1.getSender(), proofMessage.getMessage());
        setProofMessage(proofMessage);
        setSession1(session1);
        setSession2(session2);
    }

    public ProofMessage getProofMessage() { return proofMessage; }

    public void setProofMessage(ProofMessage proofMessage) { this.proofMessage = proofMessage; }

    public String getReceiver() { return String.valueOf(getProofMessage().getSender()); }

    public Message getSession1() { return session1; }

    public void setSession1(Message session1) { this.session1 = session1; }

    public Message getSession2() { return session2; }

    public void setSession2(Message session2) { this.session2 = session2; }

    @Override
    public String toSave() {
        return getId() + "," + getName() + "," + getSender() + "," + getReceiver() + ","  + getSession1() + "," + getSession2();
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
    public String toSave() {
        return String.valueOf(
                getId()) + "," + getName() + "," + getSender() + "," + getReceiver() + "," + this;
    }
}
