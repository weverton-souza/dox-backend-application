package com.dox.domain.enum

enum class AiTier(val monthlyLimit: Int, val overagePriceCents: Int) {
    NONE(0, 0),
    DOX_IA(15, 900),
    DOX_IA_PRO(40, 1200)
}
