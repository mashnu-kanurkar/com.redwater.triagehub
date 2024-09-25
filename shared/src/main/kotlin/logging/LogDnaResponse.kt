package com.redwater.logging

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent


data class LogDnaResponse(
    val error: String,
    val code: String,
    val status: String
)

data class LogDnaRequestBody(
    val lines: List<Line>
)
data class Line(
    val timestamp: String,
    val line: String,
    val app: String,
    val level: String,
    val meta: String
)


