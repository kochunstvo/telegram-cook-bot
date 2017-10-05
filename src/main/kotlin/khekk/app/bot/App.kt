package khekk.app.bot

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi

/**
 * @author d.khekk
@since 05.10.2017
 */

fun main(args: Array<String>) {
    ApiContextInitializer.init()
    TelegramBotsApi().registerBot(CookBot())
}