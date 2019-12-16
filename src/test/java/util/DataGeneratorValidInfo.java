package util;

import com.github.javafaker.Faker;

import java.util.Locale;

public class DataGeneratorValidInfo {

    public static RequestData generateUsersData(Locale locale){

        Faker faker = new Faker(new Locale(locale.toLanguageTag()));

        return new RequestData(faker.name().fullName(), faker.phoneNumber().phoneNumber());
    }

}
