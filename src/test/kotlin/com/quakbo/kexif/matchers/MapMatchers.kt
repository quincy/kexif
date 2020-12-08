package com.quakbo.kexif.matchers

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher

fun <T, U> isEmptyMap(): Matcher<Map<T, U>> = object : Matcher<Map<T, U>> {
    override val description: String = "empty map"

    override fun invoke(actual: Map<T, U>): MatchResult {
        return if (actual.isEmpty()) MatchResult.Match else MatchResult.Mismatch("has mappings $actual")
    }
}

fun <T, U> hasElement(key: T, value: U): Matcher<Map<T, U>> = object : Matcher<Map<T, U>> {
    override val description: String = "a map with mapping $key -> $value"

    override fun invoke(actual: Map<T, U>): MatchResult {
        return when (val actualValue = actual[key]) {
            is ByteArray -> if (actualValue.contentEquals(value as ByteArray)) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
            is DoubleArray -> if (actualValue.contentEquals(value as DoubleArray)) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
            is FloatArray -> if (actualValue.contentEquals(value as FloatArray)) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
            is IntArray -> if (actualValue.contentEquals(value as IntArray)) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
            is LongArray -> if (actualValue.contentEquals(value as LongArray)) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
            is ShortArray -> if (actualValue.contentEquals(value as ShortArray)) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
            else -> if (actualValue == value) MatchResult.Match else MatchResult.Mismatch("mapping ($key -> $value) was not present.  map=$actual")
        }
    }
}