package com.dox.application.port.output

import com.dox.domain.enum.Vertical
import com.dox.domain.model.AiInstruction

interface AiInstructionPort {
    fun findActiveByTypeAndVertical(type: String, vertical: Vertical): AiInstruction?
    fun findActiveByType(type: String): AiInstruction?
}
