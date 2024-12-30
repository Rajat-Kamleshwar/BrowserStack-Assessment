# BrowserStack-Assessment
This repository contains BS assessment containing Web Scraping, API integration and text processing using Selenium with Java

Framework Overview
This framework is built using Selenium automation framework with Java programming language and integrated with TestNG framework using POM. The framework is dynamic i.e, it is integrated with BrowserStack library as well to execute scripts over the cloud platform.


Framework Highlights

WebDriver Implementation
In order to make the framework more dynamic, a separate DriverManagerUtil class has been developed consisting of the webdrive implementation supported for multiple browsers including Chrome, Firefox, Safari and Edge. The browserName key on which the script execution is to be done is driven via browser.properties file which contains keys for all the browsers.
This utility also contains implementation for mWEB execution using an external class called MWebUtil class.
This utility class also contains an implementation of WebDriverWait class where the default wait is defined in 30 seconds.
This class is extended by Test Class to access webdriver variables and properties across the tests.
For local execution, hardcoded driver versions are defined within the project directory only but this can be made dynamic by introducing WebDriverManager class which fetches the latest browser binaries upon execution and proceeds execution with the found binary.

MWebUtil Integration within DriverManagerUtil class
To make the execution compatible for mWEB resolutions as well, a separate class has been defined consisting of a mobileEmulation key which contains deviceName and its value in the form of key-value pair. This property is specific to ChromeOptions class only as the web resolution is converted into mobile resolution for the desired deviceName passed within the options class. This can be made more dynamic by using DevTools class supported for 4.0+ selenium versions.

ImageSaveUtil Class for saving artwork images
For saving artworks which are present for the articles, ImageSaveUtil class is defined. The implementation is like first clear the image directory ensuring the folder is empty before saving the artworks and then capture all the images present within the top 5 articles using src attribute which contains image location. For each article parsed via the script, this util checks if the artwork is available or not, if yes then fetches the image and saves it within the directory with the name CoverImage_ followed by dynamic incremented value.

RestAssured Util for translating the article headers via API
This is one of the most important utility classes of this framework which consists of rest assured code for integrating translation API. RequestSpecification and ResponseSpecific classes are responsible for building the request and response for the API. All the API headers, url and request body are dynamically driven via TranslationAPI.properties file.
First, the request is built by using the RequestSpecBuilder() class which contains methods like setBaseUri(), setContentType(), setBody(), etc. Request is built using the given() method which is responsible for building the request by accepting preconditions and configurations for the API request.
Since the body needs to be passed as String, Jackson Databind Library is integrated which consists of a class called ObjectMapper responsible for converting the object to a string and vice versa. The body is converted into String using the writeValueAsString() method.
Then response is expected using ResponseSpecBuilder() class which contains methods like expectStatusCode(), expectContentType(), etc. Request is integrated with a combination of when() and then() methods. when() is used to specify the HTTP method of the request i.e., GET, POST, PUT or DELETE and then() is used to set the final expectation like status code.
Finally, the API request is completed by integrating both request and response objects within Response class’s object. This class is used to extract the response of a request.
This utility is called within the translation test where article headers are passed within the request body which are to be translated and the response is extracted as a string.



Test Cases and Methods

@BeforeClass
setup() method is defined which will be executed at the very beginning of the test execution. This method calls the driverInitialization() method from the DriverManagerUtil class to initiate the driver for the tests.
Test URL is also defined within this class to initiate the browser and navigate to a particular URL.

@Test1 navigateToWebsite()
This test case is executed post navigation to the test website and contains the logic for accepting the Website Notice before proceeding further with other tests. Since these test cases are flaky sometimes as the notice rendering is not consistent and might take longer to be clickable, hence it is executed first to ensure the rest of the tests are not failed.
This test contains some additional logic for handling the website notice on both Browsers and Devices as in some devices, the HTML code is different compared to Web and mWeb versions of the same website. Hence, this test is dynamic and is applicable for Browsers, mWeb and Device resolutions.


@Test2 verifyLanguage()
This test contains validation of the default language of the website. As per the requirements, the language should be Spanish, this test validates the same use case. If the language is not Spanish then assertion will fail and rest of the test cases will be skipped.


@Test3 scrapeArticles()
This is one of the critical tests where article headers, content and artworks are extracted. First, the Opinion section is clicked from within the hamburger menu section. Once the section is invoked, then all the articles along with their header and content present on the page are stored within an array list. The list is iterated 5 times along with a scrolling mechanism using JavascriptExecutor, and all the headers present for the top 5 articles are captured in another array list that will be translated.
During the iteration, the ImageSaveUtil class is also invoked for downloading the artworks for top 5 articles.


@Test4 translateArticleHeaders()
Another important test which is responsible for translating the article headers using the Translation API. All the headers stored in the previous test are iterated in this test and each header value is passed within the request body defined in RestAssuredUtil class. The method translateApi() accepts string input as an argument which is passed to the request body’s q key to return the translated header in the desired language. Both the original and translated languages are property driven and can be changed from the TranslationAPI.properties file.
Once all the headers are translated, the updated headers are stored in an array list to be used in the next test.


@Test5 analyseTranslatedHeaders()
This test contains the logic to filter the words which are repeated more than twice by introducing a HashMap<> data structure as hashMap stores data in a key-value pair and can contain duplicate values for the same key. The logic works by extracting all the words from the headers and iterating through them, putting them within the map. If  the map contains existing words, count is incremented, else 1 is stored.
Once all the words are stored in the map, the map is iterated and only words whose occurrence is more than twice (getValue() > 2) are printed.


@AfterClass()
Once all the tests / methods are executed then all browser windows are closed by triggering quit() method. This method closes all the instances of the browser opened during execution.


**Translation API Source**
https://codepen.io/oussamasibari/pen/JjLLxxv


**NOTE**

BROWSERSTACK_USERNAME, BROWSERSTACK_ACCESSKEY, TRANSLATION_API_KEY values are environment driven and are not exposed 
within the framework!