import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.WatchEvent;
import java.time.Duration;
import java.util.ArrayList;

public class TestAuto {
    private final static Logger logger = LogManager.getLogger(TestAuto.class);
    private WebDriver webDriver;

    String fname = "Имя";
    String fname_latin = "Name";
    String lname = "Фамилия";
    String lname_latin = "LastName";
    String blog_name = "BlogName";
    String date_of_birth = "25.01.2000";
    String vkText = "vkText";
    String okText = "okText";

    @BeforeAll
    public static void init() {
        logger.info("======================================================");
        logger.info("Скачивание WebDriverManager - начато");
        WebDriverManager.chromedriver().setup();
        logger.info("Скачивание WebDriverManager - завершено");
        logger.info("======================================================");
    }

    @AfterEach
    public void close() {
        logger.info("======================================================");
        logger.info("Закрытие браузера - начато");
        if (webDriver != null) {
            webDriver.quit();
        }
        logger.info("Закрытие браузера - завершено");
        logger.info("======================================================");
    }

    @Test
    public void editProfile() throws InterruptedException {
        logger.info("======================================================");
        webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        auth();
        new WebDriverWait(webDriver, Duration.ofSeconds(180)).
                until(ExpectedConditions.
                        visibilityOfElementLocated(By.cssSelector("section div.sc-1og4wiw-0-Component:nth-child(2) span")));
        logger.info("Переходим в профиль");
        webDriver.get("https://otus.ru/lk/biography/personal/");
        logger.info("Вводим данные в поля");
        enterTextByCss(webDriver.findElement(By.cssSelector("input[name='fname']")), fname);
        enterTextByCss(webDriver.findElement(By.cssSelector("input[name='fname_latin']")), fname_latin);
        enterTextByCss(webDriver.findElement(By.cssSelector("input[name='lname']")), lname);
        enterTextByCss(webDriver.findElement(By.cssSelector("input[name='lname_latin']")), lname_latin);
        enterTextByCss(webDriver.findElement(By.cssSelector("input[name='blog_name']")), blog_name);
        enterTextByCss(webDriver.findElement(By.cssSelector("input[name='date_of_birth']")), date_of_birth);
        scrollToElement(webDriver, webDriver.findElement(By.cssSelector("input[class='input input_full'][placeholder='Телефон']")));
        if (!isElementExists((By.xpath("//input[@value='ok']/../../following-sibling::input")))) {
            webDriver.
                    findElement(By.
                            cssSelector("button[class='lk-cv-block__action lk-cv-block__action_md-no-spacing js-formset-add js-lk-cv-custom-select-add']")).click();

            webDriver.findElement(By.cssSelector("span[class='placeholder']")).click();
            webDriver.findElements(By.cssSelector("button[data-value='ok']")).get(0).click();
            enterTextByCss(webDriver.findElement(By.cssSelector("input[id='id_contact-0-value']")), okText);
        } else {
            enterTextByCss(webDriver.findElement(By.cssSelector("input[id='id_contact-0-value']")), okText);
        }

        if (!isElementExists((By.xpath("//input[@value='vk']/../../following-sibling::input")))) {
            scrollToElement(webDriver, webDriver.findElement(By.cssSelector("input[id='id_contact-0-value']")));
            webDriver.
                    findElement(By.
                            cssSelector("button[class='lk-cv-block__action lk-cv-block__action_md-no-spacing js-formset-add js-lk-cv-custom-select-add']")).click();
            webDriver.findElement(By.cssSelector("span[class='placeholder']")).click();
            webDriver.findElements(By.cssSelector("button[data-value='vk'].lk-cv-block__select-option")).get(1).click();
            enterTextByCss(webDriver.findElement(By.cssSelector("input[id='id_contact-1-value']")), vkText);
        } else {
            enterTextByCss(webDriver.findElement(By.cssSelector("input[id='id_contact-1-value']")), vkText);
        }

        logger.info("Сохраняем все");
        webDriver.findElement(By.cssSelector("button[title='Сохранить и продолжить']")).click();
        logger.info("Закрываем браузер");
        webDriver.close();
        WebDriverManager.chromedriver().setup();
        webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        auth();
        new WebDriverWait(webDriver, Duration.ofSeconds(180)).
                until(ExpectedConditions.visibilityOfElementLocated(By.
                        cssSelector("section div.sc-1og4wiw-0-Component:nth-child(2) span")));
        logger.info("Переходим в профиль");
        webDriver.get("https://otus.ru/lk/biography/personal/");
        logger.info("Проверяем введенные данные");

        Assertions.assertEquals(fname, webDriver.findElement(By.cssSelector("input[name='fname']")).getAttribute("value"));
        Assertions.assertEquals(fname_latin, webDriver.findElement(By.cssSelector("input[name='fname_latin']")).getAttribute("value"));
        Assertions.assertEquals(lname, webDriver.findElement(By.cssSelector("input[name='lname']")).getAttribute("value"));
        Assertions.assertEquals(lname_latin, webDriver.findElement(By.cssSelector("input[name='lname_latin']")).getAttribute("value"));
        Assertions.assertEquals(blog_name, webDriver.findElement(By.cssSelector("input[name='blog_name']")).getAttribute("value"));
        Assertions.assertEquals(date_of_birth, webDriver.findElement(By.cssSelector("input[name='date_of_birth']")).getAttribute("value"));
        Assertions.assertEquals(okText, webDriver.findElement(By.xpath("//input[@value='ok']/../../following-sibling::input")).getAttribute("value"));
        Assertions.assertEquals(vkText, webDriver.findElement(By.xpath("//input[@value='vk']/../../following-sibling::input")).getAttribute("value"));
    }

    public void auth() {
        logger.info("Открытие сайта http://otus.ru в браузере- начато");
        webDriver.get("http://otus.ru");
        logger.info("Клик на Войти");
        String btnEnter = "//button[text()='Войти']";
        WebElement button = webDriver.findElement(new By.ByXPath(btnEnter));
        button.click();
        logger.info("Ввод почты");
        webDriver.findElement(new By.ByXPath("//div[./input[@name='email']]")).click();
        webDriver.findElement(new By.ByXPath("//input[@name='email']")).sendKeys(System.getProperty("email"));
        logger.info("Ввод пароля");
        webDriver.findElement(new By.ByXPath("//div[./input[@type='password']]")).click();
        webDriver.findElement(new By.ByXPath("//input[@type='password']")).sendKeys(System.getProperty("pass"));
        logger.info("Клик на Войти");
        WebElement enter = webDriver.findElement(new By.ByXPath("//button[./*[text()='Войти']]"));
        enter.click();
    }

    public static void scrollToElement(WebDriver webDriver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void enterTextByCss(WebElement webElement, String text) {
        webElement.clear();
        webElement.sendKeys(text);
    }

    public boolean isElementExists(By by) {
        boolean isExists = true;
        try {
            webDriver.findElement(by);
        } catch (NoSuchElementException e) {
            isExists = false;
        }
        return isExists;
    }
}
