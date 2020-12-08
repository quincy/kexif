package com.quakbo.kexif.number

import org.apache.commons.imaging.common.RationalNumber
import kotlin.math.abs

class Rational(n: Int, d: Int) : Number() {

    val numerator: Int
    val denominator: Int

    init {
        val (n1, d1) = reduce(n, d)
        numerator = n1
        denominator = d1
    }

    constructor(rn: RationalNumber) : this(rn.numerator, rn.divisor)

    companion object {
        fun parse(value: String): Rational {
            return when {
                '/' in value -> value.split('/')
                    .map { it.toInt() }
                    .let { (n, d) -> Rational(n, d) }
                else -> Rational(Integer.parseInt(value), 1)
            }
        }
    }

    private fun reduce(n: Int, d: Int): Pair<Int, Int> {
        return gcd(n, d)
            .let { Pair(n / it, d / it) }
            .let { (n, d) ->
                when {
                    n < 0 && d < 0 -> abs(n) to abs(d)
                    else -> n to d
                }
            }
    }

    private fun gcd(a: Int, b: Int): Int = when (b) {
        0 -> abs(a)
        else -> gcd(b, a % b)
    }

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    operator fun compareTo(other: Rational): Int = this.toDouble().compareTo(other.toDouble())

    // TODO add overloaded functions for plus, minus, times, div to operate on other types

    /** Adds the other value to this value. */
    operator fun plus(other: Rational): Rational {
        val d = this.denominator * other.denominator
        val n = (this.numerator * other.denominator) + (other.numerator * this.denominator)
        return reduce(n, d).let { Rational(it.first, it.second) }
    }

    /** Subtracts the other value from this value. */
    operator fun minus(other: Rational): Rational {
        val d = this.denominator * other.denominator
        val n = (this.numerator * other.denominator) - (other.numerator * this.denominator)
        return reduce(n, d).let { Rational(it.first, it.second) }
    }

    /** Multiplies this value by the other value. */
    operator fun times(other: Rational): Rational {
        val n = this.numerator * other.numerator
        val d = this.denominator * other.denominator
        return reduce(n, d).let { Rational(it.first, it.second) }
    }

    /** Divides this value by the other value. */
    operator fun div(other: Rational): Rational {
        val n = this.numerator * other.denominator
        val d = this.denominator * other.numerator
        return reduce(n, d).let { Rational(it.first, it.second) }
    }

    /** Increments this value. */
    operator fun inc(): Rational {
        return this + Rational(1, 1)
    }

    /** Decrements this value. */
    operator fun dec(): Rational {
        return this - Rational(1, 1)
    }

    /** Returns this value. */
    operator fun unaryPlus(): Rational = Rational(abs(numerator), abs(denominator))

    /** Returns the negative of this value. */
    operator fun unaryMinus(): Rational = Rational(-numerator, denominator)

    override fun toByte(): Byte = this.toInt().toByte()
    override fun toChar(): Char = this.toInt().toChar()
    override fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()
    override fun toFloat(): Float = numerator.toFloat() / denominator.toFloat()
    override fun toInt(): Int = numerator / denominator
    override fun toLong(): Long = numerator.toLong() / denominator.toLong()
    override fun toShort(): Short = this.toInt().toShort()

    override fun toString(): String = "$numerator/$denominator"

    override fun equals(other: Any?): Boolean {
        if (other is RationalNumber) return this == Rational(other.numerator, other.divisor)
        if (this === other) return true
        if (other !is Rational) return false

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator
        result = 31 * result + denominator
        return result
    }
}

fun String.toRational(): Rational = Rational.parse(this)
fun RationalNumber.toRational(): Rational = Rational(this)
