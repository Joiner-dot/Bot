import org.glassfish.grizzly.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class BankService {

    private BankCurrency bankCurrency = new BankCurrency();
    private List<Pair<String, String>> region = new ArrayList<>();
    private List<Pair<String, String>> valute = new ArrayList<>();


    BankService() {
        region.add(new Pair<>("Санкт-Петербургъ", "sankt-peterburg"));
        region.add(new Pair<>("Барнаулъ", "barnaul"));
        region.add(new Pair<>("Москва", "moskva"));
        region.add(new Pair<>("Благовещенскъ", "blagovesschensk"));
        region.add(new Pair<>("Астрахань", "astrahan~"));
        region.add(new Pair<>("Белгородъ", "belgorod"));
        region.add(new Pair<>("Брянскъ", "bryansk"));
        region.add(new Pair<>("Владимиръ", "bryansk"));
        region.add(new Pair<>("Волгоградъ", "volgograd"));
        region.add(new Pair<>("Вологда", "vologda"));
        region.add(new Pair<>("Воронежъ", "voronezh"));
        region.add(new Pair<>("Иваново", "ivanovo"));
        region.add(new Pair<>("Иркутскъ", "irkutsk"));
        region.add(new Pair<>("Калининградъ", "kaliningrad"));
        region.add(new Pair<>("Краснодаръ", "krasnodar"));
        region.add(new Pair<>("Екатеринбургъ", "ekaterinburg"));
        region.add(new Pair<>("Ростовъ", "rostov-na-donu"));
        region.add(new Pair<>("Уфа", "ufa"));
        region.add(new Pair<>("Казань", "kazan"));
        region.add(new Pair<>("Тюмень", "tumen"));
        region.add(new Pair<>("Челябинскъ", "chelyabinsk"));
        region.add(new Pair<>("Нижний Новгородъ", "nizhniy-novgorod"));
        region.add(new Pair<>("Самара", "samara"));
        region.add(new Pair<>("Махачкала", "mahachkala"));
        region.add(new Pair<>("Красноярскъ", "krasnoyarsk"));
        region.add(new Pair<>("Ставрополь", "stavropol~"));
        region.add(new Pair<>("Новосибирскъ", "novosibirsk"));
        region.add(new Pair<>("Владивостокъ", "vladivostok"));
        region.add(new Pair<>("Томскъ", "tomsk"));
        region.add(new Pair<>("Омскъ", "omsk"));
        valute.add(new Pair<>("Доллар", "usd"));
        valute.add(new Pair<>("Евро", "eur"));
        valute.add(new Pair<>("Юань", "cny"));
        valute.add(new Pair<>("Фунт", "gbp"));
        valute.add(new Pair<>("Йена", "jpy"));
        valute.add(new Pair<>("Иена", "jpy"));
    }


    /**
     * Добавление банка и проверка на повторение
     */


    private boolean checkRegion(String name, Pair<String, String> pair) {

        if (name.toLowerCase()
                .contains(pair.getFirst().toLowerCase().substring(0, pair.getFirst().length() - 1))
                && !pair.getFirst().equals("Уфа")
                && !pair.getFirst().equals("Омскъ")) {
            return true;
        } else if (pair.getFirst().equals("Москва")
                && name.toLowerCase().contains(pair.getFirst().toLowerCase().substring(0, 4))) {
            return true;
        } else if (pair.getFirst().equals("Нижний Новгородъ")
                && name.toLowerCase().contains("нижн")
                && name.toLowerCase().contains("новгород")) {
            return true;
        } else if (pair.getFirst().equals("Омскъ")
                && (name.toLowerCase().contains(" " + pair.getFirst().toLowerCase().substring(0, 4))
                || name.toLowerCase().indexOf(pair.getFirst().toLowerCase().substring(0, 4)) == 0)) {
            return true;
        } else if (pair.getFirst().equals("Уфа")
                && (name.toLowerCase().contains("уфа")
                || name.toLowerCase().contains("уфе")
                || name.toLowerCase().contains("уфи")
                || name.toLowerCase().contains("уфы"))) {
            return true;
        }
        return false;
    }

    /**
     * Перевод названия банка в url формат
     */
    private List<Pair<String, String>> nameOfRegion(String name) {


        List<Pair<String, String>> urlname = new ArrayList<>();

        for (Pair<String, String> pair : region) {

            if (checkRegion(name, pair)) {
                urlname.add(pair);
            }
        }
        return urlname;
    }

    public String listOfRegion() {
        String name = "";
        for (Pair<String, String> pair : region) {
            if (pair.getFirst().contains("ъ")) {
                name += pair.getFirst().substring(0, pair.getFirst().length() - 1) + "\n";
            } else {
                name += pair.getFirst() + "\n";
            }
        }
        return name;
    }

    /**
     * Аналогично с валютой
     */
    private Pair<String, String> nameOfValute(String name) {
        for (int i = 0; i < valute.size(); i++) {
            if (name.toLowerCase()
                    .contains(valute.get(i).getFirst().substring(0, valute.get(i).getFirst().length() - 1).toLowerCase())
                    || name.toLowerCase().contains(valute.get(i).getSecond().toLowerCase())) {
                return valute.get(i);
            }

        }
        return new Pair<>("xz", "xz");
    }

    /**
     * Курс валют по региону
     *
     * @param name
     */
    public String currencyofRegion(String name) {
        Pair<String, String> urlvalue = nameOfValute(name);
        List<Pair<String, String>> urlregion = nameOfRegion(name);
        String answer = "";
        if (urlvalue.getFirst().equals("xz")) {
            return "Не понимаю валюту";
        }
        if (urlregion.isEmpty()) {
            return "Не понимаю город";
        }
        for (int i = 0; i < urlregion.size(); i++) {
            if (urlregion.get(i).getFirst().contains("ъ")) {
                answer += urlregion.get(i).getFirst().substring(0, urlregion.get(i).getFirst().length() - 1)
                        + "\n" + urlvalue.getFirst()
                        + "\n" + bankCurrency.currencyOfBanks(urlvalue.getSecond(), urlregion.get(i).getSecond())
                        + "\n";
            } else {
                answer += urlregion.get(i).getFirst() + "\n" + urlvalue.getFirst() +
                        "\n" + bankCurrency.currencyOfBanks(urlvalue.getSecond(), urlregion.get(i).getSecond())
                        + "\n";
            }
        }
        return answer;
    }

    /**
     * Лучший банк по курсу в регионе
     */
    public String bestBankCurrencySell(String name) {
        Pair<String, String> urlvalue = nameOfValute(name);
        List<Pair<String, String>> urlregion = nameOfRegion(name);
        if (urlvalue.getFirst().equals("xz")) {
            return "Не понимаю валюту";
        }
        if (urlregion.isEmpty()) {
            return "Не понимаю Регион";
        }
        String answer = "";
        for (int i = 0; i < urlregion.size(); i++) {
            if (urlregion.get(i).getFirst().contains("ъ")) {
                answer += urlregion.get(i).getFirst().substring(0, urlregion.get(i).getFirst().length() - 1) + "\n" + urlvalue.getFirst() +
                        "\n" + bankCurrency.bestBankSell(urlvalue.getSecond(), urlregion.get(i).getSecond())
                        + "\n";
            } else {
                answer += urlregion.get(i).getFirst() + "\n" + urlvalue.getFirst() +
                        "\n" + bankCurrency.bestBankSell(urlvalue.getSecond(), urlregion.get(i).getSecond())
                        + "\n";
            }
        }
        return answer;
    }

    public String bestBankCurrencyBuy(String name) {
        Pair<String, String> urlvalue = nameOfValute(name);
        List<Pair<String, String>> urlregion = nameOfRegion(name);
        if (urlvalue.getFirst().equals("xz")) {
            return "Не понимаю валюту";
        }
        if (urlregion.isEmpty()) {
            return "Не понимаю Регион";
        }
        String answer = "";
        for (int i = 0; i < urlregion.size(); i++) {
            if (urlregion.get(i).getFirst().contains("ъ")) {
                answer += urlregion.get(i).getFirst().substring(0, urlregion.get(i).getFirst().length() - 1) + "\n" + urlvalue.getFirst() +
                        "\n" + bankCurrency.bestBankBuy(urlvalue.getSecond(), urlregion.get(i).getSecond())
                        + "\n";
            } else {
                answer += urlregion.get(i).getFirst() + "\n" + urlvalue.getFirst() +
                        "\n" + bankCurrency.bestBankBuy(urlvalue.getSecond(), urlregion.get(i).getSecond())
                        + "\n";
            }
        }
        return answer;
    }

    public String centerBank(String date, String valute) {
        Pair<String, String> urlvalute = nameOfValute(valute);
        String answer = bankCurrency.centreBank(date, urlvalute.getSecond());
        if (urlvalute.getFirst().equals("xz")) {
            return "Не понимаю валюту";
        }
        if (answer.equals("")) {
            return "Хмм, я ничего не смог найти";
        }
        if (urlvalute.getSecond().equalsIgnoreCase("CNY")
                || urlvalute.getSecond().equalsIgnoreCase("JPY")) {
            return 10 + " " + answer;
        }
        return answer;

    }
}
