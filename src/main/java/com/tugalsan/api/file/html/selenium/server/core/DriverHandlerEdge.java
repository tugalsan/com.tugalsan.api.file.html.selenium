package com.tugalsan.api.file.html.selenium.server.core;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

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
