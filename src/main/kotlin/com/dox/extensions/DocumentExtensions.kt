package com.dox.extensions

/**
 * Sanitiza CPF ou CNPJ removendo qualquer caractere que não seja dígito ou letra.
 * Letras são convertidas para MAIÚSCULAS — o novo CNPJ alfanumérico (a partir
 * de julho/2026) aceita A-Z nas 12 primeiras posições. CPF segue numérico.
 *
 * Exemplos:
 *   "123.456.789-00" → "12345678900"
 *   "AB.CDE.FGH/0001-23" → "ABCDEFGH000123"
 */
fun String.sanitizeDocument(): String = filter { it.isLetterOrDigit() }.uppercase()
