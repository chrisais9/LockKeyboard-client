package kr.koohyongmo.lockkeyboardclient.keyboard.model.encrypt

import android.util.Base64
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
        publicKey = kp.public
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
    fun encrypt(vararg args: Any): String {
        val plain = args[0] as String
        val rsaPublicKey: PublicKey?
        rsaPublicKey = if (args.size == 1) {
            publicKey
        } else {
            args[1] as PublicKey
        }
        cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        encryptedBytes = cipher.doFinal(plain.toByteArray(StandardCharsets.UTF_8))
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
        cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding")
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
    fun getPublicKey(option: String?): String? {
        return when (option) {
            "pkcs1-pem" -> {
                var pkcs1pem: String? = "-----BEGIN RSA PUBLIC KEY-----\n"
                pkcs1pem += Base64.encodeToString(
                    publicKey.encoded,
                    Base64.DEFAULT
                )
                pkcs1pem += "-----END RSA PUBLIC KEY-----"
                pkcs1pem
            }
            "pkcs8-pem" -> {
                var pkcs8pem: String? = "-----BEGIN PUBLIC KEY-----\n"
                pkcs8pem += Base64.encodeToString(
                    publicKey.encoded,
                    Base64.DEFAULT
                )
                pkcs8pem += "-----END PUBLIC KEY-----"
                pkcs8pem
            }
            "base64" -> Base64.encodeToString(
                publicKey.encoded,
                Base64.DEFAULT
            )
            else -> null
        }
    }

    companion object {
        private const val CRYPTO_METHOD = "RSA"
        private const val CRYPTO_BITS = 2048

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