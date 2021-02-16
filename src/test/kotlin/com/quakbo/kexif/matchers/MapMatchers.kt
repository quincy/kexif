package com.quakbo.kexif.matchers

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher

fun <T, U> isEmptyMap(): Matcher<Map<T, U>> = object : Matcher<Map<T, U>> {
    override val description: String = "empty map"

    override fun invoke(actual: Map<T, U>): MatchResult {
        return if (actual.isEmpty()) MatchResult.Match else MatchResult.Mismatch("has mappings $actual")
    }
}

fun <T, U> hasElement(key: T, matcher: Matcher<U>): Matcher<Map<T, U>> = object : Matcher<Map<T, U>> {
    override val description: String = "has $key -> ${matcher.description}"

    @Suppress("UNCHECKED_CAST")
    override fun invoke(actual: Map<T, U>): MatchResult {
        return when (val actualValue = actual[key]) {
            is ByteArray -> matcher.invoke(actualValue)
            is DoubleArray -> matcher.invoke(actualValue)
            is FloatArray -> matcher.invoke(actualValue)
            is IntArray -> matcher.invoke(actualValue)
            is LongArray -> matcher.invoke(actualValue)
            is ShortArray -> matcher.invoke(actualValue)
            else -> matcher.invoke(actualValue as U)
        }
    }
}

// TODO put these in a better spot
fun contentsEqual(expected: ByteArray): Matcher<ByteArray?> = object : Matcher<ByteArray?> {
    override val description: String = "has same contents as ${expected.joinToString()}"

    override fun invoke(actual: ByteArray?): MatchResult {
        return if (actual.contentEquals(expected)) MatchResult.Match else MatchResult.Mismatch("was ${actual?.joinToString() ?: "null"}")
    }
}

// TODO put these in a better spot
fun <T> contentsEqual(expected: Array<T>): Matcher<Array<T>?> = object : Matcher<Array<T>?> {
    override val description: String = "has same contents as ${expected.joinToString()}"

    override fun invoke(actual: Array<T>?): MatchResult {
        return if (actual.contentEquals(expected)) MatchResult.Match else MatchResult.Mismatch("was ${actual?.joinToString() ?: "null"}")
    }
}
