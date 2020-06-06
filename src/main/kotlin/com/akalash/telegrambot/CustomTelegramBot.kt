package com.akalash.telegrambot

import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class CustomTelegramBot(
        val botProperties: BotProperties,
        val tgControlCommandProcessor: ControlCommandProcessor
) : TelegramLongPollingBot(CustomDefaultOptions()) {

    fun sendMsg(targeId: String, msg: String, actions: List<Pair<String, String>> = listOf()) {
        val message = SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(targeId)
                .setReplyMarkup(inlineKeyboardMarkup(actions))
                .setText(msg)

        try {
            execute<Message, SendMessage>(message) // Call method to send the message
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun replaceMsg(targeId: String, messageId: Int, msg: String, actions: List<Pair<String, String>> = listOf()) {
        val message = EditMessageText().setChatId(targeId)
                .setMessageId(messageId)
                .setText(msg)
                .setReplyMarkup(inlineKeyboardMarkup(actions))

        try {
            execute(message) // Call method to send the message
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    private fun inlineKeyboardMarkup(actions: List<Pair<String, String>>): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()

        val chunkedSize = if (actions.isNotEmpty()) {
            if (actions[0].first.length < 6) 4 else 2
        } else 2

        inlineKeyboardMarkup.keyboard = actions.map {
            val button = InlineKeyboardButton(it.first)
            button.setCallbackData(it.second)
            button
        }.chunked(chunkedSize)
        return inlineKeyboardMarkup
    }

    override fun getBotUsername(): String {
        return botProperties.name!!
    }

    override fun getBotToken(): String {
        return botProperties.key!!
    }

    override fun onUpdateReceived(update: Update?) {
        // We check if the update has a message and the message has text
        if (update!!.hasMessage() && update.getMessage().hasText()) {
            val answer = tgControlCommandProcessor.execute(
                    update.message.chatId.toString(),
                    update.message.text
            )

            sendMsg(update.message.chatId.toString(), answer.text, answer.actions)
        } else if (update.hasCallbackQuery()) {
            val message = update.callbackQuery.message
            val answer = tgControlCommandProcessor.execute(
                    message.chatId.toString(),
                    update.callbackQuery.data
            )

            sendMsg(message.chatId.toString(), answer.text, answer.actions)
        }
    }
}

class CustomDefaultOptions : DefaultBotOptions() {

}