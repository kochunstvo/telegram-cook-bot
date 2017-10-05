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

    private val recipeList = ArrayList<String>()
    private var breakfastList = ArrayList<String>()
    private var lunchList = ArrayList<String>()
    private var dinnerList = ArrayList<String>()

    private val cookCite = "https://www.gastronom.ru"

    init {
        val quickDinnerPage = "/recipe/group/2998/chto-prigotovit-na-uzhin-bystro"
        val body = Jsoup.connect(cookCite + quickDinnerPage).get().body()
        val feedRow = body.getElementsByClass("feed").addClass("row").first().children()
        dinnerList.addAll(feedRow.map { cookCite + it.select("a[href]").first().attr("href") })
        recipeList.addAll(dinnerList)
    }

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        if (message != null && message.hasText()) {
            val text = message.text
            when (text) {
                "/start" -> {
                    sendMsg(message, "Здравствуй, " + message.chat.firstName)
                    sendMsg(message, "Случайный рецепт - /recipe\n" +
                            "Случайный рецепт завтрака - /breakfast\n" +
                            "Случайный рецепт обеда - /lunch\n" +
                            "Случайный рецепт ужина - /dinner\n")
                }
                "/help" -> sendMsg(message, "Случайный рецепт - /recipe\n"
                        + "Случайный рецепт завтрака - /breakfast\n"
                        + "Случайный рецепт обеда - /lunch\n"
                        + "Случайный рецепт ужина - /dinner\n")
                "/recipe" -> {
                    sendMsg(message, getRandomRecipe(recipeList))
                    sendMsg(message, "Если рецепт не подошел - отправь $text еще раз")
                }
                "/dinner" -> {
                    sendMsg(message, getRandomRecipe(dinnerList))
                    sendMsg(message, "Если рецепт не подошел - отправь $text еще раз")
                }
                "/lunch", "/breakfast" -> sendMsg(message, "Я пока не знаю ни одного рецепта для этой категории")
                else -> sendMsg(message, "Я не знаю как на это ответить :c")
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

    private fun getRandomRecipe(recipes: List<String>): String {
        return recipes[Random().nextInt(recipes.size - 1)]
    }

    override fun getBotToken(): String {
        return botToken
    }

    override fun getBotUsername(): String {
        return botUsername
    }
}