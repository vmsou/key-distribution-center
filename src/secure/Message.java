package secure;

import java.util.ArrayList;
import java.util.Arrays;

public class Message {
    private int sender;
    private byte[] message;

    public Message(int sender, byte[] message) {
        setSender(sender);
        setMessage(message);
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
        return "Message{" +
                "sender=" + sender +
                ", message=" + new String(message) +
                '}';
    }
}

class ProofMessage extends Message {
    private byte[] proof;
    private byte[] receiver;

    public ProofMessage(int sender, byte[] proof, byte[] receiver, byte[] message) {
        super(sender, message);
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
    public String toString() {
        return "ProofMessage{" +
                "proof=" + Arrays.toString(proof) +
                ", receiver=" + Arrays.toString(receiver) +
                '}';
    }
}

class SessionMessage extends Message {
    private ProofMessage proofMessage;
    private Message session1, session2;

    public SessionMessage(ProofMessage idMessage, Message session1, Message session2) {
        super(session1.getSender(), idMessage.getMessage());
        setProofMessage(idMessage);
        setSession1(session1);
        setSession2(session2);
    }

    public ProofMessage getProofMessage() { return proofMessage; }

    public void setProofMessage(ProofMessage proofMessage) { this.proofMessage = proofMessage; }

    public Message getSession1() { return session1; }

    public void setSession1(Message session1) { this.session1 = session1; }

    public Message getSession2() { return session2; }

    public void setSession2(Message session2) { this.session2 = session2; }
}

class NonceMessage extends Message {
    public NonceMessage(int sender, byte[] message) {
        super(sender, message);
    }
}

