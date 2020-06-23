package kr.koohyongmo.lockkeyboardclient.keyboard.model.encrypt

import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/**
 * Created by KooHyongMo on 2020/06/23
 */
class RSACipher {
    private lateinit var kpg: KeyPairGenerator
    private lateinit var kp: KeyPair
    private lateinit var publicKey: PublicKey
    private lateinit var privateKey: PrivateKey
    private lateinit var encryptedBytes: ByteArray
    private lateinit var decryptedBytes: ByteArray
    private lateinit var cipher: Cipher
    private lateinit var cipher1: Cipher
    private lateinit var encrypted: String
    private lateinit var decrypted: String

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    private fun generateKeyPair() {
        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD)
        kpg.initialize(CRYPTO_BITS)
        kp = kpg.genKeyPair()
//        publicKey = kp.public
        publicKey = stringToPublicKey("-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvaNy3C2Y7inKWoNXDtSY\n" +
                "mLPAwj9vMvv7NeOgWeEFF9W9o1FTByX+Je5fVLliZqaAvQ+LZB7ORDVnwC35Q/Dx\n" +
                "gWiiZiUJK7waQH00Q62FNiEy6zSl75KWxvaJd6lKD7VbZmx2l9IydV3SY0e6fbKc\n" +
                "2H0KpLMa9mPE5a2LKPpalirYP0mhyaIeZTgUO5HFdtDUNzHwjqisUyYIUaWfatxO\n" +
                "R7W8l1aV7R6hEWMO/JMZ0kzLzVdarTsqCZcM3h/cV9KYiD6cHtuj42veS8tr0M23\n" +
                "YI3W1b05yAaJtBJ3QHTGntmHKpvaFtMhPUbFcOskNQiVHxh5aYGol4CtizzOx9fG\n" +
                "/wIDAQAB\n" +
                "-----END PUBLIC KEY-----\n")!!
        privateKey = kp.private
    }

    /**
     * Encrypt plain text to RSA encrypted and Base64 encoded string
     *
     * @param args args[0] should be plain text that will be encrypted
     * If args[1] is be, it should be RSA public key to be used as encrypt public key
     * @return a encrypted string that Base64 encoded
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(string: String): String {
        cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        encryptedBytes = cipher.doFinal(string.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(result: String?): String {
        cipher1 = Cipher.getInstance(CRYPTO_ALGORITHM)
        cipher1.init(Cipher.DECRYPT_MODE, privateKey)
        decryptedBytes = cipher1.doFinal(Base64.decode(result, Base64.DEFAULT))
        decrypted = String(decryptedBytes)
        return decrypted
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )

    fun getPublicKey(): String =
        Base64.encodeToString(
            publicKey.encoded,
            Base64.DEFAULT
        )


    companion object {
        private const val CRYPTO_METHOD = "RSA"
        private const val CRYPTO_BITS = 2048
        private const val CRYPTO_ALGORITHM = "RSA/ECB/OAEPWithSHA1AndMGF1Padding"

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            IllegalBlockSizeException::class,
            BadPaddingException::class
        )
        fun stringToPublicKey(publicKeyString: String): PublicKey? {
            var publicKeyString = publicKeyString
            return try {
                if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains(
                        "-----END PUBLIC KEY-----"
                    )
                ) publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                val keyBytes =
                    Base64.decode(publicKeyString, Base64.DEFAULT)
                val spec =
                    X509EncodedKeySpec(keyBytes)
                val keyFactory =
                    KeyFactory.getInstance("RSA")
                keyFactory.generatePublic(spec)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                null
            } catch (e: InvalidKeySpecException) {
                e.printStackTrace()
                null
            }
        }
    }

    init {
        generateKeyPair()
    }
}