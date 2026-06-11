package com.yogesh.auraai.core.common

fun String.truncate(maxLength: Int): String {
    if (length <= maxLength) return this
    return take(maxLength - 1) + "…"
}

fun Long.toTitleFromMessage(): String = "New Chat"
