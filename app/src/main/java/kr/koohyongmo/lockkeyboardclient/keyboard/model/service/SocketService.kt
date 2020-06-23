package kr.koohyongmo.lockkeyboardclient.keyboard.model.service

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kr.koohyongmo.lockkeyboardclient.SocketApplication
import kr.koohyongmo.lockkeyboardclient.keyboard.model.encrypt.RSACipher
import kr.koohyongmo.lockkeyboardclient.keyboard.model.response.TokenResponse
import kr.koohyongmo.lockkeyboardclient.utils.NetworkHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey


/**
 * Created by KooHyongMo on 2020/06/20
 */
class SocketService(private val activity: AppCompatActivity) {

    private val TAG = "SocketService"

    lateinit var socket: Socket

    lateinit var privateKey: PrivateKey
    lateinit var publicKey: PublicKey

    lateinit var rsaCipher: RSACipher

    lateinit var encryptedTokenString: String
    lateinit var encryptedTokenSign: String
    lateinit var encryptedSeeds: String

    val seeds = listOf(123456, 789012, 345678, 901234)


    private var isConnected = false

    fun listen() {

        rsaCipher = RSACipher()
        Log.d(TAG, rsaCipher.getPublicKey())

        NetworkHelper.instance
            .getToken("hmk")
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Log.d(TAG,t.localizedMessage.toString())
                }

                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: Response<TokenResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            val tokenResponse = response.body()!!

                            encryptedTokenString = generateEncryptedString(tokenResponse.tokenString)
                            encryptedTokenSign = generateEncryptedString(tokenResponse.tokenSign)
                            encryptedSeeds = generateEncryptedString("$seeds")

                            socket = (activity.application as SocketApplication).mSocket

                            socket.on(Socket.EVENT_CONNECT, onConnect)
                            socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
                            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                            socket.connect()

                        }

                    }
                }

            })
    }

    fun disconnect() {

        socket.disconnect()
        socket.off(Socket.EVENT_CONNECT, onConnect)
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
    }

    private fun generateEncryptedString(toBeEncrypted: String) =
        rsaCipher.encrypt(toBeEncrypted)

    private val onConnect: Emitter.Listener? = Emitter.Listener {

        activity.runOnUiThread {
            if (!isConnected) {
                isConnected = true


                val jsonObject = JSONObject()
                jsonObject.put("tokenString", encryptedTokenString)
                jsonObject.put("tokenSign", encryptedTokenSign)
                jsonObject.put("seeds", encryptedSeeds)


                Log.d(TAG, jsonObject.toString())
                socket.emit("handshake", jsonObject.toString())
            }
        }
    }
    private val onDisconnect: Emitter.Listener? = Emitter.Listener {
        activity.runOnUiThread {
            Log.i(TAG, "disconnected")
            isConnected = false
        }
    }
    private val onConnectError: Emitter.Listener? = Emitter.Listener {
        activity.runOnUiThread {
            Log.e(TAG, "Error connecting")
        }
    }

//    private val onKeyChar = Emitter.Listener {
//        val preJsonObject = JSONObject()
//        preJsonObject.addProperty("comment", etMsg.getText().toString() + "")
//        var jsonObject: JSONObject? = null
//        try {
//            jsonObject = JSONObject(preJsonObject.toString())
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        socket.emit("handshake", jsonObject)
//    }
}