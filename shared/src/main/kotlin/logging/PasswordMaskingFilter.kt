package com.redwater.logging

import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.classic.turbo.TurboFilter

class PasswordMaskingFilter : TurboFilter() {

    private val passwordRegex = "(?i)(password|pwd)[^\\s]*\\s*[:=]\\s*([^\\s]+)".toRegex()
    private val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

    override fun decide(marker: org.slf4j.Marker?,
                        logger: ch.qos.logback.classic.Logger?,
                        level: ch.qos.logback.classic.Level?,
                        format: String?,
                        params: Array<out Any>?,
                        t: Throwable?): FilterReply {

        var modifiedMessage = format

        // Mask password if found
        if (format != null && passwordRegex.containsMatchIn(format)) {
            modifiedMessage = format.replace(passwordRegex, "$1: *****")
        }

        // Mask email if found
        if (modifiedMessage != null && emailRegex.containsMatchIn(modifiedMessage)) {
            modifiedMessage = modifiedMessage.replace(emailRegex) { matchResult ->
                maskEmail(matchResult.value)
            }
        }

        // If the message was modified, log the masked message
        if (modifiedMessage != format && logger != null && level != null) {
            logger.callAppenders(
                LoggingEvent(
                    logger.name, logger, level, modifiedMessage, t, params
                )
            )
            return FilterReply.DENY // Deny the original unmasked log message
        }

        return FilterReply.NEUTRAL // Continue with normal logging if no sensitive data is found
    }

    // Function to mask the email, keeping the first and last character before '@' visible
    private fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts[0].length <= 2) {
            return email // Not enough characters to mask
        }
        val localPartMasked = parts[0].first() + "*".repeat(parts[0].length - 2) + parts[0].last()
        return "$localPartMasked@${parts[1]}"
    }
}
