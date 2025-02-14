module com.tugalsan.api.file.html.selenium {
    requires java.xml;
    requires org.seleniumhq.selenium.manager;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.edge_driver;
    requires org.seleniumhq.selenium.chrome_driver;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.thread;
    requires com.tugalsan.api.url;
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.function;
    exports com.tugalsan.api.file.html.server.selenium;
}
