package com.example.product.logging

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.pattern.color.ANSIConstants

/**
 * A Logback converter that prints key-value pairs highlighting keys and values with the specified color.
 * Inspired by Logback's KeyValuePairConverter and ForegroundCompositeConverterBase.
 *
 * Example usage: %ckvp{magenta,yellow}
 */
internal class ColoredKeyValuePairConverter : ClassicConverter() {
    private val setDefaultColor =
        ANSIConstants.ESC_START + ANSIConstants.RESET + ANSIConstants.DEFAULT_FG + ANSIConstants.ESC_END

    override fun convert(event: ILoggingEvent): String {
        val kvpList = event.keyValuePairs
        if (kvpList == null || kvpList.isEmpty()) {
            return CoreConstants.EMPTY_STRING
        }

        val sb = StringBuilder()
        for (i in kvpList.indices) {
            val kvp = kvpList[i]
            if (i != 0) sb.append(' ')
            sb.append(ANSIConstants.ESC_START)
            sb.append(colorCode(firstOption))
            sb.append(ANSIConstants.ESC_END)
            sb.append(kvp.key)
            sb.append(setDefaultColor)
            sb.append('=')
            sb.append("\"")
            sb.append(ANSIConstants.ESC_START)
            sb.append(colorCode(optionList.getOrNull(1)))
            sb.append(ANSIConstants.ESC_END)
            sb.append(kvp.value)
            sb.append(setDefaultColor)
            sb.append("\"")
        }

        return sb.toString()
    }

    private fun colorCode(option: String?): String =
        when (option?.lowercase()) {
            "magenta" -> ANSIConstants.MAGENTA_FG
            "cyan" -> ANSIConstants.CYAN_FG
            "yellow" -> ANSIConstants.YELLOW_FG
            "green" -> ANSIConstants.GREEN_FG
            "red" -> ANSIConstants.RED_FG
            "blue" -> ANSIConstants.BLUE_FG
            "white" -> ANSIConstants.WHITE_FG
            "black" -> ANSIConstants.BLACK_FG
            else -> ANSIConstants.DEFAULT_FG
        }
}
