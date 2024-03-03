package com.example.product.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import com.github.loki4j.logback.json.AbstractFieldJsonProvider
import com.github.loki4j.logback.json.JsonEventWriter

class KeyValuePairLokiJsonProvider : AbstractFieldJsonProvider() {
    init {
        fieldName = "kvp_"
    }

    override fun canWrite(event: ILoggingEvent): Boolean {
        return !event.keyValuePairs.isNullOrEmpty()
    }

    override fun writeTo(
        writer: JsonEventWriter,
        event: ILoggingEvent,
        startWithSeparator: Boolean,
    ): Boolean {
        val keyValuePairs = event.keyValuePairs
        var firstFieldWritten = false
        for (pair in keyValuePairs) {
            val key = pair.key
            val value = pair.value
            // skip empty records
            if (key == null || value == null) continue

            if (startWithSeparator || firstFieldWritten) writer.writeFieldSeparator()
            writer.writeStringField(fieldName + key, value.toString())
            firstFieldWritten = true
        }
        return firstFieldWritten
    }

    override fun writeExactlyOneField(
        writer: JsonEventWriter,
        event: ILoggingEvent,
    ) {
        throw UnsupportedOperationException(
            "KeyValuePairJsonProvider can write an arbitrary number of fields. " +
                "`writeExactlyOneField` should never be called for KeyValuePairJsonProvider.",
        )
    }
}
