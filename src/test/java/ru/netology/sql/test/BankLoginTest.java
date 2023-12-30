package ru.netology.sql.test;

import org.junit.jupiter.api.*;
import ru.netology.sql.page.LoginPage;

import static com.codeborne.selenide.Selenide.*;
import static ru.netology.sql.data.DataHelper.*;
import static ru.netology.sql.data.SQLHelper.*;

public class BankLoginTest {
    LoginPage loginPage;


    @AfterEach
    void cleanCode() {
        cleanAuth_code();
    }

    @AfterAll
    static void cleanAll() {
        cleanDatabase();
    }

    @BeforeEach
    void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    @DisplayName("Should successfully login with registered user and right code")
    void shouldPositiveLogin() {
        var verificationPage = loginPage.validLogin(getAuthData());
        verificationPage.verificationPageIsVisible();
        verificationPage.validVerify(getVerificationCode());
    }

    @Test
    @DisplayName("Should get error when invalid login")
    void shouldNotLoginWithInvalidLogin() {
        loginPage.validLogin(new AuthData(testName, generatePassword()));
        loginPage.findErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error when invalid password")
    void shouldNotLoginWithInvalidPassword() {
        loginPage.validLogin(new AuthData(generateLogin(), testPassword));
        loginPage.findErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error when invalid login and password")
    void shouldNotLoginWithInvalidLoginAndPassword() {
        loginPage.validLogin(generateUser());
        loginPage.findErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error when invalid verificationCode")
    void shouldNotLoginWithInvalidCode() {
        var verificationPage = loginPage.validLogin(getAuthData());
        verificationPage.verify(generateCode());
        verificationPage.findErrorMessage("Неверно указан код!");
    }

    @Test
    @DisplayName("Should blocked user when invalid password more 3 times enter")
    void shouldBlockedWhenInvalidPasswordEnterMoreTimes() {
        for (int i = 0; i < 3; i++) {
            refresh();
            loginPage.validLogin(new AuthData(testName, generatePassword()));
            loginPage.findErrorMessage("Неверно указан логин или пароль");
        }
        String expected = "blocked";
        String actual = getStatus();
        Assertions.assertEquals(expected, actual);
    }

}