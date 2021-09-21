package secure;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Global {
    Users users;
    Messages messages;

    public Global() {
        users = getUsersData("data/users.csv");
    }

    public Users getUsersData(String filename) {
        Users users = new Users();
        Scanner sc = Engine.fstream(filename);

        if (sc != null) {
            while (sc.hasNextLine()) {
                String[] attr = sc.nextLine().split(",");
                int id = Integer.parseInt(attr[0]);
                users.put(id, new UserEntity(
                        id,      // id
                        attr[1],                        // name
                        new MasterKey(attr[2].getBytes(StandardCharsets.UTF_8))
                ));

            }
        }
        return users;
    }

}
