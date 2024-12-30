import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.given;

public class RestAssuredUtil {
    Properties properties;
    public static String apiKey;

    public String translateApi(String input){
        try {
            properties = new Properties();
            properties.load(new FileInputStream(System.getProperty("user.dir") + File.separator +
                    "TranslationAPI.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // BaseURI
        String baseUri = properties.getProperty("translationApiUrl");

        // API Headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", properties.getProperty("contentType"));
        apiKey = System.getenv("TRANSLATION_API_URL");
        headers.put("X-RapidAPI-Key", apiKey);
        headers.put("X-RapidAPI-Host", properties.getProperty("apiHost"));

        // API Body
        Map<String, String> body = new HashMap<>();
        body.put("from", properties.getProperty("fromLanguage"));
        body.put("to", properties.getProperty("toLanguage"));
        body.put("e", "");
        body.put("q", input);

        // Convert body to String
        String jsonBody = convertBodyToString(body);

        // Providing hardcoded status code 200 here, will be dynamic later
        String response = fetchResponse(headers, jsonBody, baseUri, 200).asString();
        JsonArray object = JsonParser.parseString(response).getAsJsonArray();
        return object.get(0).getAsString();
    }

    public RequestSpecification requestBuilder(Map<String, String> headers, String body,
                                               String baseUri){
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addHeaders(headers)
                .setBody(body)
                .build();
    }

    public ResponseSpecification responseBuilder(int statusCode){
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .build();
    }

    public Response fetchResponse(Map<String, String> headers, String body,
                                  String baseUri, int expectedCode){
        // Build complete request using requestSpec and responseSpec
        RequestSpecification request;
        Response response = null;
        request = given().log().all().spec(requestBuilder(headers, body, baseUri));
        response = request.when().post().then().spec(responseBuilder(expectedCode)).log().all().extract().response();

        Assert.assertEquals(expectedCode, response.statusCode());
        return response;
    }

    public String convertBodyToString(Object body){
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            return mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractStringFromJsonArray(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String[] stringArray = mapper.readValue(response, String[].class);
            return stringArray[0];
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
