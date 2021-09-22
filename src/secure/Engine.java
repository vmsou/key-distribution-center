package secure;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Engine {
    Global global;
    KDC kdc;
    UserEntity user;

    // Constructors
    public Engine() {
        new File("data").mkdir();
        setGlobal(new Global());
        setKdc(new KDC(this));
        setUser(load("data/data.json"));
        System.out.println();
    }

    // Methods
    public void send(int id, String message) {
        try {
            UserEntity to = getUser(id);
            Message msg = kdc.send(user, to, message);
            global.add(msg);
            to.add(msg);
        } catch (Exception e){
            System.out.println("Não foi possível enviar a mensagem.");
            e.printStackTrace();
        }

    }

    public UserEntity create(String name) {
        int id = UserEntity.count;
        MasterKey masterKey = new MasterKey(KDC.genKey(32));
        UserEntity newUser = new UserEntity(id, name, masterKey);
        kdc.add(id, masterKey);
        global.users.put(id, newUser);
        return newUser;
    }

    public static String fstream(String filename) {
        try {
            return Files.readString(Path.of(filename), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
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
        String file = fstream(filename);
        if (file == null) return null;
        JSONObject obj = new JSONObject(file);
        if (obj.isEmpty()) return null;
        return new UserEntity(
                obj.getInt("id"),
                obj.getString("name"),
                new MasterKey(obj.getString("masterKey").getBytes(StandardCharsets.UTF_8))
        );
    }

    public void close() {
        System.out.println("Saving data...");
        save(user, "data/data.json");
        save(global.users, "data/users.json");
        save(global.messages, "data/messages.json");
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
