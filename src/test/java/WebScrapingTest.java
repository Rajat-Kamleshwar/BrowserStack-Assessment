import org.openqa.selenium.*;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;
import java.util.NoSuchElementException;

import static org.openqa.selenium.remote.Browser.SAFARI;

public class WebScrapingTest extends DriverManagerUtil {

    ImageSaveUtil imageSaveUtil;
    RestAssuredUtil restAssuredUtil;
    List<String> headersToTranslate;
    List<String> translatedHeaders;
    String url, browser = "Chrome", platform = "WEB";


    @BeforeClass
    public void setup(){
        // Initialize Driver
        driver = driverInitialization(browser, platform);    // BrowserName: For Local Execution
        url = properties.getProperty("URL");
        driver.navigate().to(url);
        driver.manage().window().maximize();
        System.out.println("Navigated to: " + url);
    }

    @Test(priority = 1, description = "Visit the website El País, a Spanish news outlet.")
    public void navigateToWebsite(){
        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(properties.getProperty("URL")));
        WebElement acceptBtn;
        boolean elementFound = false;
        // Adding explicit sleep wait here, wait until website notice is displayed and accept the notice
        try {
            Thread.sleep(10000);
        } catch (InterruptedException i){
            System.out.println(i.getMessage());
        }
        List<WebElement> noticeLocator1 = driver.findElements(By.xpath("//div[@class='pmConsentWall-content']"));
        List<WebElement> noticeLocator2 = driver.findElements(By.xpath("//div[@data-testid = 'notice']"));
        // Check if the following component is visible within Mobile View or not, if not proceed with existing logic,
        // adding this additional logic because the notice elements are different in Browser mWeb and BrowserStack
        // mWeb. Hence, handled both element conditions to continue script execution with whatever element is displayed

