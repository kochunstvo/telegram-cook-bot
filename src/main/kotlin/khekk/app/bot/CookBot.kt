package khekk.app.bot

import org.jsoup.Jsoup
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author d.khekk
 * @since 05.10.2017
 */
class CookBot : TelegramLongPollingBot() {

    private val botToken = "395516945:AAFjXHzlwdWU2SNWbvZPkedSlpw-OmgM1kc"
    private val botUsername = "random_recipe_bot"

    private val recipeList = ArrayList<Recipe>()

    private val cookCite = "http://tvoirecepty.ru/"

    private val stickers: List<String> = listOf("CAADAgADPgEAAooSqg4ZkiyN7KmJ7AI",
            "CAADAgADXQEAAooSqg7e1UbQcaOvXgI",
            "CAADAgADKgEAAooSqg5ZUX2YNQa2xQI",
            "CAADAgADPwADfeyYB7uSlAvXkEPfAg",
            "CAADAgADNQADfeyYBzOQm5kjpH5WAg",
            "CAADAgADWwADfeyYB0o4lPdQPyW-Ag")

    init {
        val quickDinnerPage = "recepty/na-kazhdy-den"
        val body = Jsoup.connect(cookCite + quickDinnerPage).get().body()
        val foodRow = body.getElementsByClass("recipe").addClass("recipe-teaser")
        recipeList.addAll(foodRow.map {
            Recipe(it.attr("data-title").replace("&quot;", ""),
                    it.attr("data-ingreds").split(";").joinToString(postfix = "."),
                    it.attr("data-cooktime"),
                    cookCite + it.attr("data-url"))
        })
    }

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        if (message != null && message.hasText()) {
            val text = message.text
            val format = SimpleDateFormat("dd.MM.YY - HH:mm:ss")
            println("${format.format(Date())} - ${message.chat.firstName} ${message.chat.lastName}: ${message.text}")
            when {
                text.toLowerCase().contains("привет") or
                        text.toLowerCase().contains("здравствуй") -> {
                    sendMsg(message, "Здравствуй, " + message.chat.firstName)
                    sendSticker(message, getRandom(stickers) as String)
                }
                text == "/start" -> {
                    sendMsg(message, "Здравствуй, " + message.chat.firstName)
                    sendMsg(message, "Отправь мне /recipe и я покажу тебе случайный рецепт")
                }
                text == "/help" -> sendMsg(message, "Случайный рецепт - /recipe")
                text == "/recipe" -> {
                    val randomRecipe = getRandom(recipeList)
                    sendMsg(message, "$randomRecipe")
                    sendMsg(message, "Если рецепт не подошел - отправь $text еще раз")
                }
                text == "/exit" -> {
                    sendMsg(message, "Отключаюсь")
                    System.exit(0)
                }
            }
        }
    }

    private fun sendMsg(message: Message, text: String) {
        val messageToSend = SendMessage()
        messageToSend.enableMarkdown(true)
        messageToSend.chatId = message.chatId.toString()
        messageToSend.text = text
        sendApiMethod(messageToSend)
    }

    private fun sendSticker(message: Message, sticker: String) {
        val sendSticker = SendSticker()
        sendSticker.setChatId(message.chatId)
        sendSticker.sticker = sticker
        sendSticker(sendSticker)
    }

    private fun getRandom(list: List<Any>): Any {
        return list[Random().nextInt(list.size)]
    }

    override fun getBotToken(): String {
        return botToken
    }

    override fun getBotUsername(): String {
        return botUsername
    }
}