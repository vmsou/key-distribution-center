package secure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
        setUser(load("data/data.txt"));
    }

    // Methods
    public void send(int id, String message) {
        UserEntity to = global.users.get(id);
        kdc.send(user, to, message);
    }

    public void create(String name) {
        int id = UserEntity.count;
        global.users.put(id, new UserEntity(id, name, new MasterKey(KDC.genKey(32))));
    }

    public static Scanner fstream(String filename) {
        System.out.println("Lendo o arquivo '" + filename + '\'');
        try {
            File file = new File(filename);
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Não foi possível ler o arquivo: '" + filename + '\'');
        }
        return null;
    }

    public<T extends Entity> void save (EntityContainer<T> data, String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(data.toSave());
            fw.close();
        } catch (Exception e) {
            System.out.println("Não foi possível salvar.");
        }
    }

    public UserEntity load(String filename) {
        Scanner sc = fstream(filename);
        UserEntity user = null;
        if (sc != null) {
            String[] attr = sc.nextLine().split(",");
            user = new UserEntity(
                    Integer.parseInt(attr[0]),      // id
                    attr[1],                        // name
                    new MasterKey(attr[2].getBytes(StandardCharsets.UTF_8))
            );
        }
        return user;
    }

    // Getters amd Setters
    public Global getGlobal() { return global; }

    public void setGlobal(Global global) { this.global = global; }

    public KDC getKdc() { return kdc; }

    public void setKdc(KDC kdc) { this.kdc = kdc; }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }
}
