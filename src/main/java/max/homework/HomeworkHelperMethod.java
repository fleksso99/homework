package max.homework;

import org.assertj.core.api.Assertions;

public class HomeworkHelperMethod extends BasePage {

  public void validateDiscountCodeNotValid(String discountCode) {
    click("//div[@class='cart-ingka-ssr-label' and text()='Have a discount code?']");
    String discountCouponField = "//input[@id='discountCode']";
    waitForVisibility(discountCouponField, 2);
    type(discountCouponField, discountCode);
    click("//div[contains(@class,'cartCoupon_formContent')]/button[contains(@class,'cart-ingka-btn')]");
    String notValidCouponMessage = "//span[@class='cart-ingka-form-field__message']";
    Assertions.assertThat(getTextFrom(notValidCouponMessage)).contains("You've added an invalid coupon code. Please try again.");
  }

  public String addItemToCartByIndex(int itemIndex) {
    String valueBefore = getTextFrom("//li[@class='hnf-header__shopping-cart-link']/a/span/span");
    int counterBefore = valueBefore.equals("") ? 0 : Integer.parseInt(valueBefore);
    //picking 1rd item by index
    String itemRoot = "//div[@id='search-results']/div[" + itemIndex + "]";
    click(itemRoot + "//button[contains(@id,'add_to_cart')]");
    String data_ref_id = getValueFromAttribute(itemRoot, "data-ref-id");

    fluentWait(7000); // instead of wait
    // need to refactor this method and instead wait until popup message will dissapear

    int counterAfterAdding = Integer.parseInt(getTextFrom("//li[@class='hnf-header__shopping-cart-link']/a/span/span"));
    Assertions.assertThat(counterAfterAdding).isEqualTo(counterBefore + 1);
    return data_ref_id;
  }

  public void searchItem(String searchItem) {
    click("//input[contains(@class,'search')]");
    type("//input[contains(@class,'search')]", searchItem);
    click("//ol[@id='search-suggestions']/li[1]/a");
    waitForVisibility("//h1[@class='search-summary__heading']", 5);
  }

}
