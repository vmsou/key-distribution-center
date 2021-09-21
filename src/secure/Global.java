package secure;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Global {
    Users users;
    Messages messages;

    public Global() {
        users = getUsersData("data/users.csv");
    }

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

    public Users getUsersData(String filename) {
        Users users = new Users();
        Scanner sc = fstream(filename);

        while (sc.hasNextLine()) {
            String[] attr = sc.nextLine().split(",");
            users.add(new UserEntity(
                Integer.parseInt(attr[0]),      // id
                attr[1],                        // name
                new MasterKey(attr[2].getBytes(StandardCharsets.UTF_8))
            ));

        }
        return users;
    }

}
