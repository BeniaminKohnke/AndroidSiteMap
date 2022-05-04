package com.example.androidforeversource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public abstract class SiteMap {
    protected final String shopName;
    protected final String shopAddress;
    private final ArrayList<String> productsAddresses = new ArrayList<>();

    public SiteMap(String shopName, String shopAddress){
        this.shopName = shopName;
        this.shopAddress = shopAddress;
    }

    protected abstract Product scrapPage(String address);
    protected abstract ArrayList<String> crawlAddresses();

    public void startScanning(){
        var database = new DataAccess();

        for (var address : crawlAddresses()) {
            if(!productsAddresses.contains(address)){
                productsAddresses.add(address);
            }
        }

        for (var address : productsAddresses) {
            try {
                var product = scrapPage(address);
                database.SaveOrUpdateProduct(product);
            } catch (Exception e) {

            }
        }
    }

    protected Document getHTMLDocument(String address, Integer timeout){
        Document document = null;
        try {
            Jsoup.newSession();
            document = Jsoup.parse(new URL(address), timeout);
            document.outputSettings()
                    .escapeMode(Entities.EscapeMode.xhtml)
                    .syntax(Document.OutputSettings.Syntax.html)
                    .charset(StandardCharsets.UTF_8);
        } catch (IOException e) {

        }
        return document;
    }
}
