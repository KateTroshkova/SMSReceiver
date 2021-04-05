package com.android.research

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val smsBroadcastReceiver:SmsBroadcastReceiver by lazy {
        SmsBroadcastReceiver().apply {
            onSuccess = {
                startActivityForResult(it, USER_CONSENT_CODE)
            }
            onFailure = {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerReceiver(smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_CONSENT_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                messageTextView.setText(
                    String.format(
                        "%s - %s",
                        getString(R.string.received_message),
                        message
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun startSmsUserConsent(){
        val client = SmsRetriever.getClient(this)
        client
            .startSmsUserConsent(null)
            .addOnSuccessListener {
                Toast.makeText(this, "Get SMS", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Smt went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private companion object{
        private const val USER_CONSENT_CODE = 42
    }
}