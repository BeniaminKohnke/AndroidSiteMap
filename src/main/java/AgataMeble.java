import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;

public class AgataMeble extends SiteMap {
    private final XPathEvaluator categoryXPath =
            Xsoup.compile("//div[@class='m-menu_listColumn']//div[contains(@class,'m-menu_submenu')]//a/@href");
    private final XPathEvaluator nextPageXPath =
            Xsoup.compile("//a[@class='m-pagination_item m-pagination_next js-pagination_next']/@href");
    private final XPathEvaluator addressXPath =
            Xsoup.compile("//div[@class='m-offerBox_name']/a/@href");

    private final XPathEvaluator nameXPath =
            Xsoup.compile("//h1[@class='m-typo m-typo_primary']/text()");
    private final XPathEvaluator priceXPath =
            Xsoup.compile("//div[@class='b-offer_cta ']//div[@class='m-priceBox_price m-priceBox_promo']/text()");
    private final XPathEvaluator skuXPath =
            Xsoup.compile("//p[@class='m-typo_tertiary' and contains(text(), 'Index:')]/text()");

    public AgataMeble(){
        super("AgataMeble", "https://www.agatameble.pl");
    }

    @Override
    protected Product scrapPage(String address) {
        var product = new Product();
        product.url = address;

        try{
            var document = getHTMLDocument(address, 10000);
            product.name = nameXPath.evaluate(document).get().trim();
            product.sku = skuXPath.evaluate(document).get().replace("Index:", " ").trim();
            product.price = Double.parseDouble(priceXPath.evaluate(document).get());
        } catch (Exception e) {

        }

        return product.sku.isEmpty() || product.name.isEmpty() || product.price <= 0 ? null : product;
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
                                addresses.add(shopAddress + url);
                            }
                        }

                        nextPageUrl = nextPageXPath.evaluate(document).get();
                    }while(nextPageUrl != null);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }

        return addresses;
    }
}
