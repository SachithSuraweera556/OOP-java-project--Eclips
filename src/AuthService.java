public class AuthService {
    // Stores the username of the currently logged-in user for the session
    public static String currentUser = null;

    // Authenticate User (Login) - HARDCODED "admin" / "admin"
    public static boolean authenticate(String username, String password) {
        // Hardcoded check: User must be "admin" and password "admin"
        if ("admin".equals(username) && "admin".equals(password)) {
            currentUser = "admin"; // Set session
            return true;
        }
        return false;
    }
}