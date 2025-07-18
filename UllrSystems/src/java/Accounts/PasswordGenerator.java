package Accounts;
import java.security.SecureRandom;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordGenerator {
    public String getNewPassword() {
        return GeneratePassword(3, 3, 3);// can change the type of passwords being created
    }

    private String GeneratePassword(int numUpperCase, int numLowerCase, int numDigits) {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789"; // all possible letters in a password

        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < numUpperCase; i++) {
            char upper = upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length()));
            password.append(upper); // random uppercase letter x 3
        }
        for (int i = 0; i < numLowerCase; i++) {
            char lower = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
            password.append(lower); // random lowercase letter x 3
        }
        for (int i = 0; i < numDigits; i++) {
            char digit = digits.charAt(random.nextInt(digits.length()));
            password.append(digit); // random digits x 3
        }
        String rawPassword = password.toString();// converts from string builder to string
        return rawPassword; // encodes the password and return the hashed password
    }
}