package secure;

import java.util.ArrayList;
import java.util.Scanner;

enum MenuID { MENU_MAIN }

class Menus extends ArrayList<Menu> {
    // Constructors
    public Menus(Engine engine) {
        super();
        Menu.setEngine(engine);
        createMenus(engine);
    }

    // Methods
    public void loadMenu(MenuID id) {
        get(id.ordinal()).show();
    }

    public void createMenus(Engine engine) {
        add(new MainMenu());       // 0 - Menu
    }
}

abstract class Option {
    public String name;

    public Option(String name) {
        this.name = name;
    }

    public abstract void action();
}

public abstract class Menu {
    // Attributes
    static protected Engine engine;
    protected ArrayList<Option> options;
    protected boolean active;

    public static final String[] affirmations = new String[]{ "s", "sim", "si", "y", "yes" };

    // Constructors
    public Menu() {
        setOptions(new ArrayList<>());
        setActive(false);
        addOptions();
    }

    // Methods
    public abstract void addOptions();

    public void add(Option o) {
        options.add(o);
    }

    public void show() {
        setActive(true);
        showHeader();
        while (active) {
            handleOptions(options).action();
        }
    }

    public Option handleOptions(ArrayList<Option> options) {
        int len = options.size();

        System.out.println("Escolhas: ");
        for (int i = 0; i < len;  ++i) {
            System.out.println(i + ") " + options.get(i).name);
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Opção: ");
        String action = sc.next();
        System.out.println();

        if (Menu.isDigit(action)) {
            int index = Integer.parseInt(action);
            if (index >= 0 && index < len) {
                return options.get(index);
            } else {
                System.out.println("Entrada Inválida.");
            }
        } else {
            System.out.println("Entrada Inválida.");
        }
        return null;
    }

    public void update() {
        setOptions(new ArrayList<>());
        addOptions();
    }

    public void exit() {
        setActive(false);
    }

    public void showHeader() {}

    public static boolean isDigit(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <T> boolean elementInArray(T[] elements, T value) {
        for (T element : elements) {
            if (element.equals(value)) return true;
        }
        return false;
    }

    // Getters and Setter
    public static Engine getEngine() { return engine; }

    public static void setEngine(Engine engine) { Menu.engine = engine; }

    public ArrayList<Option> getOptions() { return options; }

    public void setOptions(ArrayList<Option> options) { this.options = options; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}

class MainMenu extends Menu {
    public MainMenu() { super(); }

    @Override
    public void addOptions() {
        add(new Option("Mandar Mensagem") {
            public void action() {
                Scanner sc = new Scanner(System.in);
                System.out.print("ID: ");
                String idStr = sc.next();
                if (isDigit(idStr)) {
                    System.out.print("Mensagem: ");
                    String message = sc.nextLine();
                    message = sc.nextLine();

                    System.out.println("ID: " + idStr + " | Mensagem: " + message);
                    System.out.print("Confirmar (s/n): ");
                    String confirm = sc.next();
                    if (elementInArray(affirmations, confirm)) {
                        engine.send(Integer.parseInt(idStr), message);
                    }
                    System.out.println();
                }
            }
        });

        add(new Option("Sair") {
            public void action() {
                System.out.println("Encerrando a sessão.");
                engine.close();
                System.exit(0);
            }
        });
    }
}