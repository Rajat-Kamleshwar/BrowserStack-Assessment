import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MWebUtil {
    public Properties properties;

    public void DevTools(ChromeOptions chromeOptions){
        String propertyDir = System.getProperty("user.dir") + File.separator + "MobileWeb.properties";
        try {
            properties = new Properties();
            properties.load(new FileInputStream(propertyDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Specify the device
        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", System.getProperty("DeviceName"));
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
    }

    public WebDriver BrowserStackConfig(){
        WebDriver driver = null;
//        try {
//            // Get the environment (mobile/desktop) from config
//            String environment = "mobile"; // This could also come from a command-line argument or system property
//            Map<String, String> envConfig = YamlReader.getEnvironment(environment);
//
//            // Set up DesiredCapabilities
//            DesiredCapabilities caps = new DesiredCapabilities();
//            caps.setCapability("browserstack.user", YamlReader.getBrowserStackUsername());
//            caps.setCapability("browserstack.key", YamlReader.getBrowserStackAccessKey());
//            caps.setCapability("os", envConfig.get("os"));
//            caps.setCapability("os_version", envConfig.get("osVersion"));
//            caps.setCapability("browser", envConfig.get("browserName"));
//            caps.setCapability("browser_version", envConfig.get("browserVersion"));
//            caps.setCapability("device", envConfig.get("deviceName"));
//
//            driver = new RemoteWebDriver(new URL("https://rajatkamleshwar_D20P7i:wPz6AqoqTxQaCp7jAkP1@hub" +
//                    "-cloud" +
//                ".browserstack.com/wd/hub"), caps);
//        } catch (MalformedURLException m){
//            System.out.println(m.getMessage());
//        }
        return driver;
    }
}
