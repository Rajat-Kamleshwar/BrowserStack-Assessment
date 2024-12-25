import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
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

    public static WebDriver driverInitialization(String browserName, String platform){
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

                if (platform.equalsIgnoreCase("mWEB")){
                    MWebUtil mWebUtil = new MWebUtil();
                    mWebUtil.DevTools(options);
                    mWebUtil.BrowserStackConfig();
                } else {
                    options.addArguments("--disable-extensions");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--remote-allow-origins=*");
                }

                driver = new ChromeDriver(options);
                System.out.println("*************************Chrome Browser Invoked!*************************");
                break;
            case "Firefox":
                System.setProperty("webdriver.gecko.driver",
                        System.getProperty("user.dir") + File.separator + "Drivers" + File.separator + "geckodriver");

                driver = new FirefoxDriver();
                System.out.println("*************************Firefox Browser Invoked!*************************");
                break;
            case "Edge":
                System.setProperty("webdriver.edge.driver",
                        System.getProperty("user.dir") + File.separator + "Drivers" + File.separator + "msedgedriver");

                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--disable-blink-features=AutomationControlled");
                edgeOptions.setExperimentalOption("useAutomationExtension", false);

                driver = new EdgeDriver(edgeOptions);
                System.out.println("*************************Edge Browser Invoked!*************************");
                break;
            case "Safari":
                driver = new SafariDriver();
                System.out.println("*************************Safari Browser Invoked!*************************");
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        System.out.println("WebDriver initialized for: " + browser);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));  // Applicable throughout the execution
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        return driver;
    }
}