        try {
            if (!noticeLocator1.isEmpty() && noticeLocator1.get(0).isDisplayed() && noticeLocator1.get(0).isEnabled()){
                acceptBtn = driver.findElement(By.xpath("//div[@class='pmConsentWall-col'][1] //div/following-sibling::a"));
                wait.until(ExpectedConditions.elementToBeClickable(acceptBtn));
                acceptBtn.click();
                elementFound = true;
                System.out.println("Clicked on m-WEB Notice!");
            }
        } catch (NoSuchElementException nse) {
            nse.printStackTrace();
        }
        if (!elementFound) {
            try {
                if (!noticeLocator2.isEmpty() && noticeLocator2.get(0).isDisplayed() && noticeLocator2.get(0).isEnabled()) {
                    acceptBtn = driver.findElement(By.xpath("//button[@id='didomi-notice-agree-button']"));
                    wait.until(ExpectedConditions.elementToBeClickable(acceptBtn));
                    acceptBtn.click();
                    elementFound = true;
                    System.out.println("Clicked on WEB Notice!");
                }
            } catch (NoSuchElementException nse1) {
                System.out.println(nse1.getMessage());
            }
        }
        if (!elementFound) {
            System.out.println("Neither mWEB nor WEB notice element is displayed!");
        }
    }

    @Test(priority = 2,
            description = "Ensure that the website's text is displayed in Spanish.",
            dependsOnMethods = "navigateToWebsite")
    public void verifyLanguage(){
        WebElement languageEle = null;

            // Click on Hamburger menu and proceed with validations

            WebElement hamburgerMenu = driver.findElement(By.xpath("//button[@id='btn_open_hamburger']"));
            try {
                wait.until(ExpectedConditions.elementToBeClickable(hamburgerMenu));
                hamburgerMenu.click();
            } catch (NoSuchElementException ne){
                throw new NoSuchElementException(ne.getMessage());
            }

        // Language Text
            languageEle = driver.findElement(By.xpath
                    ("//div[contains(@data-dtm-region, 'header_hamburguesa_edicion')] //ul/li/a[@href='https://elpais.com']"));

        // Assert default language is Spanish
        boolean flag = false;
        if (flag == Objects.equals(languageEle.getText(), System.getProperty("Spanish"))){
            System.out.println("Default language is Spanish");
        } else {
            System.out.println("Default language is not Spanish");
            closeBrowser();
        }
    }

    @Test(priority = 3,
            dependsOnMethods = "verifyLanguage",
            description = "Scrape Articles from the Opinion Section")
    public void scrapeArticles(){
        /*
            Scrape Articles from the Opinion Section:
            Navigate to the Opinion section of the website.
            Fetch the first five articles in this section.
            Print the title and content of each article in Spanish.
            If available, download and save the cover image of each article to your local machine.
        */

        List<WebElement> opinionOpt;
        // Choose the Opinion option from hamburger menu and click on it
        opinionOpt = driver.findElements
                    (By.xpath("//div[@id='hamburger_container'] //nav/div[1]/ul/li/a"));
        for (WebElement e : opinionOpt){
            if (e.getText().equalsIgnoreCase(properties.getProperty("OpinionSection"))){
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(e));
                    e.click();
                    wait.until(ExpectedConditions.urlToBe(url + "opinion/"));
                } catch (ElementNotInteractableException excp){
                    throw new ElementNotInteractableException(excp.getMessage());
                }
                break;
            }
        }

        System.out.println("Opinion option clicked:\n");

        // Explicit timeout for Safari Browser as DOM loading is often delayed and unreliable
        if (browser.equalsIgnoreCase("Safari")){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException i){
                System.out.println(i.getMessage());
            }
        }

        // Wait until articles are visible on page
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(
                    driver.findElements
                            (By.xpath
                                    ("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] //article"))));
        } catch (NoSuchElementException e){
            throw new NoSuchElementException(e.getMessage());
        }

        List<WebElement> articleHeader = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] " +
                        "//article/header/h2"));
        List<WebElement> articleContent = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] " +
                        "//article/p"));
        List<WebElement> articleImage = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] " +
                        "//article/figure //a[contains(@href, '" + url + "')]/img"));

        System.out.println("Articles scraped from Opinion section!:\n");

        imageSaveUtil = new ImageSaveUtil();
        imageSaveUtil.emptyDirectory();
        headersToTranslate = new ArrayList<>();

        int top5 = 0;
            while (top5 < 5){
                // Print the Article Header
                System.out.println("Article Headline:\t" + articleHeader.get(top5).getText());
                headersToTranslate.add(articleHeader.get(top5).getText());
                // Print the Article Content
                System.out.println("Article Content:\t" + articleContent.get(top5).getText());
                // Save article image if any
                try {
                    wait.until(ExpectedConditions.visibilityOfAllElements(articleImage));
                    imageSaveUtil.saveImageFromWebsite(articleImage);
                } catch (StaleElementReferenceException s){
                    throw new StaleElementReferenceException(s.getMessage());
                }
                top5++;
                // Perform Scroll
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollBy(0,250)", "");

                System.out.println("\n*********************************************************\n");
            }
    }

    @Test(priority = 4,
            dependsOnMethods = "scrapeArticles",
            description = "Translate Article Headers")
    public void translateArticleHeaders(){
        /*
            Translate Article Headers:
            Use a translation API of your choice, such as:
            Google Translate API
            Rapid Translate Multi Traduction API
            Translate the title of each article to English.
            Print the translated headers.
            */

        // Translate using Rapid Translate API
        restAssuredUtil = new RestAssuredUtil();
        translatedHeaders = new ArrayList<>();
            System.out.println("\n************* Translated Headers: *************\n");

            Assert.assertFalse(headersToTranslate.isEmpty());
        for (String s : headersToTranslate){
            System.out.println("Spanish Header:\t" + s);

            // Place a regex to handle special characters if any and only return words
            String translatedHeader = restAssuredUtil.translateApi(s.replaceAll("[^a-zA-Z0-9\\s]", ""));
            System.out.println("English Header:\t" + translatedHeader);
            System.out.println("--------------------------------------------------------------------------------");

            // Copy translated headers to a new list
            translatedHeaders.add(translatedHeader);
            }
    }

    @Test(priority = 5,
            dependsOnMethods = "translateArticleHeaders",
            description = "Analyze Translated Headers")
    public void analyseTranslatedHeaders(){
        /*
        Analyze Translated Headers:
        From the translated headers, identify any words that are repeated more than twice across all headers combined.
        Print each repeated word along with the count of its occurrences.
        */

        String headersCombinedText = " ";
        Assert.assertFalse(translatedHeaders.isEmpty());
        for (String s : translatedHeaders){
            headersCombinedText = headersCombinedText.concat(s.toLowerCase()).concat(" ");
        }

        System.out.println("Combined headers in a string:\t" + headersCombinedText);

        // Split the string contents based on the spaces and compare each word and it's occurrence
        Map<String, Integer> map = new HashMap<>();
        int occurrence = 0;
        String[] keywords = headersCombinedText.split(" ");

        for (String word : keywords){
            map.put(word, map.getOrDefault(word, occurrence) + 1);
        }

        // Iterate through values stored in map
        for (Map.Entry<String, Integer> m : map.entrySet()){
            if (m.getValue() > 1){
                System.out.println("Repeated Word:\t" + m.getKey() + "\tOccurrence:\t" + m.getValue());
            }
        }
    }

    @AfterClass
    public void closeBrowser(){
        driver.quit();
    }

}
