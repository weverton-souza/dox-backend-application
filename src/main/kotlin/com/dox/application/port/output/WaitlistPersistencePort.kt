package com.dox.application.port.output

import com.dox.domain.waitlist.WaitlistEntry

interface WaitlistPersistencePort {
    fun saveIfNotExists(entry: WaitlistEntry): Boolean
}
