package com.tugalsan.api.file.html.selenium.server.core;

import module org.seleniumhq.selenium.edge_driver;

public class DriverHandlerEdge implements AutoCloseable {

    public DriverHandlerEdge(EdgeOptions options) {
        driver = new EdgeDriver(options);
    }
    final public EdgeDriver driver;

    @Override
    public void close() {
        driver.close();
    }
}
