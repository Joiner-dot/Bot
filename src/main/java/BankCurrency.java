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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class BankCurrency {

    private static final Logger LOGGER = Logger.getLogger(BankCurrency.class.getName());

    /**
     * Вычисляет набор из неболее 5 банков региона и передает в новый banks
     *
     * @param valute
     * @param region
     */
    public String currencyOfBanks(String valute, String region) {
        StringBuilder message = new StringBuilder();
        List<Bank> banks = banksOfRegion(valute, region);
        if (banks.isEmpty()) {
            return "Тут о такой валюте не знают";
        }
        for (Bank bank : banks) {
            message.append(bank.getName()).append("\nПокупка: ").append(bank.getSell()).append(" RUB\nПродажа: ")
                    .append(bank.getBuy()).append(" RUB\n\n");
        }
        return message.toString();
    }


    public String bestBankSell(String urlvalute, String urlregion) {
        LOGGER.info("Вычисляем банк с лучшим курсом продажи банку указанной валюты в регионе");
        List<Bank> banks = banksOfRegion(urlvalute, urlregion);
        double max = -1;
        Bank bestbank = null;
        banksOfRegion(urlvalute, urlregion);
        for (Bank bank : banks) {
            if ((bank.getSell()) > max) {
                max = bank.getSell();
                bestbank = bank;
            }
        }
        if (bestbank != null) {
            return ("Лучшая продажа банку\n" + bestbank.getName() + "\nПокупка: " +
                    bestbank.getSell() + " RUB\nПродажа: " + bestbank.getBuy() + " RUB\n\n");
        } else {
            return "Банков тут нет";
        }
    }


    public String bestBankBuy(String urlvalute, String urlregion) {
        LOGGER.info("Вычисляем банк с лучшим курсом покупки у банка указанной валюты в регионе");
        double max = 100000;
        Bank bestbank = null;
        List<Bank> banks = banksOfRegion(urlvalute, urlregion);
        for (Bank bank : banks) {
            if ((bank.getBuy()) < max) {
                max = bank.getBuy();
                bestbank = bank;
            }
        }
        if (bestbank != null) {
            return ("Лучшая покупка у банка\n" + bestbank.getName() + "\nПокупка: " +
                    bestbank.getSell() + " RUB\nПродажа: " + bestbank.getBuy() + " RUB\n\n");
        } else {
            return "Банков тут нет";
        }
    }


    public List<Bank> banksOfRegion(String valute, String region) {
        List<Bank> banks = new ArrayList<>();
        LOGGER.info("Пытаемся взять информацию о валюте в указанном регионе");
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect("https://ru.myfin.by/currency/" + valute + "/" + region)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            LOGGER.warning("Неверный url сайта для сайта myfin.ru");
        }
        parsingSiteMyfin(banks, doc, valute);
        if (banks.isEmpty()) {
            try {
                doc = Jsoup.connect("https://www.banki.ru/products/currency/cash/" + valute + "/" + region + "/")
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get();
            } catch (IOException e) {
                LOGGER.warning("Неверный url сайта");
            }
            parsingBankru(banks, doc);
        }
        return banks;
    }

    private void parsingSiteMyfin(List<Bank> banks, org.jsoup.nodes.Document doc, String valute) {
        String message;
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
                addingBank(banks, message, sell, buy);
                j++;
            }
        } catch (NullPointerException e) {
            LOGGER.warning("Нет таких тегов для сайта myfin.ru");
        }
    }

    private void parsingBankru(List<Bank> banks, org.jsoup.nodes.Document doc) {
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
                addingBank(banks, message, sell, buy);
            }
        } catch (NullPointerException e) {
            LOGGER.warning("Нет таких тегов для banki.ru");
        }
    }

    public String centreBank(String date, String valute) {
        LOGGER.info("Пытаемся взять информацию о валюте в центробанке");
        String message = "";
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder;
            dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(
                    new InputSource(
                            new URL("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date).openStream()));
            NodeList nodes = doc.getElementsByTagName("Valute");
            message += "";
            Node node;
            for (int i = 0; i < nodes.getLength(); i++) {
                node = nodes.item(i).getChildNodes().item(1);
                if (node.getTextContent().toLowerCase().contains(valute)) {
                    message += (nodes.item(i).getChildNodes().item(3).getTextContent() + "\n"
                            + nodes.item(i).getLastChild().getTextContent() + " RUB\n\n");
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            LOGGER.warning("Ошибка. Либо нет такого тега у центробанка, либо неправильные теги");
        }
        return message;
    }

    public void addingBank(List<Bank> banks, String name, double sell, double buy) {
        if (banks.size() < 5) {
            for (Bank bank : banks) {
                if (bank.getName().equalsIgnoreCase(name)) {
                    return;
                }
            }
            banks.add(new Bank(name, sell, buy));
        }
    }
}
