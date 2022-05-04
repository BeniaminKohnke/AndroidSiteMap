package com.example.androidforeversource;

import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;

public class AgataMeble extends SiteMap {
    private final XPathEvaluator categoryXPath =
            Xsoup.compile("//div[contains(@class,'m-menu_submenu')]//a[contains(@href, '/meble/')]/@href"); //only furniture
    private final XPathEvaluator nextPageXPath =
            Xsoup.compile("//a[@class='m-pagination_item m-pagination_next js-pagination_next']/@href");
    private final XPathEvaluator addressXPath =
            Xsoup.compile("//div[@class='m-offerBox_name']/a/@href");

    private final XPathEvaluator nameXPath =
            Xsoup.compile("//h1[contains(@class,'m-typo_primary')]/text()");
    private final XPathEvaluator priceXPath =
            Xsoup.compile("//div[@class='b-offer_cta ']//div[@class='m-priceBox_price m-priceBox_promo']/text()");
    private final XPathEvaluator oldPriceXPath =
            Xsoup.compile("//div[@class='b-offer_cta ']//span[@class='m-priceBox_oldPrice']/text()");
    private final XPathEvaluator skuXPath =
            Xsoup.compile("//p[@class='m-typo_tertiary']//text()");
    private final XPathEvaluator productCategoryXPath =
            Xsoup.compile("//li[@class='m-breadcrumb_item' or @class='m-breadcrumb_item m-breadcrumb_preLast']/a/text()");

    public AgataMeble(){
        super("AgataMeble", "https://www.agatameble.pl");
    }

    @Override
    protected Product scrapPage(String address) {
        System.out.println("CURRENT PRODUCT:" + address);
        var product = new Product();
        product.url = address;

        try{
            var document = getHTMLDocument(address, 10000);
            product.name = nameXPath.evaluate(document).get().trim();
            product.sku = skuXPath.evaluate(document).get().replace("Index:", "").trim();
            product.currentPrice = Double.parseDouble(priceXPath.evaluate(document).get().replace(',','.').replace("-", ""));

            var oldPrice = oldPriceXPath.evaluate(document);
            if(oldPrice != null && oldPrice.get() != null){
                product.oldPrice = Double.parseDouble(oldPrice.get().replace(',','.').replace("-", ""));
            }

            var categories = productCategoryXPath.evaluate(document).list();
            var fullCategory = new StringBuilder();
            for (var category : categories) {
                fullCategory.append(category + ";");
            }
            product.category = fullCategory.toString();
        } catch (Exception e) {

        }

        return product.sku.isEmpty() || product.name.isEmpty() || product.currentPrice <= 0 ? null : product;
    }

    @Override
    protected ArrayList<String> crawlAddresses() {
        var categoriesAddresses = categoryXPath.evaluate(getHTMLDocument(shopAddress, 60000)).list();
        var addresses = new ArrayList<String>();
        try {
            for (var address : categoriesAddresses) {
                try{
                    var nextPageUrl = shopAddress + address;
                    do{
                        var document = getHTMLDocument(nextPageUrl, 30000);
                        var urls = addressXPath.evaluate(document).list();
                        if(urls != null) {
                            for(var url : urls){
                                System.out.println("NEW PRODUCT:" + url);
                                addresses.add(shopAddress + url);
                            }
                        }

                        //Because of database capacity we shouldn't scrape every page
                        nextPageUrl = null;//nextPageXPath.evaluate(document).get();
                    }while(nextPageUrl != null);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }

        return addresses;
    }
}
