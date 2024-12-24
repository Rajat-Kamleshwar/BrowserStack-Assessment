package TestCase;

import Utils.DriverManagerUtil;
import Utils.ImageSaveUtil;
import Utils.RestAssuredUtil;
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
import java.util.*;

public class WebScraping extends DriverManagerUtil {

    static ImageSaveUtil imageSaveUtil;
    static RestAssuredUtil restAssuredUtil;
    static List<String> headersToTranslate;
    static List<String> translatedHeaders;

    public static void main(String[] args) {

        // Initialize Driver
        driver = driverInitialization("Chrome");

        // Invoke browser and navigate to website
        navigateToWebsite();
        acceptWebsiteNotice();  // Accept the notice if displayed

        // Assert default language is Spanish
        boolean flag = false;
        if (flag == verifyLanguage()){
            System.out.println("Default language is Spanish");
        } else {
            System.out.println("Default language is not Spanish");
            closeBrowser();
        }

        // Scrape Title and Content of Top 5 articles within Opinion section
        scrapeArticles();

        // Translate Headers into English language
        translateArticleHeaders();

        // Analyse translated headers and print the repeated words from combined headers
        analyseTranslatedHeaders();

        closeBrowser();
    }

    static void navigateToWebsite(){
        String url = properties.getProperty("URL");
        driver.navigate().to(url);
        driver.manage().window().maximize();
        System.out.println("Navigated to: " + url);
    }

    static void acceptWebsiteNotice(){
        // Wait until website notice is displayed and accept the notice
        try {
            wait.until(ExpectedConditions.elementToBeClickable(driver.findElement
                    (By.xpath("//div[@data-testid = 'notice']"))));

            WebElement acceptBtn = driver.findElement(By.xpath("//button[@id='didomi-notice-agree-button']"));
            wait.until(ExpectedConditions.elementToBeClickable(acceptBtn));
            acceptBtn.click();
        } catch (NoSuchElementException e){
            e.printStackTrace();
        } finally {
            assert Objects.equals(driver.getCurrentUrl(), properties.getProperty("URL"));
        }
    }

    static boolean verifyLanguage(){
        // TC1: Ensure that the website's text is displayed in Spanish.

        WebElement languageEle = driver.findElement
                (By.xpath("//time[contains(@id, 'header')]/parent::div/div //ul/li[1] //span"));
        return Objects.equals(languageEle.getText(), System.getProperty("Spanish"));
    }

    static void scrapeArticles(){
        /*
            TC2: Scrape Articles from the Opinion Section:
            Navigate to the Opinion section of the website.
            Fetch the first five articles in this section.
            Print the title and content of each article in Spanish.
            If available, download and save the cover image of each article to your local machine.
        */

        List<WebElement> opinionOpt = driver.findElements
                (By.xpath("//div[@id='csw']/div[1] //div/a"));
        for (WebElement e : opinionOpt){
            if (e.getText().equalsIgnoreCase(properties.getProperty("OpinionSection"))){
                e.click();
                break;
            }
        }

        System.out.println("Articles scraped from Opinion section!:\n");
        List<WebElement> articles = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] //article"));
        List<WebElement> articleHeader = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] " +
                        "//article/header/h2"));
        List<WebElement> articleContent = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] " +
                        "//article/p"));
        List<WebElement> articleImage = driver.findElements
                (By.xpath("//div[@id='csw']/parent::header/parent::header/following-sibling::main/div[1] " +
                        "//article/figure //img"));

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
                imageSaveUtil.saveImageFromWebsite(articleImage);
                top5++;
                System.out.println("\n*********************************************************\n");
            }
        }

    static void translateArticleHeaders(){
        /*
        TC3: Translate Article Headers:
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
        for (String s : headersToTranslate){
            System.out.println("Spanish Header:\t" + s);

            // Place a regex to handle special characters if any and only return words
            String translatedHeader = restAssuredUtil.translateApi(s.replaceAll("[^a-zA-Z0-9\\s]", ""));
            System.out.println("English Header:\t" + translatedHeader);
            System.out.println("--------------------------------------------------------------------------------");

            // Copy translated headers to a new list
            translatedHeaders.add(translatedHeader);
            }


        // Remove later, printing just for verification
        System.out.println("\n\n\n\n\nEnglish Headers:\n\n\n\n\n");
        for (String s : translatedHeaders){
            System.out.println(s);
            }
        }

    static void analyseTranslatedHeaders(){
        /*
        Analyze Translated Headers:
        From the translated headers, identify any words that are repeated more than twice across all headers combined.
        Print each repeated word along with the count of its occurrences.
        */

        String headersCombinedText = " ";
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

    static void closeBrowser(){
        driver.quit();
    }

}
