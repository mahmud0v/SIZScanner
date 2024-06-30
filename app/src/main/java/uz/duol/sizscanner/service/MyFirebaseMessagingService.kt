package uz.duol.sizscanner.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("PPP", "onMessageReceived: ${message.data}")

        message.notification?.let {
            Log.d("PPP", "message body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("TTT", "onNewToken: $token")
    }


}