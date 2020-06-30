import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.logging.Logger;


public class Bot extends TelegramLongPollingBot {

    private static final Logger LOGGER = Logger.getLogger(Bot.class.getName());
    private BankService bankService;
    private boolean flag = false;
    private String centerbankmessage;
    private boolean right = false;


    public void onUpdateReceived(Update update) {
        //Если чат 1 на 1
        if (update.getMessage().getChat().isUserChat()) {
            LOGGER.info("Сообщение прислано из чата одиночного");
            //Если пришло вложение
            if (update.getMessage().getText() == null) {
                LOGGER.info("Пришло вложение");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Я вас не понимаю, но вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи или покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: где лучшего всего купить доллары в москве ?" +
                                    "\nИли: курс евро у центробанка (далее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список городов их разных регионов командой /city ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Отправлено пустое сообщение");
                    e.printStackTrace();
                }
            }
            // Если уже указана валюта для центробанка
            if (flag) {
                right = true;
                LOGGER.info("Просьба указать дату в правильном формате");
                try {
                    flag = false;
                    bankService = new BankService();
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.centerBank(update.getMessage().getText().replaceAll(",", "."),
                                    centerbankmessage));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в просьбе указать дату в правильном формате");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().equals("/start")) {
                right = true;
                LOGGER.info("Просьба о помощи");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Здравствуйте, вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи или покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: где лучшего всего купить(продать) доллары в москве ?" +
                                    "\nИли: курс евро у центробанка (далее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список городов их разных регионов командой /city ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в просьбе о помощи");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().equals("/help")) {
                right = true;
                LOGGER.info("Просьба о помощи");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи и покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: где лучшего всего купить доллары в москве ?" +
                                    "\nИли: курс евро у центробанка (далее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список городов их разных регионов командой /city ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в просьбе о помощи");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().equals("/city")) {
                right = true;
                LOGGER.info("Вывод городов регионов");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.listOfRegion());
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в просьбе о помощи");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().toLowerCase().contains("центробанк")
                    || update.getMessage().getText().toLowerCase().contains("цб")) {
                right = true;
                LOGGER.info("Пользователь хочет узнать курс валюты в центробанке");
                flag = true;
                centerbankmessage = update.getMessage().getText();
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(), "Введите дату по такому же формату\n" +
                            "21.12.2012 ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в блоке с центробанков");
                    e.printStackTrace();
                }

            } else if (update.getMessage().getText().toLowerCase().contains("спасибо")) {
                right = true;
                LOGGER.info("Бота поблагодарили");
                try {
                    sendMsg(update.getMessage().getChatId().toString(), "Обращайтесь");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в ответе благодарности");
                    e.printStackTrace();
                }
            } else if (!update.getMessage().getText().toLowerCase().contains("лучш")
                    && !update.getMessage().getText().toLowerCase().contains("выгодн")) {
                right = true;
                LOGGER.info("Отправить курс валюты в регионе");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.currencyofRegion(update.getMessage().getText()));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке курса валюты в регионе");
                    e.printStackTrace();
                }
            } else if ((update.getMessage().getText().toLowerCase().contains("лучш")
                    || update.getMessage().getText().toLowerCase().contains("выгодн"))
                    && (update.getMessage().getText().toLowerCase().contains("купи")
                    || update.getMessage().getText().toLowerCase().contains("покуп")
                    || update.getMessage().getText().toLowerCase().contains("купл"))) {
                right = true;
                LOGGER.info("Отправить лушчий курс покупки валюты у банка в регионе");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.bestBankCurrencyBuy(update.getMessage().getText()));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке курса валюты в регионе");
                    e.printStackTrace();
                }
            }
            if ((update.getMessage().getText().toLowerCase().contains("лучш")
                    || update.getMessage().getText().toLowerCase().contains("выгодн"))
                    && update.getMessage().getText().toLowerCase().contains("прода")) {
                right = true;
                LOGGER.info("Отправить лушчий курс продажи валюты у банка в регионе");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.bestBankCurrencySell(update.getMessage().getText()));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке курса валюты в регионе");
                    e.printStackTrace();
                }
            }
            if (!right) {
                LOGGER.info("Непонятно что написано");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Я вас не понимаю, но вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи или покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: где лучшего всего купить доллары в москве ?" +
                                    "\nИли: курс евро у центробанка (далее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список городов их разных регионов командой /city ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Отправлено пустое сообщение");
                    e.printStackTrace();
                }
            }
            right = false;

            // Если чат групповой
        } else if (update.getMessage().getChat().isGroupChat()) {
            LOGGER.info("Сообщение из группового чата");
            if (flag) {
                right = true;
                LOGGER.info("Просьба указать дату в правильном формате");
                try {
                    flag = false;
                    bankService = new BankService();
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.centerBank(
                                    update.getMessage().getText().replaceAll(",", "."),
                                    centerbankmessage));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка блоке отправке просьбы даты");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().contains("/city")
                    && update.getMessage().getText().contains("@SonofFartherbot")) {
                right = true;
                bankService = new BankService();
                LOGGER.info("Просьба о вывести города регионов в групповом чате");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.listOfRegion());
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке городов");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().contains("/start")
                    && update.getMessage().getText().contains("@SonofFartherbot")) {
                right = true;
                LOGGER.info("Просьба о помощи в групповом чате");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Здравсвуйте, вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи или покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: курс евро у центробанка (да" +
                                    "лее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список г" +
                                    "ородов их разных регионов командой \"@SonofFartherbot /city\" ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке туториала");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().contains("/help")
                    && update.getMessage().getText().contains("@SonofFartherbot")) {
                right = true;
                LOGGER.info("Просьба о помощи в групповом чате");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи или покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: где лучшего всего купить доллары в москве ?" +
                                    "\nИли: курс евро у центробанка (да" +
                                    "лее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список г" +
                                    "ородов их разных регионов командой \"@SonofFartherbot /city\" ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке туториала");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().toLowerCase().contains("спасибо")
                    && update.getMessage().getText().contains("@SonofFartherbot")) {
                right = true;
                LOGGER.info("Благодарность в групповом чатее");
                try {
                    sendMsg(update.getMessage().getChatId().toString(), "Обращайтесь");
                } catch (TelegramApiException e) {
                    LOGGER.info("Ошибка в ответе на благодарность в групповом чате");
                    e.printStackTrace();
                }
            } else if ((update.getMessage().getText().toLowerCase().contains("центробанк")
                    || update.getMessage().getText().toLowerCase().contains("цб"))
                    && update.getMessage().getText().contains("@SonofFartherbot")) {
                right = true;
                LOGGER.info("Пользователь хочет узнать курс валюты в центробанке");
                flag = true;
                centerbankmessage = update.getMessage().getText();
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(), "Введите дату по такому же формату\n" +
                            " 21.12.2012");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке просьбы ввести дату");
                    e.printStackTrace();
                }

            } else if (update.getMessage().getText().contains("@SonofFartherbot") &&
                    !update.getMessage().getText().toLowerCase().contains("лучш")
                    && !update.getMessage().getText().toLowerCase().contains("выгодн")) {
                right = true;
                LOGGER.info("Отправка в гурупповой чат курса валюты по региону");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.currencyofRegion(update.getMessage().getText()));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке курса валюты по региону");
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().contains("@SonofFartherbot") &&
                    (update.getMessage().getText().toLowerCase().contains("лучш")
                            || update.getMessage().getText().toLowerCase().contains("выгодн"))
                    && (update.getMessage().getText().toLowerCase().contains("купи")
                    || update.getMessage().getText().toLowerCase().contains("покуп")
                    || update.getMessage().getText().toLowerCase().contains("купл"))) {
                right = true;
                LOGGER.info("Отправить в групповой чат лушчий курс покупки валюты у банка в регионе");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.bestBankCurrencyBuy(update.getMessage().getText()));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке курса валюты в регионе");
                    e.printStackTrace();
                }
            }
            if (update.getMessage().getText().contains("@SonofFartherbot")
                    && (update.getMessage().getText().toLowerCase().contains("лучш")
                    || update.getMessage().getText().toLowerCase().contains("выгодн"))
                    && update.getMessage().getText().toLowerCase().contains("прода")) {
                right = true;
                LOGGER.info("Отправить в групповой чат лушчий курс продажи валюты у банка в регионе");
                bankService = new BankService();
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            bankService.bestBankCurrencySell(update.getMessage().getText()));
                } catch (TelegramApiException e) {
                    LOGGER.warning("Ошибка в отправке курса валюты в регионе");
                    e.printStackTrace();
                }
            }
            System.out.println(right);
            if ((!right) && update.getMessage().getText().contains("@SonofFartherbot")) {
                LOGGER.info("Непонятно что написано");
                try {
                    sendMsg(update.getMessage().getChatId().toString(),
                            "Я вас не понимаю, но вы можете спросить меня какой курс валюты в" +
                                    " городе региона или какой лучший курс продажи или покупки валюты в городе региона\n" +
                                    "Или какой курс валюты в центробанке\n" +
                                    "\nНапример: курс доллара в казани" +
                                    "\nИли: где лучшего всего купить доллары в москве ?" +
                                    "\nИли: курс евро у центробанка (далее нужно будет указать дату следующим сообщением) \n" +
                                    "Кроме того, можно узнать список городов их разных регионов командой /city ");
                } catch (TelegramApiException e) {
                    LOGGER.warning("Отправлено пустое сообщение");
                    e.printStackTrace();
                }
            }
            right = false;
        }
    }

    //Отправка сообщения
    public synchronized void sendMsg(String chatId, String s) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        execute(sendMessage);

    }

    @Override
    public String getBotUsername() {
        return "SonofFartherbot";
    }


    @Override
    public String getBotToken() {
        return "1274274612:AAFabI1uN8z-DDgvmGUjAeIXk5J3PWy7leI";
    }

}
