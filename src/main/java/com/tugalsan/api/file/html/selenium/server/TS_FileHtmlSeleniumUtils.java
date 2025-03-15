package com.tugalsan.api.file.html.selenium.server;

import com.tugalsan.api.file.html.selenium.server.core.DriverHandlerChrome;
import com.tugalsan.api.file.html.selenium.server.core.DriverHandlerEdge;
import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import com.tugalsan.api.function.client.maythrow.uncheckedexceptions.TGS_FuncMTUCE_OutBool_In1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwaitSingle;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncWait;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.url.client.TGS_Url;
import java.awt.Dimension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.print.PageMargin;
import org.openqa.selenium.print.PageSize;
import org.openqa.selenium.print.PrintOptions;

//https://stackoverflow.com/questions/79432850/selenium-driver-is-not-waiting-for-the-page-to-load-on-java-for-mermaid-js
public class TS_FileHtmlSeleniumUtils {

    private TS_FileHtmlSeleniumUtils() {

    }

    final private static TS_Log d = TS_Log.of(TS_FileHtmlSeleniumUtils.class);

    public static PrintOptions toPrintOptions(boolean isLandScape, Float scale, Dimension pageSize, Integer marginLeft, Integer marginRight, Integer marginTop, Integer marginBottom) {
        var po = new PrintOptions();
        po.setOrientation(isLandScape ? PrintOptions.Orientation.LANDSCAPE : PrintOptions.Orientation.PORTRAIT);
        if (scale != null) {
            po.setScale(scale);
        }
        if (pageSize != null) {
            po.setPageSize(new PageSize(pageSize.width, pageSize.height));
        }
        marginTop = marginTop == null ? 0 : marginTop;
        marginBottom = marginBottom == null ? 0 : marginBottom;
        marginLeft = marginLeft == null ? 0 : marginLeft;
        marginRight = marginRight == null ? 0 : marginRight;
        po.setPageMargin(new PageMargin(marginTop, marginBottom, marginLeft, marginRight));
        return po;
    }

    public static TGS_UnionExcuseVoid toPdf(Path urlPath, Path outputPath, PrintOptions printOptions) {
        return TGS_FuncMTCEUtils.call(() -> {
            var options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--run-all-compositor-stages-before-draw", "--remote-allow-origins=*", "--kiosk-printing", "--scalingType:3", "--scaling:10");
            try (var driverHandler = new DriverHandlerChrome(options)) {
                var driver = driverHandler.driver;
                driver.get(urlPath.toUri().toString());
                var pdf = driver.print(printOptions);
                var pdfContent = Base64.getDecoder().decode(pdf.getContent());
                Files.write(outputPath, pdfContent);
                return TGS_UnionExcuseVoid.ofVoid();
            }
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

    public static TS_ThreadAsyncAwaitSingle<String> toHTML(TS_ThreadSyncTrigger killTrigger, TGS_Url url, Dimension scrnSize, Duration waitForPageLoad, TGS_FuncMTUCE_OutBool_In1<String> loadValidator, Duration waitForPstTolerans) {
        return toHTML(killTrigger, url.toString(), scrnSize, waitForPageLoad, loadValidator, waitForPstTolerans);
    }

    public static TS_ThreadAsyncAwaitSingle<String> toHTML(TS_ThreadSyncTrigger killTrigger, Path urlPath, Dimension scrnSize, Duration waitForPageLoad, TGS_FuncMTUCE_OutBool_In1<String> loadValidator, Duration waitForPstTolerans) {
        return toHTML(killTrigger, urlPath.toUri().toString(), scrnSize, waitForPageLoad, loadValidator, waitForPstTolerans);
    }

    private static TS_ThreadAsyncAwaitSingle<String> toHTML(TS_ThreadSyncTrigger killTrigger, String urlStr, Dimension scrnSize, Duration _waitForPageLoad, TGS_FuncMTUCE_OutBool_In1<String> _loadValidator, Duration _waitForPstTolerans) {
        var threshold = 1;
        var waitForPageLoad = _waitForPageLoad == null || _waitForPageLoad.toSeconds() < threshold ? Duration.ofSeconds(threshold) : _waitForPageLoad;
        var waitForPstTolerans = _waitForPstTolerans == null || _waitForPstTolerans.toSeconds() < threshold ? Duration.ofSeconds(threshold) : _waitForPstTolerans;
        TGS_FuncMTUCE_OutBool_In1<String> loadValidator = _loadValidator == null ? html -> true : _loadValidator;
        var options = new EdgeOptions();
        try (var driverHandler = new DriverHandlerEdge(options)) {
            var driver = driverHandler.driver;
            driver.manage().timeouts().implicitlyWait(waitForPageLoad);
            driver.manage().timeouts().pageLoadTimeout(waitForPageLoad);
            driver.manage().window().setPosition(new org.openqa.selenium.Point(0, 0));
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(scrnSize.width, scrnSize.height));
            return TS_ThreadAsyncAwait.callSingle(killTrigger.newChild(d.className).newChild("toHTML"), Duration.ofSeconds(waitForPageLoad.toSeconds() * 2 + waitForPstTolerans.toSeconds() * 2), kt -> {
                TS_ThreadSyncWait.of(d.className, kt, waitForPageLoad);
                driver.get(urlStr);
                String sourcePre = null;
                while (kt.hasNotTriggered() && sourcePre == null) {
                    d.cr("processHTML", "while.null", "tick");
                    sourcePre = driver.getPageSource();
                    TS_ThreadSyncWait.milliseconds100();
                }
                while (kt.hasNotTriggered() && !loadValidator.validate(sourcePre)) {
                    d.cr("processHTML", "while.validate", "tick");
                    sourcePre = driver.getPageSource();
                    TS_ThreadSyncWait.milliseconds200();
                }
                String sourceCurrent = null;
                while (kt.hasNotTriggered() && !Objects.equals(sourcePre, sourceCurrent)) {
                    d.cr("processHTML", "while.processed", "tick");
                    sourcePre = sourceCurrent;
                    TS_ThreadSyncWait.milliseconds500();
                    sourceCurrent = driver.getPageSource();
                }
                TS_ThreadSyncWait.of(d.className, kt, waitForPstTolerans);
                return driver.getPageSource();
            });
        }
    }
}
