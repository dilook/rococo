package guru.qa.rococo.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomMuseumName() {
        return faker.letterify("??????????");
    }

    @Nonnull
    public static String randomCityName() {
        return faker.address().city();
    }

    @Nonnull
    public static String randomSentence(int characterCount) {
        return faker.lorem().fixedString(characterCount);
    }
}
