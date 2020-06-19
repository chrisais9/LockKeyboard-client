package kr.koohyongmo.lockkeyboardclient.keyboard.application

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket
import kr.koohyongmo.lockkeyboardclient.keyboard.Constants
import java.net.URISyntaxException

/**
 * Created by KooHyongMo on 2020/06/20
 */

class SocketApplication : Application() {
    var socket: Socket
    init {
        try {
            socket = IO.socket(Constants.SERVER_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }
}