package com.beibeilab.keepin.password

import java.util.Random

class PasswordGenerator(length: Int, ruleArray: BooleanArray) {
    private val uppercases = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercases = "abcdefghijklmnopqrstuvwxyz"
    private val numbers = "0123456789"
    private val others = "!@#$%^&*()_"

    private lateinit var sample: String
    private var length: Int = 0

    init {
        this.length = when {
            length < 4 -> 4
            length > 12 -> 12
            else -> length
        }

        createSample(ruleArray)
    }

    private fun createSample(ruleArray: BooleanArray) {
        val stringBuilder = StringBuilder()
        if (ruleArray[0]) {
            stringBuilder.append(uppercases)
        }

        if (ruleArray[1]) {
            stringBuilder.append(lowercases)
        }

        if (ruleArray[2]) {
            stringBuilder.append(numbers)
        }

        if (ruleArray[3]) {
            stringBuilder.append(others)
        }

        sample = stringBuilder.toString()
    }

    fun generate(): String {
        val random = Random(System.nanoTime())

        val stringBuilder = StringBuilder()
        for (i in 0 until length) {
            stringBuilder.append(sample[random.nextInt(sample.length)])
        }

        return stringBuilder.toString()
    }
}