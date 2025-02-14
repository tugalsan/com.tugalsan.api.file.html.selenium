package com.tugalsan.api.file.html.server.selenium;

import com.tugalsan.api.function.client.maythrow.uncheckedexceptions.TGS_FuncMTUCE_OutBool_In1;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwaitSingle;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncWait;
import com.tugalsan.api.url.client.TGS_Url;
import java.awt.Dimension;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

//https://stackoverflow.com/questions/79432850/selenium-driver-is-not-waiting-for-the-page-to-load-on-java-for-mermaid-js
public class TS_FileHtmlSeleniumUtils {

    final private static TS_Log d = TS_Log.of(TS_FileHtmlSeleniumUtils.class);

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
        WebDriver _driver = null;
        try {
            var options = new EdgeOptions();
            _driver = new EdgeDriver(options);
            _driver.manage().timeouts().implicitlyWait(waitForPageLoad);
            _driver.manage().timeouts().pageLoadTimeout(waitForPageLoad);
            _driver.manage().window().setSize(new org.openqa.selenium.Dimension(scrnSize.width, scrnSize.height));
            var driver = _driver;
            return TS_ThreadAsyncAwait.callSingle(killTrigger, Duration.ofSeconds(waitForPageLoad.toSeconds() * 2 + waitForPstTolerans.toSeconds() * 2), kt -> {
                TS_ThreadSyncWait.of(d.className, kt, waitForPageLoad);
                driver.get(urlStr);
                String sourcePre = null;
                while (kt.hasNotTriggered() && sourcePre == null) {
                    d.cr("processHTML", "while.null", "thick");
                    sourcePre = driver.getPageSource();
                    TS_ThreadSyncWait.milliseconds100();
                }
                while (kt.hasNotTriggered() && !loadValidator.validate(sourcePre)) {
                    d.cr("processHTML", "while.validate", "thick");
                    sourcePre = driver.getPageSource();
                    TS_ThreadSyncWait.milliseconds200();
                }
                String sourceCurrent = null;
                while (kt.hasNotTriggered() && !Objects.equals(sourcePre, sourceCurrent)) {
                    d.cr("processHTML", "while.processed", "thick");
                    sourcePre = sourceCurrent;
                    TS_ThreadSyncWait.milliseconds500();
                    sourceCurrent = driver.getPageSource();
                }
                TS_ThreadSyncWait.of(d.className, kt, waitForPstTolerans);
                return driver.getPageSource();
            });
        } finally {
//            if (driver != null) {//gives error!
//                driver.close();
//            }
            if (_driver != null) {
                _driver.quit();
            }
        }
    }
}
