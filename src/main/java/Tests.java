import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Tests {
    Bot bot = new Bot();
    String[] splitdouble;
    BankService bankService = new BankService();

    @Test
    public void nameTest() {
        Assert.assertEquals("SonofFartherbot", new Bot().getBotUsername());
    }


    @Test
    public void tokendTest() {
        Assert.assertEquals("1274274612:AAFabI1uN8z-DDgvmGUjAeIXk5J3PWy7leI", new Bot().getBotToken());
    }

    @Test
    public void centerbankTest() {
        bankService = new BankService();
        String name = bankService.centerBank("28.06.2020", "иен");
        String[] splitname = name.split("\n");
        Assert.assertEquals("10 Японских иен", splitname[0]);
        splitdouble = splitname[1].split(" ");
        Double.parseDouble(splitdouble[0].replaceAll(",", "."));
    }

    @Test
    public void currencyOfRegionBank() {
        bankService = new BankService();
        String name = bankService.currencyofRegion("доллар в санкт-петербурге");
        String[] splitname = name.split("\n");
        Assert.assertEquals("Санкт-Петербург", splitname[0]);
        splitdouble = splitname[3].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
        splitdouble = splitname[4].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
        splitdouble = splitname[7].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
    }

    @Test
    public void bestBankSellTest() {
        bankService = new BankService();
        String message = bankService.bestBankCurrencySell("лучшая продажа банку доллара в санкт-петербурге");
        String[] splitname = message.split("\n");
        Assert.assertEquals("Лучшая продажа банку", splitname[2]);
        splitdouble = splitname[4].split(" ");
        Double.parseDouble(splitdouble[1]);
        splitdouble = splitname[5].split(" ");
        Double.parseDouble(splitdouble[1]);
    }

    @Test
    public void bestBankBuyTest() {
        bankService = new BankService();
        String message = bankService.bestBankCurrencyBuy("лучшая покупка у банка доллара в санкт-петербурге");
        String[] splitname = message.split("\n");
        Assert.assertEquals("Лучшая покупка у банка", splitname[2]);
        splitdouble = splitname[4].split(" ");
        Double.parseDouble(splitdouble[1]);
        splitdouble = splitname[5].split(" ");
        Double.parseDouble(splitdouble[1]);
    }


    @Test
    public void sendMessageTest() {
        try {
            bot.sendMsg(null, "");
            Assert.fail("Expected TelegramApiException");
        } catch (TelegramApiException thrown) {
            Assert.assertNotEquals("", thrown.getMessage());
        }
        try {
            bot.sendMsg("0", "");
            Assert.fail("Expected TelegramApiException");
        } catch (TelegramApiException thrown) {
            Assert.assertNotEquals("", thrown.getMessage());
        }
    }

    @Test
    public void addingBankTest ()
    {
        bankService = new BankService();
        bankService.addingBank("ВТБ",11.2,10);
        bankService.addingBank("ВТБ",11.2,10);
        Assert.assertEquals(1,bankService.getBanks().size());
    }

}
