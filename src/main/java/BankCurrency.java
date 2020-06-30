
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;


public class BankCurrency {

    private static final Logger LOGGER = Logger.getLogger(BankCurrency.class.getName());
    private BankService service = null;

    public String currencyBank(String valute, String region) {
        LOGGER.info("Берём информацию о васюте в указанном регионе");
        String message = "";
        service = new BankService();
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect("https://ru.myfin.by/currency/" + valute + "/" + region)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            LOGGER.warning("Неверный url сайта для сайта myfin.ru");
        }

        parsingSiteMyfin(doc, valute);

        if (service.getBanks().isEmpty()) {
            try {
                doc = Jsoup.connect("https://www.banki.ru/products/currency/cash/" + valute + "/" + region + "/")
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get();
            } catch (IOException e) {
                LOGGER.warning("Неверный url сайта");
            }

            parsingBankru(doc);

        }
        if (service.getBanks().isEmpty()) {
            return "Тут о такой валюте не знают";
        }
        for (Bank bank : service.getBanks()) {
            message += (bank.getName() + "\nПокупка: "
                    + bank.getSell() + " RUB\nПродажа: " + bank.getBuy() + " RUB\n\n");
        }
        return message;
    }

    private void parsingSiteMyfin(org.jsoup.nodes.Document doc, String valute) {
        String message = "";
        double sell;
        double buy;
        int j = 0;
        int i = 0;
        try {
            Elements list2 = doc.select("tr.tr-turn");
            Elements list2name = list2.select("td.bank_name");
            Elements list2currency = list2.select("td." + valute.toUpperCase());
            while (j < list2name.size()) {
                message = list2name.get(j).text();
                sell = Double.parseDouble(list2currency.get(i).text());
                i++;
                buy = Double.parseDouble(list2currency.get(i).text());
                i++;
                service.addingBank(message, sell, buy);
                j++;
            }
        } catch (NullPointerException e) {
            LOGGER.warning("Нет таких тегов для сайта myfin.ru");
        }
    }

    private void parsingBankru(org.jsoup.nodes.Document doc) {
        Element element;
        int j = 0;
        int i = 0;
        double sell;
        double buy;
        String message;
        try {
            Elements lists = doc.select("a.font-bold");
            Elements list2 = doc.select("td.font-size-large");
            while (j < list2.size()) {
                element = list2.get(j);
                sell = Double.parseDouble(element.text().split(" ")[0].replaceAll(",", "."));
                j++;
                element = list2.get(j);
                buy = Double.parseDouble(element.text().split(" ")[0].replaceAll(",", "."));
                j++;
                message = lists.get(i).text();
                i++;
                service.addingBank(message, sell, buy);
            }
        } catch (NullPointerException e) {
            LOGGER.warning("Нет таких тегов для banki.ru");
        }
    }

    public String centrBank(String date, String valute) {
        LOGGER.info("Берём информацию о васюте в центробанке");
        String message = "";
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = null;
            dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(
                    new InputSource(
                            new URL("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date).openStream()));
            NodeList nodes = doc.getElementsByTagName("Valute");
            message += "";
            Node node;
            for (int i = 0; i < nodes.getLength(); i++) {
                node = nodes.item(i).getChildNodes().item(1);
                if (node.getTextContent().toLowerCase().contains(valute.substring(0, valute.length()))) {
                    message = message + (nodes.item(i).getChildNodes().item(3).getTextContent() + "\n"
                            + nodes.item(i).getLastChild().getTextContent() + " RUB\n\n");
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            LOGGER.warning("Либо нет такого тего у центробанка, либо неправильные теги");
        }
        return message;
    }

    public String bestBankSell(String urlvalute, String urlregion) {

        LOGGER.info("Вычисляем банк с лучшим курсом продажи банку указанной валюты в регионе");
        service = new BankService();
        double max = -1;
        Bank bestbank = null;
        currencyBank(urlvalute, urlregion);
        for (Bank bank : service.getBanks()) {
            if ((bank.getSell()) > max) {
                max = bank.getBuy();
                bestbank = bank;
            }
        }
        if (service.getBanks().isEmpty()) {
            return "Боюсь, что такого тут не делают";
        }
        return ("Лучшая продажа банку\n" + bestbank.getName() + "\nПокупка: " +
                bestbank.getSell() + " RUB\nПродажа: " + bestbank.getBuy() + " RUB\n\n");
    }

    public String bestBankBuy(String urlvalute, String urlregion) {

        LOGGER.info("Вычисляем банк с лучшим курсом покупки у банка указанной валюты в регионе");
        service = new BankService();
        double max = 100000;
        Bank bestbank = null;
        currencyBank(urlvalute, urlregion);
        for (Bank bank : service.getBanks()) {
            if ((bank.getBuy()) < max) {
                max = bank.getSell();
                bestbank = bank;
            }
        }
        if (service.getBanks().isEmpty()) {
            return "Боюсь, что такого тут не делают";
        }
        return ("Лучшая покупка у банка\n" + bestbank.getName() + "\nПокупка: " +
                bestbank.getSell() + " RUB\nПродажа: " + bestbank.getBuy() + " RUB\n\n");
    }
}
