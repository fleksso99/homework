package max.homework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

public class HomeworkTests extends BasePage {

  HomeworkHelperMethod homeworkHelperMethod = new HomeworkHelperMethod();

  //base url
  String baseUrl = "https://www.ikea.com/us/en";
  String apiEndpoint = "https://datausa.io/api/data";

  //    1. Navigate to: https://www.ikea.com/us/en
//    2. Using search bar at the top of the page - search for "sofa"
//    3. On the first page of search results, pick the 1st item in the list and add it to the cart
//    4. Using search bar at the top of the page - search for "table"
//    5. On the first page of search results, pick the 3rd item in the list and add it to the cart
//    6. Navigate to shopping cart page and validate that 2 items are added to the cart
//    7. Click on "Use a discount code" link, enter random string of 15 characters as discount
//    code and click "Apply
//    discount" button
//    8. Validate that "invalid coupon code" error message is displayed
  @Test
  public void testSelenium() throws InterruptedException {
    openPage(baseUrl);
    Set<String> selectedItems = new HashSet<>();

    //search "sofa"
    homeworkHelperMethod.searchItem("sofa");
    selectedItems.add(homeworkHelperMethod.addItemToCartByIndex(1));

    //search table
    homeworkHelperMethod.searchItem("table");
    selectedItems.add(homeworkHelperMethod.addItemToCartByIndex(3));

    click("//li[@class='hnf-header__shopping-cart-link']");
    String productList = "//div[contains(@class,'productList_productlist')]/div/div[contains(@class,'product_contents')]";
    waitForVisibility(productList, 10);

    //better to write wripper in case if logical validation will need in future
    List<WebElement> elements = driver.findElements(By.xpath(productList));
    Set<String> itemInsideCart = new HashSet<>();
    for (WebElement element : elements) {
      String attribute = element.getAttribute("data-testid");
      String substring = attribute.substring(attribute.lastIndexOf("_") + 1);
      itemInsideCart.add(substring);
    }
    Assertions.assertThat(selectedItems).isEqualTo(itemInsideCart);

    //discount code check
    homeworkHelperMethod.validateDiscountCodeNotValid(RandomStringUtils.random(15, true, true));
  }

  @Test
  public void testApi() {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("User-Agent",
        "&quot;&quot;Mozilla/5.0 (Windows NT 10.0; Win64; x64)AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36&quot;");
    HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(apiEndpoint)
        .queryParam("drilldowns", "State")
        .queryParam("measures", "Population")
        .queryParam("year", "latest");

    ResponseEntity<String> response = restTemplate.exchange(
        uriComponentsBuilder.toUriString(),
        HttpMethod.GET, requestEntity, String.class);
    Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
    JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
    JsonArray dataArr = jsonObject.get("data").getAsJsonArray();
    String yearValue = "";
    Set<String> states = new HashSet<>();
    for (JsonElement data : dataArr) {
      JsonObject dataAsJsonObject = data.getAsJsonObject();

      //validate year the same
      String year = dataAsJsonObject.get("Year").getAsString();
      if (yearValue.isEmpty()) {
        yearValue = year;
      }
      Assertions.assertThat(year).isEqualTo(yearValue);

      //validate State value unique
      String state = dataAsJsonObject.get("State").getAsString();
      if (!states.contains(state)) {
        states.add(state);
      } else {
        Assertions.fail("state: " + state + " present multiple times");
      }
    }
  }

  //method which mentioned in assignment can be emproved
  // String unmutable (not changeable) so everytime when substring method called new String method created
  //in approach which I am offering I am using StringBuilder which is mutable so method execution required less space
  //new method handling spaces between and after letter character which old method not
  //multiple spaces next to each other also handled

  @Test
  public void refactoredMethod() {
    List<String> inputData = new ArrayList<>(
        Arrays.asList("Bill Bee V", "Bruno Mars", "Brian", " Brian   Ivanov  ")
    );
    List<String> outputData = new ArrayList<>();
    inputData.forEach(c -> outputData.add(convertNameToInitials(c)));

    Assertions.assertThat(outputData).isEqualTo(new ArrayList<>(
        Arrays.asList("B.B.V.", "B.M.", "B.", "B.I.")));
  }

  private String convertNameToInitials(String name) {
    StringBuilder builder = new StringBuilder();
    char[] chars = name.toCharArray();
    boolean flag = true;
    for (int i = 0; i < chars.length; i++) {
      if (flag && chars[i] != ' ') {
        builder.append(chars[i]).append('.');
        flag = false;
      }
      if (i + 1 < chars.length && chars[i + 1] == ' ') {
        flag = true;
      }
    }
    return builder.toString();
  }


}
