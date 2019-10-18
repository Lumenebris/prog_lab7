package server;

import commands.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Класс представляет объект сервера, который манипулирует CollectionManager.
 */
public class ServerConnection implements Runnable {

    private StorageManager manager;
    private Socket incoming;
    private HashMap<String, AbstractCommand> availableCommands;

    /**
     * Команды, доступные клиенту.
     * @param manager обеспечивает доступ к коллекции.
     * @param incoming активное соединение с клиентской программой.
     */
    ServerConnection(StorageManager manager, Socket incoming) {
        this.manager = manager;
        this.incoming = incoming;
        availableCommands = new HashMap<>();
        availableCommands.put("add_user", new AddUserCommand(manager));
        availableCommands.put(RegisterUserCommand.NAME, new RegisterUserCommand(manager));
        availableCommands.put("show", new ShowCommand(manager));
        availableCommands.put("info", new InfoCommand(manager));
        availableCommands.put("save", new SaveCommand(manager));
        availableCommands.put("clear", new ClearCommand(manager));
        availableCommands.put("remove_all", new RemoveAllCommand(manager));
        availableCommands.put("remove", new RemoveCommand(manager));
        availableCommands.put("remove_lower", new RemoveLowerCommand(manager));
        availableCommands.put("load", new LoadCommand(manager));
        availableCommands.put("import", new ImportCommand(manager));
        availableCommands.put("insert", new InsertCommand(manager));
        availableCommands.put("help", new HelpCommand(manager, availableCommands));
        availableCommands.put("man", new ManCommand(manager, availableCommands));
    }

    /**
     * Запускает активное соединение с клиентом.
     */
    @Override
    public void run() {
        try (ObjectInputStream getFromClient = new ObjectInputStream(incoming.getInputStream());
             ObjectOutputStream sendToClient = new ObjectOutputStream(incoming.getOutputStream())) {
            sendToClient.writeObject("\nСоединение установлено.\nВы можете вводить команды.\nВведите 'help' для получения списка команд.");
            AbstractCommand errorCommand = new AbstractCommand(null) {
                @Override
                public String execute() {
                    return "Неизвестная команда. Введите 'help' для получения списка команд.";
                }
            };
            while (true) {
                try {
                    String requestFromClient = (String) getFromClient.readObject();
                    System.out.print("Получено [" + requestFromClient + "] от " + incoming + ". ");
                    System.out.println();
                    String[] parsedCommand = parseCommandLine(requestFromClient.trim());
                    sendToClient.writeObject(availableCommands.getOrDefault(parsedCommand[0], errorCommand).execute(parsedCommand));
                    System.out.println("Ответ успешно отправлен.");
                } catch (SocketException e) {
                    System.out.println(incoming + " отключился от сервера."); //Windows
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(incoming + " отключился от сервера."); //Unix
        }
    }

    private String[] parseCommandLine(String commandLine) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        boolean insideQuote = false;

        for (char c : commandLine.toCharArray()) {
            if (c == '\'') {
                insideQuote = !insideQuote;
            }
            if (c == ' ' && !insideQuote) {
                tokens.add(sb.toString());
                sb.delete(0, sb.length());
            } else
                sb.append(c);
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return "ServerConnection{" +
                "serverCollection=" + manager +
                ", incoming=" + incoming +
                ", availableCommands=" + availableCommands +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerConnection)) return false;
        ServerConnection that = (ServerConnection) o;
        return Objects.equals(manager, that.manager) &&
                Objects.equals(availableCommands, that.availableCommands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, availableCommands);
    }
}