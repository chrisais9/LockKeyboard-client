package kr.koohyongmo.lockkeyboardclient.keyboard.model.service

import androidx.appcompat.app.AppCompatActivity
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kr.koohyongmo.lockkeyboardclient.SocketApplication
import org.json.JSONException
import org.json.JSONObject


/**
 * Created by KooHyongMo on 2020/06/20
 */
class SocketService(private val activity: AppCompatActivity) {

    private val TAG = "SocketService"

    lateinit var socket: Socket

    private var isConnected = false

    fun listen() {
        socket = (activity.application as SocketApplication).mSocket

        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.connect()

    }

    fun disconnect() {

        socket.disconnect()
        socket.off(Socket.EVENT_CONNECT, onConnect)
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
    }

    private val onConnect: Emitter.Listener? = Emitter.Listener {
        activity.runOnUiThread {
            if (!isConnected) {
                isConnected = true
            }
        }
    }
    private val onDisconnect: Emitter.Listener? = Emitter.Listener {
        activity.runOnUiThread {
            android.util.Log.i(TAG, "diconnected")
            isConnected = false
        }
    }
    private val onConnectError: Emitter.Listener? = Emitter.Listener {
        activity.runOnUiThread {
            android.util.Log.e(TAG, "Error connecting")
        }
    }

//    private val onKeyChar = Emitter.Listener {
//        val preJsonObject = JSONObject
//        preJsonObject.addProperty("comment", etMsg.getText().toString() + "")
//        var jsonObject: JSONObject? = null
//        try {
//            jsonObject = JSONObject(preJsonObject.toString())
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        socket.emit("reqMsg", jsonObject)
//    }
}