package com.tugalsan.api.file.html.selenium.server.core;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverHandlerChrome implements AutoCloseable {

    public DriverHandlerChrome(ChromeOptions options) {
        driver = new ChromeDriver(options);
    }
    final public ChromeDriver driver;

    @Override
    public void close() {
        driver.close();
    }
}
