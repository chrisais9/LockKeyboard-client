package kr.koohyongmo.lockkeyboardclient.keyboard.model.encrypt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


/**
 * Created by KooHyongMo on 2020/06/23
 */
object RSACipherUtil {

    private const val CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA1AndMGF1Padding"
    private const val KEY_SIZE = 2048
    /**
     * 1024비트 RSA 키쌍을 생성합니다.
     */
    @Throws(NoSuchAlgorithmException::class)
    fun genRSAKeyPair(): KeyPair {
        val gen: KeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        gen.initialize(KEY_SIZE, SecureRandom())
        return gen.genKeyPair()
    }

    /**
     * Public Key로 RSA 암호화를 수행합니다.
     *
     * @param plainText 암호화할 평문입니다.
     * @param publicKey 공개키 입니다.
     * @return
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encryptRSA(plainText: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val bytePlain = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(bytePlain)
    }

    /**
     * Private Key로 RSA 복호화를 수행합니다.
     *
     * @param encrypted  암호화된 이진데이터를 base64 인코딩한 문자열 입니다.
     * @param privateKey 복호화를 위한 개인키 입니다.
     * @return
     * @throws Exception
     */
    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        UnsupportedEncodingException::class
    )
    fun decryptRSA(encrypted: String, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val byteEncrypted =
            Base64.getDecoder().decode(encrypted.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val bytePlain = cipher.doFinal(byteEncrypted)
        return String(bytePlain, Charsets.UTF_8)
    }
}