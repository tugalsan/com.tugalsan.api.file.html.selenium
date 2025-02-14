package com.tugalsan.api.file.html.server.selenium;

import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import static java.lang.System.out;
import java.nio.file.Path;
import java.time.Duration;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

//https://stackoverflow.com/questions/79432850/selenium-driver-is-not-waiting-for-the-page-to-load-on-java-for-mermaid-js
public class TS_FileHtmlSeleniumUtils {

    public static void test() throws InterruptedException {
        TGS_FuncMTCEUtils.run(() -> {
            var urlPath = Path.of("C:\\git\\tst\\com.tugalsan.tst.html\\a.html");
            var urlStr = urlPath.toUri().toString();
            var until = Duration.ofSeconds(15);
            var scrnSize = new Dimension(640, 480);
            var output = processHTML(urlStr, until, scrnSize);
            out.println(output);
        });
    }

    public static String processHTML(String urlStr, Duration until, Dimension scrnSize) throws InterruptedException {
        WebDriver driver = null;
        try {
            var options = new EdgeOptions();
            driver = new EdgeDriver(options);
            driver.manage().timeouts().implicitlyWait(until);
            driver.manage().timeouts().pageLoadTimeout(until);
            driver.manage().window().setSize(scrnSize);
            driver.get(urlStr);
            Thread.sleep(until);
            return driver.getPageSource();
        } finally {
            if (driver != null) {
                driver.close();
            }
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
