package server;

import sun.security.provider.MD5;
import tale.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserSessionManager {

    private static UserSessionManager instance;

    private Map<String, User> sessions;

    private UserSessionManager() {
        sessions = new HashMap<>();
    }

    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    public User getUser(String sessionId) {
        return sessions.get(sessionId);
    }

    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }
}
