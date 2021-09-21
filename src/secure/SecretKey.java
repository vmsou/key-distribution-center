package secure;

public abstract class SecretKey {
    private byte[] key;

    // Constructor
    public SecretKey(byte[] key) { setKey(key); }

    // Methods
    public byte[] toBytes() { return key; }

    @Override
    public String toString() { return new String(getKey()); }

    // Getters and Setters
    public byte[] getKey() { return key; }

    public void setKey(byte[] key) { this.key = key; }
}

class MasterKey extends SecretKey {
    public MasterKey(byte[] key) { super(key); }
}

class SessionKey extends SecretKey {
    public SessionKey(byte[] key) { super(key); }
}
