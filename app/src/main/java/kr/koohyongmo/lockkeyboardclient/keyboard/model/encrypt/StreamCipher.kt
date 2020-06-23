package kr.koohyongmo.lockkeyboardclient.keyboard.model.encrypt

import java.math.BigInteger

class Random(var value: Int) {
    operator fun next() {
        var bi = BigInteger.valueOf(value.toLong())
        bi = bi.multiply(BigInteger.valueOf(1103515245L))
        bi = bi.add(BigInteger.valueOf(12345))
        bi = bi.mod(BigInteger.valueOf(2147483647L))
        value = bi.toInt()
    }

}

class StreamCipher internal constructor(seeds: IntArray) {
    var randoms: Array<Random?> = arrayOfNulls(4)
    var keys: IntArray
    var key_i = 0
    var bit_i = 0
    fun encrypt(c: Char): Char {
        return (c.toInt() xor getnow7bit()).toChar()
    }

    fun decrypt(c: Char): Char {
        return (c.toInt() xor getnow7bit()).toChar()
    }

    fun getnow7bit(): Int {
        val res = keys[key_i] and (127 shl 7 * bit_i) shr 7 * bit_i
        if (++bit_i == 4) {
            bit_i = 0
            if (++key_i == 4) {
                next()
            }
        }
        return res
    }

    operator fun next() {
        for (i in 0..3) {
            randoms[i]!!.next()
            println(randoms[i]!!.value)
            keys[i] = randoms[i]!!.value % 268435456 // 268425456 = 2^28
            println("keys " + i + ": " + keys[i])
        }
        key_i = 0 // 0~3
        bit_i = 0 // 0~6
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // for test
            val seeds = intArrayOf(123456, 789012, 345678, 901234)
            val key = StreamCipher(seeds)
            for (i in 0..999) {
                println(key.encrypt('A').toInt())
            }
        }
    }

    init {
        for (i in 0..3) {
            randoms[i] =
                Random(seeds[i])
        }
        keys = IntArray(4)
        next()
    }
}