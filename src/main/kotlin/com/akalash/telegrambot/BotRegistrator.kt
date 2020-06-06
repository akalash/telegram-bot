package com.akalash.telegrambot

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot


class BotRegistrator(val bots: List<LongPollingBot>) {
    companion object {
        private val log = LoggerFactory.getLogger(BotRegistrator::class.java)
    }

    fun register() {
        ApiContextInitializer.init();


        val botsApi = TelegramBotsApi()

        try {
            bots.forEach { it ->
                log.info("Register bot : " + it.botUsername)

                botsApi.registerBot(it)
            }
        } catch (e: TelegramApiException) {
            log.error("Bot registration was failed", e)
        }

    }
}