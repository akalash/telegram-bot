package com.akalash.telegrambot

import org.slf4j.LoggerFactory

open class ControlCommandProcessor(
        val nameBot: String,
        val commands: Map<String, Command>) {

    companion object {
        private val log = LoggerFactory.getLogger(ControlCommandProcessor::class.java)
    }

    fun execute(chatId: String, args: String): CommandAnswer {
        log.info("Starting to execute control command")
        if (args.isBlank()) {
            log.warn("Args is blank. Execution is skipped")
            return CommandAnswer.finish("Args is blank. Execution is skipped")
        }

        val controlUserId = chatId

        val argsList = args.trim().split(" ").toMutableList()

        var commandStr = argsList[0]

        if (commandStr.contains("@")) {
            if (commandStr.substring(commandStr.indexOf("@") + 1) != nameBot) {
                log.warn("Skip command due to it is not suitable for this bot")
                return CommandAnswer.finish("")
            } else {
                commandStr = commandStr.substring(0, commandStr.indexOf("@"))
            }
        }

        val command = ParsedCommand(commandStr, argsList.subList(1, argsList.size))

        log.info("Trying to execute: " + command)

        val controlCommand = commands[command.command]

        if (controlCommand == null) {
            log.warn("Command not found")
            return CommandAnswer.finish("Command not found")
        }

        try {
            return controlCommand.execute(controlUserId, ParamsParsers(controlCommand.expectedParams()).parseParams(command.args))
        } catch (e: Exception) {
            log.error("Error during command execution", e)

            return CommandAnswer.finish("В момент выполнения произошла ошибка, попробуйте изменить параметры ввода")
        }
    }
}

data class ParsedCommand(
        val command: String,
        val args: MutableList<String>
)