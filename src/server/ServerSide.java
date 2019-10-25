package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerSide {


    private static StorageManager manager;
    private static int port;
    private static Scanner fromKeyboard;

    /**
     * Точка входа в программу. Управляет подключением к клиентам и созданием потоков для каждого из них.
     * @param args массив по умолчанию в основном методе. Не используется.
     */
    public static void main(String[] args) {
        File file = new File("serverCollection.json");
        try (Scanner scanner = new Scanner(System.in)) {
            fromKeyboard = scanner;
            String command;

            boolean flag1 = true;
            boolean flag2 = true;
            System.out.println("Команды:");
            System.out.println("1. connect урл_БД порт");
            System.out.println("    где: урл_БД - строка dev для локального сервера или урл в формате JDBC jdbc:postgresql://host:port/database");
            System.out.println("2. connect_file путь_к_файлу порт");
            try {
                while (flag1 | flag2) {
                    flag1 = true;
                    flag2 = true;
                    port = -1;
                    command = fromKeyboard.nextLine();
                    String[] parsedCommand = command.trim().split(" ", 2);
                    if (parsedCommand.length > 1) {
                        String[] arguments = parsedCommand[1].trim().split(" ", 2);
                        switch (parsedCommand[0]) {
                            case "connect":
                                if (arguments.length >= 2) {
                                    String conn_param = arguments[0];
                                    switch (arguments[0]) {
                                        case "dev":
                                            conn_param = "jdbc:postgresql://127.0.0.1/studs";
                                            break;
                                        case "prod":
                                            conn_param = "jdbc:postgresql://pg.cs.ifmo.ru/studs";
                                            break;
                                    }
                                    flag1 = false;
                                    flag2 = parsePort(flag2, arguments[1]);
                                    if (!(flag1 && flag2)) {
                                        try {
                                            manager = new DbStorageManager(conn_param, "s265081", "ppa027");
                                        } catch (SQLException e) {
                                            System.out.println("Не удалось подключиться к БД: " + e);
                                            flag1 = true;
                                        }
                                    }
                                } else {
                                    System.out.println("Не все аргументы введены.");
                                }
                                break;
                            case "connect_file":
                                if (arguments.length > 1) {
                                    flag2 = parsePort(flag2, arguments[1]);
                                    file = new File(arguments[0]);
                                    try {
                                        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
                                        if (!file.exists() | !extension.equals("json"))
                                            throw new FileNotFoundException();
                                        if (file.length() == 0) throw new Exception("Файл пуст");
                                        if (!file.canRead() || !file.canWrite()) throw new SecurityException();
                                        else {
                                            flag1 = false;
                                            manager = new FileStorageManager(file);
                                        }

                                    } catch (FileNotFoundException e) {
                                        System.err.println("Файл по указанному пути не найден.");
                                    } catch (SecurityException e) {
                                        System.err.println("Файл защищён от чтения.");
                                    } catch (IOException e) {
                                        System.err.println("Что-то не так с файлом.");
                                    } catch (Exception e) {
                                        System.err.println("Файл пуст");
                                    }
                                } else {
                                    System.out.println("Не все аргументы введены.");
                                }
                                break;
                            default:
                                System.err.println("Ошибка, неизвестная команда.");
                        }

                    } else {
                        System.out.println("Не введены аргументы.");
                    }
                }
            } catch (NoSuchElementException ex) {
                System.exit(1);
            }
        }
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.print("Сервер начал слушать клиентов. " + "\nПорт " + server.getLocalPort() +
                        " / Адрес " + InetAddress.getLocalHost() + ".\nОжидаем подключения клиентов ");
                Thread pointer = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        System.out.print(".");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.print("\n");
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                pointer.setDaemon(true);
                pointer.start();
                while (true) {
                    Socket incoming = server.accept();
                    pointer.interrupt();
                    System.out.println("\n" + incoming + " подключился к серверу.");
                    Runnable r = new ServerConnection(manager, incoming);
                    Thread t = new Thread(r);
                    t.start();
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            } catch (NumberFormatException e) {
                System.out.println("Номер порта задан неверно");
                System.exit(1);
            }
    }

    private static boolean parsePort(boolean flag2, String argument) {
        try {
            if (Integer.parseInt(argument) < 0 || Integer.parseInt(argument) > 65535)
                System.err.println("Порт сервера должен быть целым числом от 0 до 65535.");
            else {
                port = Integer.parseInt(argument);
                System.out.println("Задан порт сервера: " + port);
                flag2 = false;
            }
        } catch (NumberFormatException e) {
            System.err.println("Номер порта задан неверно.");
        }
        return flag2;
    }
}
