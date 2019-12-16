import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.Keys;
import util.CityGenerator;
import util.DataGeneratorValidInfo;
import util.RequestData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

class CardDeliveryOrderTests {
    private static final String url = "http://localhost:9999/";
    private static final String validDayOfMeeting = LocalDate.now().plusDays(3)
            .format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    private static final String anotherValidDayOfMeeting = LocalDate.now().plusDays(5)
            .format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    private static final String anotherValidDayOfMeetingForTestFormat = LocalDate.now().plusDays(5)
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    private static final String invalidDayOfMeeting = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("ddMMyyyy"));
    private static final String sendRequestButtonName = "Запланировать";
    private static final String invalidNameErrorText = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, " +
            "пробелы и дефисы.";
    private static final String invalidTelErrorText = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678";
    private static final String invalidCityErrorText = "Доставка в выбранный город недоступна";
    private static final String invalidDateErrorText = "Заказ на выбранную дату невозможен";
    private static final String nullFieldErrorText = "Поле обязательно для заполнения";
    private static final String invalidTypeOfDateErrorText = "Неверно введена дата";


    @Test
    void shouldSubmitRequest() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='success-notification']").waitUntil(Condition.visible, 15000);
    }

    @CsvFileSource(resources = "/invalidnames.csv", numLinesToSkip = 1)
    @ParameterizedTest
    void shouldDeclineRequestForInvalidName(String invalidName) {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);

        $("[name='name']").setValue(invalidName);
        $("[name='phone']").setValue(requestData.getTel());
        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='name'] .input__sub").shouldHave(Condition
                .exactText(invalidNameErrorText));
    }

    @CsvFileSource(resources = "/invalidtel.csv", numLinesToSkip = 1)
    @ParameterizedTest
    void shouldDeclineRequestForInvalidTel(String invalidTel) {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(invalidTel);

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(Condition
                .exactText(invalidTelErrorText));
    }

    @Test
    void shouldDeclineRequestForInvalidCity() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue("Сосновый Бор");
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='city'] .input__sub").shouldHave(Condition
                .exactText(invalidCityErrorText));
    }

    @Test
    void shouldDeclineRequestForInvalidDate() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(invalidDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $(new Selectors.ByText("Заказ на выбранную дату невозможен")).shouldHave(Condition
                .exactText(invalidDateErrorText));
    }

    @Test
    void shouldDeclineRequestForInvalidTypeOfDate() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(LocalDate.now().toString().replaceAll("-", ""));
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $(new Selectors.ByText("Неверно введена дата")).shouldHave(Condition
                .exactText(invalidTypeOfDateErrorText));
    }

    @Test
    void shouldDeclineRequestForNotCheckedCheckbox() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(new Selectors.ByText(sendRequestButtonName)).click();
        final String colorRedRGB = "rgba(255, 92, 92, 1)";
        String actualColor = $("[data-test-id='agreement'] .checkbox__text")
                .getCssValue("color");
        assertEquals(colorRedRGB, actualColor);
    }

    @Test
    void shouldDeclineRequestForNullCity() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(null);
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='city'] .input__sub").shouldHave(Condition
                .exactText(nullFieldErrorText));
    }

    @Test
    void shouldDeclineRequestForNullName() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(null);
        tel.setValue(requestData.getTel());

        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='name'] .input__sub").shouldHave(Condition
                .exactText(nullFieldErrorText));
    }

    @Test
    void shouldDeclineRequestForNullDate() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(null);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $(new Selectors.ByText(invalidTypeOfDateErrorText))
                .shouldHave(Condition.exactText(invalidTypeOfDateErrorText));
    }

    @Test
    void shouldDeclineRequestForNullTel() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(null);

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='phone'] .input__sub").shouldHave(Condition
                .exactText(nullFieldErrorText));
    }

    @Test
    void shouldSubmitRequestWhenCityChosenFromAppearedList() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue("Аб");
        $(".menu-item__control").click();
        date.setValue("\b\b\b\b\b\b\b\b\b\b");
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='success-notification']").waitUntil(Condition.visible, 15000);
    }

    @Test
    void shouldSubmitChangeDateOfMeeting() {
        RequestData requestData = DataGeneratorValidInfo.generateUsersData(new Locale("ru"));
        open(url);
        //Finding elements
        SelenideElement name = $("[name='name']");
        SelenideElement city = $("[placeholder = 'Город']");
        SelenideElement tel = $("[name='phone']");
        SelenideElement date = $("[placeholder = 'Дата встречи']");
        //Setting values
        city.setValue(CityGenerator.getRandomCity());
        date.setValue(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        date.setValue(validDayOfMeeting);
        name.setValue(requestData.getName());
        tel.setValue(requestData.getTel());

        $(".checkbox__box").click();
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $("[data-test-id='success-notification']").waitUntil(Condition.visible, 15000);
        date.setValue("\b\b\b\b\b\b\b\b\b\b"); //пробывал date.setValue(Keys.chord(Keys.CONTROL, "a", Keys.DELETE)); -
        // не работает причины не понял
        date.setValue(anotherValidDayOfMeeting);
        $(new Selectors.ByText(sendRequestButtonName)).click();
        $(new Selectors.ByText("Перепланировать")).click();
        $(".notification__content").shouldHave(Condition
                .text("Встреча успешно запланирована на " + anotherValidDayOfMeetingForTestFormat));
        System.out.println(validDayOfMeeting);
        System.out.println(anotherValidDayOfMeeting);
        System.out.println(anotherValidDayOfMeetingForTestFormat);
    }
}
