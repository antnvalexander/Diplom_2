package utils;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;

public class DataGenerator {

    private DataGenerator() {
    }

    public static String generateName(String locale) {
        return new Faker(new Locale(locale)).name().name();
    }
    public static String generatePassword(String locale) {
        return new Faker(new Locale(locale)).internet().password();
    }
    public static String generateEmail(String locale) {
        return new Faker(new Locale(locale)).internet().emailAddress();
    }


    @Value
    public static class FakeUser {
        String name;
        String password;
        String email;
    }

    public static FakeUser generateUser(String locale) {
        return new FakeUser(generateName(locale), generatePassword(locale),generateEmail(locale));
    }
}
