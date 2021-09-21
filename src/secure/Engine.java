package secure;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Engine {
    Global global;
    KDC kdc;
    UserEntity user;

    // Constructors
    public Engine() {
        setGlobal(new Global());
        setKdc(new KDC(this));
        setUser(load("data/bob.txt"));
    }

    // Methods
    public Scanner fstream(String filename) {
        System.out.println("Lendo o arquivo '" + filename + '\'');
        try {
            File file = new File(filename);
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Não foi possível ler o arquivo: '" + filename + '\'');
        }
        return null;
    }

    public UserEntity load(String filename) {
        Scanner sc = fstream(filename);
        String[] attr = sc.nextLine().split(",");
        return new UserEntity(
                Integer.parseInt(attr[0]),      // id
                attr[1],                        // name
                new MasterKey(attr[2].getBytes(StandardCharsets.UTF_8))
        );
    }

    // Getters amd Setters
    public Global getGlobal() { return global; }

    public void setGlobal(Global global) { this.global = global; }

    public KDC getKdc() { return kdc; }

    public void setKdc(KDC kdc) { this.kdc = kdc; }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }
}
