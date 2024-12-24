import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class DriverManagerUtil {

    public static WebDriver driver = null;
    public static WebDriverWait wait;
    public static Properties properties;

    public static WebDriver driverInitialization(String browserName){
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

                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--disable-blink-features=AutomationControlled");
                edgeOptions.setExperimentalOption("useAutomationExtension", false);

                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        System.out.println("WebDriver initialized for: " + browser);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return driver;
    }
}
