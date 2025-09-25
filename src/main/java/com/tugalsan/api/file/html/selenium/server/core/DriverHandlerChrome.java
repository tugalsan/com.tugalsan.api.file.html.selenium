package com.tugalsan.api.file.html.selenium.server.core;

import module org.seleniumhq.selenium.chrome_driver;

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
