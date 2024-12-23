
import dev.failsafe.internal.util.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class WebScraping {

    static WebDriver driver = null;
    static WebDriverWait wait;
    static Properties properties;

    public static void main(String[] args) {
        // Initialize Driver
        driverInitialization("Chrome");

        // Invoke browser and navigate to website
        navigateToWebsite();

        // Assert default language is Spanish
        boolean flag = false;
        if (flag == verifyLanguage()){
            System.out.println("Default language is Spanish");
        } else {
            System.out.println("Default language is not Spanish");
            closeBrowser();
        }

        closeBrowser();
    }

    static void driverInitialization(String browserName){
        String propertyDir = System.getProperty("user.dir") + File.separator + "Browser.properties";
        try {
            properties = new Properties();
            properties.load(new FileInputStream(propertyDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String browser = properties.getProperty(browserName);
        switch (browser){
            case "Chrome":
                System.setProperty("webdriver.chrome.driver",
                        System.getProperty("user.dir") + File.separator + "Drivers" + File.separator + "chromedriver");

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-gpu");
                options.addArguments("--remote-allow-origins=*");

                driver = new ChromeDriver(options);
                break;
            case "Firefox":
                System.setProperty("webdriver.gecko.driver",
                        System.getProperty("user.dir") + File.separator + "Drivers" + File.separator + "geckodriver");
                driver = new FirefoxDriver();
                break;
            case "Edge":
                System.setProperty("webdriver.edge.driver",
                        System.getProperty("user.dir") + File.separator + "Drivers" + File.separator + "msedgedriver");
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        System.out.println("WebDriver initialized for: " + browser);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    static void navigateToWebsite(){
        String url = properties.getProperty("URL");
        driver.navigate().to(url);
        driver.manage().window().maximize();
        System.out.println("Navigated to: " + url);
    }

    static boolean verifyLanguage(){
        // TC1: Ensure that the website's text is displayed in Spanish.
        WebElement languageEle = driver.findElement
                (By.xpath("//time[contains(@id, 'header')]/parent::div/div //ul/li[1] //span"));
        if (Objects.equals(languageEle.getText(), System.getProperty("Spanish"))){
            return true;
        }
        return false;
    }

    static void scrapeArticles(){
        /*
        * Scrape Articles from the Opinion Section:
        Navigate to the Opinion section of the website.
        Fetch the first five articles in this section.
        Print the title and content of each article in Spanish.
        If available, download and save the cover image of each article to your local machine.
        * */

        List<WebElement> opinionOpt = driver.findElements
                (By.xpath("//div[@id='csw']/div[1] //div/a"));
        for (WebElement e : opinionOpt){
            if (e.getText().equalsIgnoreCase(System.getProperty("OpinionSection"))){
                e.click();
                break;
            }
        }

        List<WebElement> articles = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] //article"));


    }

    static void closeBrowser(){
        driver.quit();
    }

}
