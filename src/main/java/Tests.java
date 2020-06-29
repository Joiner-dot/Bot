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
        bankCurrency = new BankCurrency();
        String name = bankCurrency.centrBank("28.06.2020", "иен");
        String[] splitname = name.split("\n");
        Assert.assertEquals("Центробанк", splitname[0]);
        Assert.assertEquals("Фунт стерлингов Соединенного королевства", splitname[1]);
        splitdouble = splitname[2].split(" ");
        Double.parseDouble(splitdouble[0].replaceAll(",", "."));
        Assert.assertEquals("Белорусский рубль", splitname[4]);
        splitdouble = splitname[5].split(" ");
        Double.parseDouble(splitdouble[0].replaceAll(",", "."));
        Assert.assertEquals("Доллар США", splitname[7]);
        splitdouble = splitname[8].split(" ");
        Double.parseDouble(splitdouble[0].replaceAll(",", "."));
        Assert.assertEquals("Евро", splitname[10]);
    }

    @Test
    public void currencyOfRegionBank() {
        bankCurrency = new BankCurrency();
        String name = bankCurrency.currencyBank("usd", "sankt-peterburg");
        String[] splitname = name.split("\n");
        Assert.assertEquals("SANKT-PETERBURG", splitname[0]);
        Assert.assertEquals("USD", splitname[1]);
        splitdouble = splitname[3].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
        splitdouble = splitname[4].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
        splitdouble = splitname[7].split(" ");
        Double.parseDouble(splitdouble[1].replaceAll(",", "."));
    }

    @Test
    public void bestBankTest() {
        bankCurrency = new BankCurrency();
        String message = bankCurrency.bestBank("usd", "sankt-peterburg");
        String[] splitname = message.split("\n");
        Assert.assertEquals("SANKT-PETERBURG", splitname[0]);
        Assert.assertEquals("USD", splitname[1]);
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
