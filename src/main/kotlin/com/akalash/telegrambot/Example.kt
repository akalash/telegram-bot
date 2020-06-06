package com.akalash.telegrambot

import org.slf4j.LoggerFactory

fun main(args: Array<String>) {

    val botProperties = BotProperties()
    botProperties.key = "1072307633:AAFJPiMHD5X9uCeGJ_nhTHTXy92Xz70pEEY"
    botProperties.name = "AKalashTestBot"

    val helloCommand = HelloCommand()

    val controlCommandProcessor = ControlCommandProcessor(
            nameBot = botProperties.name!!,
            commands = mapOf(
                    helloCommand.name() to helloCommand
            )
    )

    val testBot = CustomTelegramBot(botProperties, controlCommandProcessor)

    BotRegistrator(listOf(testBot)).register()

    println("BOT REGISTERED")

    Thread.sleep(60 * 1000)
}

class HelloCommand : Command {
    override fun name(): String {
        return "/hello"
    }

    companion object {
        private val log = LoggerFactory.getLogger(HelloCommand::class.java)
    }

    override fun execute(outerUserId: String, args: Map<String, String>): CommandAnswer {
        val name = args["name"]
                ?: return CommandAnswer.finish(
                        "Пожалуйста, введите введите имя после названия комманды",
                        listOf("World!" to " ${name()} name=World!", "Other" to " ${name()} name=Other")
                )

        return CommandAnswer.finish("Привет $name")
    }

    override fun expectedParams(): List<String> {
        return listOf("name")
    }
}