package client;

public class ClientSide {
    /**
     * Отправная точка клиента. Создает объект ClientConnection.
     * @param args массив по умолчанию в основном методе. Не используется.
     */
    public static void main(String[] args) {
        System.out.println("Запуск клиентского модуля.\nПодключение к серверу ...");
        ClientConnection connection = new ClientConnection();
        connection.work();
    }
}
