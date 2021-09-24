package secure;


import org.json.JSONArray;
import org.json.JSONObject;

public class Client {
    Users users;
    Messages messages;

    // Constructors
    public Client() {
        users = getUsersData("data/users.json");
        messages = getMessagesData("data/messages.json");
    }

    // Methods
    public void add(UserEntity u) { users.put(u.getId(), u); }

    public void send(Message m) { messages.put(m.getId(), m); }

    public Users getUsersData(String filename) {
        String file = Engine.fstream(filename);
        if (file == null || file.isEmpty()) return new Users();
        JSONArray arr = new JSONArray(file);
        if (arr.isEmpty()) return new Users();

        return new Users(arr);
    }

    public Messages getMessagesData(String filename) {
        String file = Engine.fstream(filename);
        if (file == null || file.isEmpty()) return new Messages();
        JSONArray arr = new JSONArray(file);
        if (arr.isEmpty()) return new Messages();
        return new Messages(arr);
    }

    public void showUsers() {
        for (UserEntity u : users.values())
            System.out.println(u);
    }

    public void showMessages() {
        for (Message m : messages.values())
            System.out.println(m);
    }

}
