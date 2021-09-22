package secure;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Global {
    Users users;
    Messages messages;

    // Constructors
    public Global() {
        users = getUsersData("data/users.csv");
        // messages = getMessagesData("data/messages.csv");
        messages = new Messages();
    }

    // Methods
    public void add(UserEntity u) { users.put(u.getId(), u); }
    public void add(Message m) { messages.put(m.getId(), m); }

    public Users getUsersData(String filename) {
        int id = 1;
        Users users = new Users();
        Scanner sc = Engine.fstream(filename);

        if (sc != null) {
            while (sc.hasNextLine()) {
                String[] attr = sc.nextLine().split(",");
                if (attr.length == 3) {
                    id = Integer.parseInt(attr[0]);
                    UserEntity.count = id;
                    users.put(id, new UserEntity(
                            id,                             // id
                            attr[1],                        // name
                            new MasterKey(attr[2].getBytes(StandardCharsets.UTF_8))
                    ));
                }
            }
        }
        UserEntity.count = id;
        return users;
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
