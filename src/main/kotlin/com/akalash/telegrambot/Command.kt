package com.akalash.telegrambot

interface Command {
    fun name(): String

    fun execute(outerUserId: String, args: Map<String, String>): CommandAnswer

    fun expectedParams() : List<String>
}

enum class ControlStatus {
    IN_PROGRESS,
    FINISH
}

data class CommandAnswer(
        val status: ControlStatus,
        val args: List<String>,
        val text: String,
        val actions: List<Pair<String, String>>
) {
    companion object {
        fun inProgress(text: String, actions: List<Pair<String, String>> = listOf(), args: List<String> = listOf()) =
                CommandAnswer(ControlStatus.IN_PROGRESS, args, text, actions)

        fun finish(text: String, actions: List<Pair<String, String>> = listOf()) = CommandAnswer(ControlStatus.FINISH, listOf(), text, actions)
    }
}

class ParamsParsers(val expectedParams: List<String>) {
    fun parseParams(arguments: List<String>): Map<String, String> {
        val params = mutableMapOf<String, String>()

        val unstructuredParams: MutableList<String> = mutableListOf()

        val iter = arguments.iterator()

        while (iter.hasNext()) {
            val next = iter.next()

            if (next.contains("=")) {
                val arg = next.split("=")
                params[arg[0]] = arg[1]
            } else {
                unstructuredParams.add(next)
            }
        }

        expectedParams.forEach {
            if(!params.containsKey(it) && unstructuredParams.isNotEmpty()){
                params[it] = unstructuredParams.removeAt(0)
            }
        }

        return params
    }
}