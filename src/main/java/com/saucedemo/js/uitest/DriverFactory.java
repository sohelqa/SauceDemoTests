package com.saucedemo.js.uitest;

import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;

public class DriverFactory {

	private ThreadLocal<WebDriver> driverBase = new ThreadLocal<WebDriver>();
	private static DriverFactory instance = null;
	private static String Hub_URL = "http://192.168.0.104:4444/wd/hub";

	
	private DriverFactory() {
	}

	public static DriverFactory initialize() {
		return initialize(null);
	}

	public static DriverFactory initialize(String browser) {
		if (instance == null) {
			instance = new DriverFactory();
		}

		if (browser == null) {
			browser = "";
		}

		if (instance.driverBase.get() == null) {
			WebDriver driver;
			DesiredCapabilities caps;
			System.out.println("Starting browser: " + browser);
			
			switch (browser.toLowerCase()) {
			case "chrome":
				ChromeDriverManager.chromedriver().arch32().setup();
				driver = new ChromeDriver();
				instance.driverBase.set(driver);
				break;

			case "headless":
				System.out.println("Starting Chrome Headless browser");
				ChromeDriverManager.chromedriver().arch32().setup();
				ChromeOptions options1 = new ChromeOptions();
				options1.addArguments("window-size=1400,800");
				options1.addArguments("headless");
				driver = new ChromeDriver(options1);
				instance.driverBase.set(driver);
				break;

			default:
				break;
			}
		}
		return instance;
	}

	public WebDriver getdriver() {
		return driverBase.get();
	}

	public void tearDown() {
		driverBase.get().quit();
		driverBase.remove();
		driverBase.set(null);
	}
}