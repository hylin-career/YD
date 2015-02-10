package com.demo;

import java.net.URLEncoder;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.*;
import org.openqa.selenium.support.ui.*;

public class testWebDriver{

    private static void _DisableVerboseLoggingReport(){
        String[] loggingOffClasses = new String[]{
                "com.gargoylesoftware.htmlunit",
                "org.apache.http",
                "com.gargoylesoftware.htmlunit.javascript",
                "com.gargoylesoftware.htmlunit.javascript.StrictErrorReporter",
                "com.gargoylesoftware.htmlunit.IncorrectnessListenerImpl"
        };

        for(String className : loggingOffClasses){
            java.util.logging.Logger.getLogger(className)
                    .setLevel(java.util.logging.Level.OFF);
        }
    }
    
    public static String kejFlvRetriever2(String YoutubeUrl){
        String kejPartialUrl = "http://kej.tw/flvretriever/youtube.php?videoUrl=";
        String encodedYoutubeUrl = URLEncoder.encode(YoutubeUrl);
        System.out.println("encodedYoutubeUrl: " + encodedYoutubeUrl);
        
        HtmlUnitDriver kejDriver = new HtmlUnitDriver(true);
        _DisableVerboseLoggingReport();

        String kejUrl = kejPartialUrl + encodedYoutubeUrl;
        kejDriver.get(kejUrl);
        
        String linkText = "下載此檔案";
        WebElement videoInfoElement = null;
        try{
            videoInfoElement = kejDriver.findElement(By.linkText(linkText));
        }catch(NoSuchElementException E){
            System.out.println(E.getMessage());
        }
        String videoInfoUrl = videoInfoElement.getAttribute("href");
        System.out.println("videoInfoUrl = " + videoInfoUrl);
        
        /* Fetch "get_video_info" */
        WebDriver videoInfoDriver = new HtmlUnitDriver();
        videoInfoDriver.get(videoInfoUrl);
        String videoInfoHtmlText = videoInfoDriver.getPageSource();
        videoInfoDriver.close();

        /* Copy "get_video_info" into textArea */
        WebElement videoInfoTextArea = kejDriver.findElement(By.id("videoInfo"));
        videoInfoTextArea.sendKeys(videoInfoHtmlText);

        /* Click "送出" button */
        String xpathButton = "/html/body/div[@class='setcenter']/div[@class='class1']/div[@id='resultarea']/input";
        WebElement submitButton = kejDriver.findElement(By.xpath(xpathButton));
        submitButton.click();

        /* Wait for the website javaScript generating "result_div" HTML block */
        WebDriverWait wait = new WebDriverWait(kejDriver, 10);
        WebElement resultDivElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("result_div")  ));
        //WebElement resultDivElement = kejDriver.findElement(By.id("result_div"));

        /* Find all HTML anchor links */
        List<WebElement> links = resultDivElement.findElements(By.tagName("a"));
        System.out.println("link count: " + links.size());
        for(WebElement linkElement : links){
            String linkTxt = linkElement.getText();
            String linkUrl = linkElement.getAttribute("href");
            System.out.println("    " + linkTxt + ", " + linkUrl);
        }
        
        String flvLinkUrl = null;
        for(WebElement linkElement : links){
            if(linkElement.getText().toUpperCase().indexOf("FLV") >= 0){
                flvLinkUrl = linkElement.getAttribute("href");
                
                break;
            }
        }
        System.out.println("First FLV: " + flvLinkUrl);

        kejDriver.quit();
        
        return(flvLinkUrl);
    }
    
    public void google(){
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://www.google.com");
System.out.println("CurrentUrl: " + driver.getCurrentUrl());
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("hello world");
        element.submit();
System.out.println("CurrentUrl: " + driver.getCurrentUrl());
        System.out.println("Page title is: " + driver.getTitle());

        driver.quit();
    }

    public void findElement(){
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://www.google.com");
        WebElement element = driver.findElement(By.name("KKK"));
        System.out.println(element);
        
        driver.quit();
    }
    
    public static void main(String[] args){
        testWebDriver twd = new testWebDriver();
        //twd.findElement();
        //twd.kejFlvRetriever();
        //twd.google();
        String url = "http://www.youtube.com/watch?v=A9HV5O8Un6k";
        twd.kejFlvRetriever2(url);
    }
}
