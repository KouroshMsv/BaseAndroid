package dev.kourosh.baseapp.model

import dev.kourosh.baseapp.enums.MessageType

internal data class Message(val messageType: MessageType, val message: String)