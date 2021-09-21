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
        setUser(load("data/data.csv"));
    }

    // Methods
    public void send(int id, String message) {
        kdc.send(user, getUser(id), message);
    }

    public UserEntity create(String name) {
        int id = UserEntity.count;
        UserEntity newUser = new UserEntity(id, name, new MasterKey(KDC.genKey(32)));
        global.users.put(id, newUser);
        return newUser;
    }

    public static Scanner fstream(String filename) {
        System.out.println("Lendo o arquivo '" + filename + '\'');
        File file = new File(filename);
        try {
            if (file.createNewFile()) return null;
            return new Scanner(file);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

    public<T extends Entity> void save (T data, String filename) {
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

    public void close() {
        System.out.println("Saving data...");
        save(user, "data/data.csv");
        save(global.users, "data/users.csv");
        // save(global.messages, "data/messages.csv");
        System.out.println("Done.");
    }

    public UserEntity getUser(int id) { return global.users.get(id); }

    // Getters amd Setters
    public Global getGlobal() { return global; }

    public void setGlobal(Global global) { this.global = global; }

    public KDC getKdc() { return kdc; }

    public void setKdc(KDC kdc) { this.kdc = kdc; }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }
}
