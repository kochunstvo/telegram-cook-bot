package khekk.app.bot

import org.jsoup.Jsoup
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.util.*

/**
 * @author d.khekk
@since 05.10.2017
 */
class CookBot : TelegramLongPollingBot() {

    private val botToken = "395516945:AAFjXHzlwdWU2SNWbvZPkedSlpw-OmgM1kc"
    private val botUsername = "random_recipe_bot"

    private val recipeList = ArrayList<Recipe>()

    private val cookCite = "http://tvoirecepty.ru/"

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
            when (text) {
                "/start" -> {
                    sendMsg(message, "Здравствуй, " + message.chat.firstName)
                    sendMsg(message, "Случайный рецепт - /recipe")
                }
                "/help" -> sendMsg(message, "Случайный рецепт - /recipe")
                "/recipe" -> {
                    val randomRecipe = getRandomRecipe(recipeList)
                    sendMsg(message, "$randomRecipe")
                    sendMsg(message, "Если рецепт не подошел - отправь $text еще раз")
                }
            }
        }
    }

    private fun sendMsg(message: Message, text: String) {
        val messageToSend = SendMessage()
        messageToSend.enableMarkdown(true)
        messageToSend.chatId = message.chatId!!.toString()
        messageToSend.text = text
        sendMessage(messageToSend)
    }

    private fun getRandomRecipe(recipeList: List<Recipe>): Recipe {
        return recipeList[Random().nextInt(recipeList.size)]
    }

    override fun getBotToken(): String {
        return botToken
    }

    override fun getBotUsername(): String {
        return botUsername
    }
}