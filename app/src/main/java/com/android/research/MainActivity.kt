package com.android.research

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val smsBroadcastReceiver: SmsBroadcastReceiver by lazy {
        SmsBroadcastReceiver().apply {
            onTextReceived = {
                messageTextView.text = it ?: ""
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!isSmsPermissionGranted()) {
            requestReadAndSendSmsPermission()
        } else {
            registerReceiver(
                smsBroadcastReceiver,
                IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == SMS_PERMISSION_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            registerReceiver(
                smsBroadcastReceiver,
                IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            )
        }
    }

    private fun isSmsPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED

    private fun requestReadAndSendSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
            ),
            SMS_PERMISSION_CODE
        )
    }

    private companion object {
        private const val SMS_PERMISSION_CODE = 42
    }
}