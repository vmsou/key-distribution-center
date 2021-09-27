package secure;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Engine {
    Client client;
    Menus menus;
    KDC kdc;
    UserEntity user;

    // Constructors
    public Engine() {
        if (new File("data").mkdir()) System.out.println("data folder created.");;
        setClient(new Client());
        setMenus(new Menus(this));
        setKdc(new KDC(this));
        setUser(load("data/data.json"));
        System.out.println();
    }

    // Methods
    public void run() {
        menus.loadMenu(MenuID.MENU_MAIN);
    }

    public void send(int id, String message) {
        try {
            UserEntity to = getUser(id);
            Message msg = kdc.send(user, to, message);
            if (msg != null) {
                client.send(msg);
                to.add(msg);
                System.out.println("Mensagem enviada.");
            }
        } catch (Exception e){
            System.out.println("Não foi possível enviar a mensagem.");
        }
    }

    public UserEntity create(String name) {
        int id = UserEntity.count;
        MasterKey masterKey = new MasterKey(KDC.genKey(32));
        UserEntity newUser = new UserEntity(id, name, masterKey);
        kdc.add(id, masterKey);
        client.users.put(id, newUser);
        return newUser;
    }

    public static String fstream(String filename) {
        try {
            return Files.readString(Path.of(filename), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public<T extends Entity> void save(EntityContainer<T> data, String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(data.toSave());
            fw.close();
        } catch (Exception e) {
            System.out.println("Não foi possível salvar. " + e.getMessage());
        }
    }

    public<T extends Entity> void save(T data, String filename) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(data.toSave());
            fw.close();
        } catch (Exception e) {
            System.out.println("Não foi possível salvar. " + e.getMessage());
        }
    }

    public UserEntity load(String filename) {
        String file = fstream(filename);
        if (file == null) return null;
        JSONObject obj = new JSONObject(file);
        if (obj.isEmpty()) return null;
        return new UserEntity(client, obj);
    }

    public void close() {
        System.out.println("Saving data...");
        save(user, "data/data.json");
        save(client.users, "data/users.json");
        save(client.messages, "data/messages.json");
        System.out.println("Done.");
    }

    public UserEntity getUser(int id) { return client.users.get(id); }

    // Getters amd Setters
    public Client getClient() { return client; }

    public void setClient(Client client) { this.client = client; }

    public Menus getMenus() { return menus; }

    public void setMenus(Menus menus) { this.menus = menus; }

    public KDC getKdc() { return kdc; }

    public void setKdc(KDC kdc) { this.kdc = kdc; }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }
}
