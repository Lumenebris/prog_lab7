package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import tale.Room;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс представляет объект клиента, подключаемый к серверу для манипулирования коллекцией.
 */
public class ClientConnection {

    private static Scanner fromKeyboard;
    private static ObjectOutputStream toServer;
    private static ObjectInputStream fromServer;
    private static int port;

    /**
     * Устанавливает активное соединение с сервером.
     */
    public void work() {
        try (Scanner scanner = new Scanner(System.in)) {
            fromKeyboard = scanner;
            String command;
            boolean flag = true;
            System.out.println("Введите connect номер_порта");
            try {
                while (flag) {
                    command = fromKeyboard.nextLine();
                    String[] parsedCommand = command.trim().split(" ", 2);
                    switch (parsedCommand[0]) {
                        case "connect":
                            try {
                                if (Integer.parseInt(parsedCommand[1]) < 0  || Integer.parseInt(parsedCommand[1]) > 65535)
                                    System.err.println("Порт сервера должен быть целым числом от 0 до 65535.");
                                else {
                                    port = Integer.parseInt(parsedCommand[1]);
                                    System.out.println("Задан порт сервера: " + port);
                                    flag = false;
                                }
                            } catch (NumberFormatException e){
                                System.err.println("Номер порта задан неверно.");
                            }
                            break;
                        default:
                            System.err.println("Ошибка, неизвестная команда.");
                    }
                }
            } catch (NoSuchElementException ex) {
                exit();
            }
            while (true) {
                try (SocketChannel channel = SocketChannel.open()) {
                    channel.connect(new InetSocketAddress("localhost", port));
                    channel.socket().setSoTimeout(5000);
                    try (ObjectOutputStream outputStream = new ObjectOutputStream(channel.socket().getOutputStream());
                         ObjectInputStream inputStream = new ObjectInputStream(channel.socket().getInputStream())) {
                        toServer = outputStream;
                        fromServer = inputStream;
                        interactiveMode();
                        System.out.println("Завершение программы.");
                    }
                } catch (IOException e) {
                        System.err.println("Нет связи с сервером. Подключться ещё раз (введите {yes} или {no})?");
                        String answer;
                        while (!(answer = fromKeyboard.nextLine()).equals("yes")) {
                            switch (answer) {
                                case "yes":
                                    break;
                                case "no":
                                    exit();
                                    break;
                                default:
                                    System.out.println("Введите корректный ответ.");
                            }
                        }
                        System.out.print("Подключение ...");
                        continue;
                    }
                }
            }
        }

    /**
     * Парсит пользовательские команды и осуществляет обмен данными с сервером.
     * @throws IOException при отправке и получении данных с сервера.
     */
    private void interactiveMode() throws IOException {
        try {
            System.out.println((String) fromServer.readObject());
            String command;
            try {
                while (!(command = fromKeyboard.nextLine()).equals("exit")) {
                    while ((command.length() - command.replaceAll("}", "").length()) < (command.length() - command.replaceAll("\\{", "").length())){
                        command = command + fromKeyboard.nextLine().trim();
                    }
                    String[] parsedCommand = command.trim().split(" ");
                    switch (parsedCommand[0]) {
                        case "":
                            break;
                        case "import":
                            try {
                                if (parsedCommand.length > 3) {
                                    toServer.writeObject(String.join(" ",
                                            parsedCommand[0], // import
                                            "'" + importingFile(parsedCommand[1]) + "'", // file
                                            parsedCommand[2], // login
                                            parsedCommand[3] // password
                                    ));
                                    System.out.println((String) fromServer.readObject());
                                } else {
                                    System.out.println("Неправильно указаны параметры к команде import");
                                }
                            } catch (FileNotFoundException e) {
                                System.out.println("Файл по указанному пути не найден: " + parsedCommand[1]);
                            } catch (SecurityException e) {
                                System.out.println("Файл защищён от чтения.");
                            } catch (IOException e) {
                                System.out.println("Что-то не так с файлом.");
                            }
                            break;
                        default:
                            toServer.writeObject(command);
                            System.out.println((String) fromServer.readObject());
                    }
                }
                exit();
            } catch (NoSuchElementException ex) {
                exit();
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Класс не найден: " + e.getMessage());
        }
    }
/*
    /**
     * Создает объект Room из строки
     * @param arg - объект в формате json
     * @return объект Room
     */
/*
    private Room makeObjectRoom(String arg) {
        Room room;
        try {
            String jsonRoom = arg.replaceAll("  ", " ").trim();
            Gson gson = new Gson();
            room = gson.fromJson(jsonRoom, Room.class);
            return room;
        } catch (JsonSyntaxException ex) {
            System.out.println("Синтаксическая ошибка JSON. Не удалось удалить элемент.");
        }
        return null;
    }
*/
    /**
     * Импортирует локальный файл и отправляет на сервер.
     * @param path путь к файлу.
     * @return команду для сервера и содержимое файла.
     * @throws SecurityException если отсутствуют права rw.
     * @throws IOException если файл не существует.
     */
    private String importingFile(String path) throws SecurityException, IOException {
        File localCollection = new File(path);
        String extension = localCollection.getAbsolutePath().substring(localCollection.getAbsolutePath().lastIndexOf(".") + 1);
        if (!localCollection.exists() | localCollection.length() == 0  | !extension.equals("json"))
            throw new FileNotFoundException();
        if (!localCollection.canRead()) throw new SecurityException();
        try (BufferedReader inputStreamReader = new BufferedReader(new FileReader(localCollection))) {
            String nextLine;
            StringBuilder result = new StringBuilder();
            while ((nextLine = inputStreamReader.readLine()) != null) result.append(nextLine);
            return result.toString();
        }
    }

    /**
    Отвечает за завершение работу клиентского приложения.
     */
    private void exit() {
        System.out.println("Завершение программы.");
        System.exit(0);
    }
}
