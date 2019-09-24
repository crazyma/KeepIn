package com.beibeilab.batukits

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class EncryptKit(
    private val keyStore: KeyStore,
    private val spec: AlgorithmParameterSpec
) {

    companion object {
        private const val PREF_NAME = "batu_encrypt_pref"
        private const val ARGS_KEY = "ARGS_KEY"
        private const val ARGS_IV = "ARGS_IV"

        //  TODO by Batu: This key value should be changed!?
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "KEY_ALIAS"
        private const val AES_MODE = "AES/GCM/NoPadding"

        /**
         * I tried to set length to 16 and get exception:
         * java.security.InvalidAlgorithmParameterException: Unsupported IV length: 16 bytes. Only 12 bytes long IV supported
         */
        private const val AES_KEY_SIZE = 128    // in bits
        private const val GCM_NONCE_LENGTH = 12 // in bytes
        private const val GCM_TAG_LENGTH = 16   // in bytes
    }


    class Factory(val context: Context) {

        fun create(): EncryptKit {
            val nonceIV = getNonceIV(context)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, nonceIV)
            val keyStore = setupKeystore()
            return EncryptKit(keyStore, spec)
        }

        private fun getNonceIV(context: Context): ByteArray {
            val nonceIvString = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).run {
                getString(ARGS_IV, null) ?: run {
                    val random = SecureRandom.getInstanceStrong()
                    val nonce = ByteArray(GCM_NONCE_LENGTH)
                    random.nextBytes(nonce)

                    val nonceString = Base64.encodeToString(nonce, Base64.DEFAULT)
                    edit {
                        putString(ARGS_IV, nonceString)
                    }

                    nonceString
                }
            }

            return Base64.decode(nonceIvString, Base64.DEFAULT)
        }

        private fun setupKeystore(): KeyStore {
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
                load(null)
            }

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenParameterSpec =
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build()

                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE).apply {
                    init(keyGenParameterSpec)
                    generateKey()
                }
            }
            return keyStore
        }
    }

    fun runEncryption(message: String): String {
        val cipher = getCipher(true, spec)
        val tag = ByteArray(GCM_TAG_LENGTH)
        cipher.updateAAD(tag)
        val encodedBytes = cipher.doFinal(message.toByteArray())
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    fun runDecryption(encryptedString: String): String {
        val decodedBytes = Base64.decode(encryptedString.toByteArray(), Base64.DEFAULT)
        val cipher = getCipher(false, spec)
        val tag = ByteArray(GCM_TAG_LENGTH)
        cipher.updateAAD(tag)
        return String(cipher.doFinal(decodedBytes))
    }

    private fun getCipher(isEncrypting: Boolean, spec: AlgorithmParameterSpec): Cipher {
        val key = keyStore.getKey(KEY_ALIAS, null)
        val mode = if (isEncrypting) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
        val c = Cipher.getInstance(AES_MODE)
        c.init(mode, key, spec)
        return c
    }

}