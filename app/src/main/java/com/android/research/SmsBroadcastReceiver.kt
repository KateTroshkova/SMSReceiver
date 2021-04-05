package com.android.research

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status;

class SmsBroadcastReceiver : BroadcastReceiver() {

    var onSuccess: ((intent: Intent) -> Unit)? = null
    var onFailure: (() -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    extras
                        .getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        ?.let {
                            onSuccess?.invoke(it)
                        }
                }
                CommonStatusCodes.TIMEOUT -> {
                    onFailure?.invoke()
                }
            }
        }
    }
}