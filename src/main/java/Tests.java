import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Tests {
    Bot bot = new Bot();
    String[] splitdouble;
    BankCurrency bankCurrency = null;
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
        Assert.assertEquals("Доллар", splitname[1]);
        splitdouble = splitname[3].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
        splitdouble = splitname[4].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
        splitdouble = splitname[7].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
    }

    @Test
    public void bestBankTest() {
        bankService = new BankService();
        String message = bankService.bestBankCurrency("лучший доллар в санкт-петербурге");
        String[] splitname = message.split("\n");
        Assert.assertEquals("Санкт-Петербург", splitname[0]);
        Assert.assertEquals("Доллар", splitname[1]);
        splitdouble = splitname[3].split(" ");
        Double.parseDouble(splitdouble[1]);
        splitdouble = splitname[4].split(" ");
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
}
