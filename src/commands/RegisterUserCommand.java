package commands;

import server.MailSender;
import server.StorageManager;
import tale.User;

import java.sql.SQLException;

/**
 * Класс переопределяет метод execute () для добавления пользователя.
 */
public class RegisterUserCommand extends AbstractCommand {
    public static final String NAME = "register";

    public RegisterUserCommand(StorageManager manager) {
        super(manager);
        setDescription("Регистрирует нового пользователя, генерирует ему пароль и высылает его на почту.\n"
                + NAME + " {String email}");
    }

    @Override
    public synchronized String execute(String[] args) {
        String email;
        if (args.length > 1) {
            email = args[1];
        } else {
            return "Не задан почтовый адрес пользователя.\nИспользование:\n" + getDescription();
        }
        String password = User.generatePassword(1, 1, 1, 1);
        User user = new User(email, password);
        try {
            if (getManager().add(user)) {
                    getManager().save();
                MailSender.getInstance().sendMail(email,
                        "Ваш пароль для входа на сервер",
                        "Вот ваш пароль - " + password);
            } else {
                return "Логическая ошибка при создании пользователя";
            }
        } catch (SQLException e) {
            System.out.println("SQL error = " + e);
            return "Ошибка SQL: " + e.getCause();
        }
        return "Пользователь зарегистрирован, пароль выслан по почте, но, на всякий случай, приводим пароль здесь: "
                + password;
    }
}
