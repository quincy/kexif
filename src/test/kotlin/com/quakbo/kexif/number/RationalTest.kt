package com.quakbo.kexif.number

import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConverter
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource


internal class RationalTest {
    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource(value = [
        "0,0",
        "1,1",
        "1/1,1",
        "1/2,1/2",
        "2/4,1/2",
        "5/3,5/3",
        "-1/4,-1/4",
        "4/2,2",
    ])
    internal fun `rationals can be parsed from their String representation`(s: String, @ConvertWith(RationalConverter::class) expected: Rational) {
        assertThat(Rational.parse(s), equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0}/{1} -> {2}/{3}")
    @CsvSource(value = [
        "0,1,0,1",
        "1,1,1,1",
        "2,2,1,1",
        "2,3,2,3",
        "3,2,3,2",
        "3,9,1,3",
        "-3,9,-1,3",
        "10,5,2,1",
        "11,5,11,5",
        "-11,5,-11,5",
    ])
    internal fun `new rational is created in simplest terms`(numerator: Int, denominator: Int, expectedNumerator: Int, expectedDenominator: Int) {
        assertThat(
            Rational(numerator, denominator),
            allOf(
                has(Rational::numerator, equalTo(expectedNumerator)),
                has(Rational::denominator, equalTo(expectedDenominator)),
            )
        )
    }

    @ParameterizedTest(name = "[{index}] {0} compareTo {1} -> {2}")
    @CsvSource(value = [
        "1/2,1/4,1",
        "1/4,1/2,-1",
        "1/2,1/2,0",
        "2/4,1/2,0",
        "-2/4,1/2,-1",
        "-2/4,-1/2,0",
        "-1/4,1/2,-1",
        "1/4,-1/2,1",
    ])
    internal fun `rationals can be compared using their natural order`(a: String, b: String, expected: Int) {
        assertThat(Rational.parse(a).compareTo(Rational.parse(b)), equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0} == {1}")
    @CsvSource(value = [
        "1/1,1/1",
        "1/1,2/2",
        "1/2,2/4",
        "1/3,2/6",
        "11/3,33/9",
    ])
    internal fun `rationals that reduce to the same value are equal`(a: String, b: String) {
        assertThat(Rational.parse(a), equalTo(Rational.parse(b)))
    }

    @ParameterizedTest(name = "[{index}] {0} != {1}")
    @CsvSource(value = [
        "1/1,1/2",
        "1/1,2/3",
        "1/2,2/5",
        "1/3,2/7",
        "11/3,34/9",
    ])
    internal fun `rationals that do not reduce to the same value are unequal`(a: String, b: String) {
        assertThat(Rational.parse(a), !equalTo(Rational.parse(b)))
    }

    @ParameterizedTest(name = "[{index}] {0} + {1} = {2}")
    @CsvSource(value = [
        "1/2,1/4,3/4",
        "1/4,1/2,3/4",
        "1/4,3/4,1/1",
        "-1/4,3/4,1/2",
        "-1/4,-3/4,-1/1",
    ])
    internal fun `rationals can be added`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) b: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        assertThat(a + b, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0} - {1} = {2}")
    @CsvSource(value = [
        "1/2,1/4,1/4",
        "1/4,1/2,-1/4",
        "1/4,3/4,-1/2",
        "-1/4,3/4,-1/1",
        "-1/4,-3/4,1/2",
    ])
    internal fun `rationals can be subtracted`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) b: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        assertThat(a - b, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0} * {1} = {2}")
    @CsvSource(value = [
        "1/2,1/4,1/8",
        "1/4,1/2,1/8",
        "1/4,3/4,3/16",
        "-1/4,3/4,-3/16",
        "-1/4,-3/4,3/16",
    ])
    internal fun `rationals can be multiplied`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) b: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        assertThat(a * b, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0} * {1} = {2}")
    @CsvSource(value = [
        "1/2,1/4,2",
        "1/4,1/2,1/2",
        "1/4,3/4,1/3",
        "-1/4,3/4,-1/3",
        "-1/4,-3/4,1/3",
    ])
    internal fun `rationals can be divided`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) b: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        assertThat(a / b, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0}++ = {1}")
    @CsvSource(value = [
        "1/2,3/2",
        "1/4,5/4",
        "3/4,7/4",
        "-1/4,3/4",
        "-1/3,2/3",
        "1/1,2",
    ])
    internal fun `rationals can be incremented`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        var input = a
        input++
        assertThat(input, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] {0}-- = {1}")
    @CsvSource(value = [
        "1/2,-1/2",
        "1/4,-3/4",
        "3/4,-1/4",
        "-1/4,-5/4",
        "-1/3,-4/3",
        "1/1,0",
    ])
    internal fun `rationals can be decremented`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        var input = a
        input--
        assertThat(input, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] +{0} = {1}")
    @CsvSource(value = [
        "1/2,1/2",
        "-1/4,1/4",
        "1/-4,1/4",
        "-1/-4,1/4",
        "1/1,1/1",
        "0,0",
    ])
    internal fun `rationals can be made positive with unary plus`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        assertThat(+a, equalTo(expected))
    }

    @ParameterizedTest(name = "[{index}] -{0} = {1}")
    @CsvSource(value = [
        "1/2,-1/2",
        "-1/4,1/4",
        "1/-4,1/4",
        "-1/-4,-1/4",
        "1/1,-1/1",
        "0,0",
    ])
    internal fun `rationals can be negated`(
        @ConvertWith(RationalConverter::class) a: Rational,
        @ConvertWith(RationalConverter::class) expected: Rational,
    ) {
        assertThat(-a, equalTo(expected))
    }
}

private class RationalConverter : ArgumentConverter {
    override fun convert(source: Any, context: ParameterContext): Any {
        require(source is String) { "The argument should be a string: $source" }
        return Rational.parse(source)
    }
}