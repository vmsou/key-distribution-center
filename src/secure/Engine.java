package secure;

public class Engine {
    Global global;
    KDC kdc;
    UserEntity user;

    // Constructors
    public Engine() {
        setGlobal(new Global());
        setKdc(new KDC());
    }

    // Methods

    // Getters amd Setters
    public Global getGlobal() { return global; }

    public void setGlobal(Global global) { this.global = global; }

    public KDC getKdc() { return kdc; }

    public void setKdc(KDC kdc) { this.kdc = kdc; }
}
