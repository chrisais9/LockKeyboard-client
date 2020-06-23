package kr.koohyongmo.lockkeyboardclient

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket
import kr.koohyongmo.lockkeyboardclient.keyboard.Constants
import java.net.URISyntaxException

class SocketApplication : Application() {

    lateinit var mSocket: Socket

    override fun onCreate() {
        super.onCreate()
        try {
            mSocket = IO.socket(Constants.SOCKET_SERVER_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }
}