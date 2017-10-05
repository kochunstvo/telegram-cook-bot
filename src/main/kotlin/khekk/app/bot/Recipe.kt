package khekk.app.bot

/**
 * @author d.khekk
@since 05.10.2017
 */
class Recipe(private val name: String,
             private val ingredients: String,
             private val cookTime: String,
             private val url: String) {

    override fun toString(): String {
        return "$name\n" +
                if (!cookTime.isBlank()) "Примерное время приготовления: $cookTime мин.\n"
                else {
                    ""
                } +
                "Ингридиенты: $ingredients\n" +
                url
    }
}