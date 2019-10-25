
package com.saucedemo.js.uitest;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

public class UITest {

	DriverFactory driverFactory;
	static WebDriver driver;
	static String item1Text, item2Text, item3Text;
	static double item1Amount, item2Amount, item3Amount;

	public void initiateDriver(String browser) {
		driver = DriverFactory.initialize(browser).getdriver();
		driver.manage().window().fullscreen();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	@Test(priority = 0)
	public void runTests() throws InterruptedException {
		sauceDemoRun("Chrome");
	}

	@Test(priority = 1)
	public void runHeadlessTests() throws InterruptedException {
		sauceDemoRun("headless");
	}

	public void sauceDemoRun(String browser) throws InterruptedException {
		initiateDriver(browser);
// Homepage login screen
		driver.get("http://www.saucedemo.com");
		Thread.sleep(2000);
// Enter username and password
		driverFunction(By.id("user-name")).sendKeys("standard_user");
		driverFunction(By.id("password")).sendKeys("secret_sauce");
		Thread.sleep(2000);

		driverFunction(By.xpath("//*[@id='login_button_container']/div/form/input[3]")).click();
//Sort items low to high price
		Select sort = new Select(driverFunction(By.className("product_sort_container")));
		sort.selectByValue("lohi");
		// Select item 1. Using method for item selection. Method given below.
		addItemToCart("Item-1");
		Thread.sleep(2000);
//Select item 2.
		addItemToCart("Item-2");
		Thread.sleep(2000);
//Going to cart for item verification
		driverFunction(By.id("shopping_cart_container")).click();
		Thread.sleep(2000);
		String cartItems = driverFunction(By.id("cart_contents_container")).getText();
		AssertJUnit.assertTrue("Item 1 is present", cartItems.contains(item1Text));
		AssertJUnit.assertTrue("Item 2 is present", cartItems.contains(item2Text));
		Thread.sleep(2000);
//Removing Item 2.
		driverFunction(By.xpath("//*[@id='cart_contents_container']/div/div[1]/div[4]/div[2]/div[2]/button")).click();
		Thread.sleep(2000);
//Adding Item 3 to cart.
		driverFunction(By.xpath("//*[@id='cart_contents_container']/div/div[2]/a[1]")).click();
		addItemToCart("Item-3");
		driverFunction(By.id("shopping_cart_container")).click();
		cartItems = driverFunction(By.id("cart_contents_container")).getText();
// Asserting selected items present in cart.
		AssertJUnit.assertTrue("Item 1 is present", cartItems.contains(item1Text));
		AssertJUnit.assertTrue("Item 3 is present", cartItems.contains(item3Text));
// Proceeding to checkout
		driverFunction(By.xpath("//*[@id='cart_contents_container']/div/div[2]/a[2]")).click();
		driverFunction(By.id("first-name")).sendKeys("Test");
		driverFunction(By.id("last-name")).sendKeys("UI");
		driverFunction(By.id("postal-code")).sendKeys("12345");
		Thread.sleep(2000);
//Verifying item price before taxes is equal to shown total price before taxes
		driverFunction(By.xpath("//input[@class='btn_primary cart_button']")).click();
		String checkoutItems = driverFunction(By.xpath("//*[@id='checkout_summary_container']/div/div")).getText();
		AssertJUnit.assertTrue("Item 1 is present", checkoutItems.contains(item1Text));
		AssertJUnit.assertTrue("Item 3 is present", checkoutItems.contains(item3Text));
		String itemTotalBeforeTaxPrice = driverFunction(By.xpath("//*[@class='summary_subtotal_label']")).getText();
		System.out.println(itemTotalBeforeTaxPrice);
		double itemTotalBeforeTax = Double.parseDouble(itemTotalBeforeTaxPrice
				.substring(itemTotalBeforeTaxPrice.length() - 5, itemTotalBeforeTaxPrice.length()));
		AssertJUnit.assertEquals(item1Amount + item3Amount, itemTotalBeforeTax, 0.0);
//Finishing checkout
		driverFunction(By.xpath("//*[@class='btn_action cart_button']")).click();
	}

	public void addItemToCart(String itemNumber) {
		switch (itemNumber) {
		case "Item-1":
			WebElement item1 = driver.findElement(By.xpath("//*[@id='item_2_title_link']/div"));
			WebElement item1Price = driver
					.findElement(By.xpath("//*[@class='inventory_container']/div/div[1]/div[3]/div"));
			item1Text = item1.getText();
			item1Amount = Double.valueOf(item1Price.getText().replace("$", ""));
			System.out.println(item1Text + " " + item1Amount);
			WebElement addToCartItem1 = driver
					.findElement(By.xpath("//*[@id='inventory_container']/div/div[1]/div[3]/button"));
			addToCartItem1.click();
			break;
		case "Item-2":
			WebElement item2 = driver.findElement(By.xpath("//*[@id='item_0_title_link']/div"));
			WebElement item2Price = driver
					.findElement(By.xpath("//*[@id='inventory_container']/div/div[2]/div[3]/div"));
			item2Text = item2.getText();
			item2Amount = Double.valueOf(item2Price.getText().replace("$", ""));
			System.out.println(item2Text + " " + item2Amount);
			WebElement addtoCartItem2 = driver
					.findElement(By.xpath("//*[@id='inventory_container']/div/div[2]/div[3]/button"));
			addtoCartItem2.click();
			break;
		case "Item-3":
			WebElement item3 = driver.findElement(By.id("item_1_title_link"));
			item3Text = item3.getText();
			WebElement item3Price = driver
					.findElement(By.xpath("//*[@class='inventory_container']/div/div[3]/div[3]/div"));
			item3Amount = Double.valueOf(item3Price.getText().replace("$", ""));
			System.out.println(item3Text + " " + item3Amount);
			WebElement addToCartItem3 = driver
					.findElement(By.xpath("//*[@id='inventory_container']/div/div[3]/div[3]/button"));
			addToCartItem3.click();
		default:
			break;
		}
	}

	private WebElement driverFunction(By by) {
		return driver.findElement(by);

	}

	@AfterClass
	public void quitDriver() {
		driver.quit();
	}

}
