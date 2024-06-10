package com.phone.randomdialer.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.phone.randomdialer.Utils.Constant
import com.phone.randomdialer.data.modules.FeedbackRequestData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallScreen(
    modifier: Modifier = Modifier,
    viewModel: CallViewModel
) {
    val listState by viewModel.callDataState.collectAsState()
    if (listState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (viewModel.getDataSize() == 0) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No numbers found, Please try again", fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.event(CallEvent.FetchNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(text = "Try again")
            }
        }
    } else {
        LaunchedEffect(listState.callData.dialStatus) {
            if (listState.callData.dialStatus == Constant.DialStatus.INACTIVE.status) {
                viewModel.event(CallEvent.StopCalling)
            }
        }
        val coroutineScope = rememberCoroutineScope()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        val permissionsToRequest = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE
        )
        val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)
        val readPermissionState = rememberPermissionState(Manifest.permission.READ_PHONE_STATE)
        var isPermissionGranted by remember {
            mutableStateOf(callPermissionState.status.isGranted && readPermissionState.status.isGranted)
        }

        var isPopupVisible by remember { mutableStateOf(false) }

        val formattedDateTime = remember {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        }

        val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach { _ ->
                isPermissionGranted =
                    callPermissionState.status.isGranted && readPermissionState.status.isGranted
            }
        }

        fun callToNumber(number: String) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$number")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }

        fun nextCalling() {
            coroutineScope.launch {
                if (listState.callData.showPopup) {
                    if (uiState.isCalling)
                        isPopupVisible = true
                } else {
                    delay(listState.callData.recallTime)
                    if (uiState.isCalling) {
                        viewModel.event(CallEvent.StartCalling) {
                            callToNumber(it)
                        }
                    }
                }
            }
        }

        if (isPermissionGranted) {
            DisposableEffect(Unit) {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val telephonyCallback = getTelephonyCallback {
                        nextCalling()
                    }
                    telephonyManager.registerTelephonyCallback(
                        context.mainExecutor,
                        telephonyCallback
                    )
                    onDispose {
                        telephonyManager.unregisterTelephonyCallback(telephonyCallback)
                    }
                } else {
                    val phoneStateListener = getPhoneStateListener {
                        nextCalling()
                    }
                    telephonyManager.listen(
                        phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE
                    )
                    onDispose {
                        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    contentColor = Color.Gray
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Vilsa dailer", fontSize = 20.sp, color = Color.Black)

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedVisibility(visible = listState.callData.showStart) {
                        AppButton(
                            onButtonClick = {
                                if (isPermissionGranted) {
                                    viewModel.event(CallEvent.StartCalling) {
                                        callToNumber(it)
                                    }
                                } else {
                                    requestMultiplePermissionsLauncher.launch(permissionsToRequest)
                                }
                            },
                            text = "START CALLING",
                            buttonColor = Color.Green,
                            textColor = Color.Black,
                            isEnabled = !uiState.isCalling
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    AnimatedVisibility(visible = listState.callData.showStop) {
                        AppButton(
                            onButtonClick = { viewModel.event(CallEvent.StopCalling) },
                            text = "STOP CALLING",
                            buttonColor = Color.Red,
                            isEnabled = uiState.isCalling
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AppButton(
                        onButtonClick = { viewModel.event(CallEvent.FetchNumber) },
                        text = "REFRESH"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (uiState.isCalling) {
                        Text(text = "Calling: ${uiState.currentNumber}", color = Color.Blue)
                    }
                }
            }

            if (isPopupVisible) {
                FeedbackDialog(
                    popupTime = listState.callData.popupTime,
                    onDismissRequest = {
                        viewModel.event(
                            CallEvent.CallFeedback(
                                FeedbackRequestData(
                                    mobileNo = uiState.currentNumber,
                                    demo = if (it == Constant.CallState.DEMO) it.state else "",
                                    callLater = if (it == Constant.CallState.CALL_LATER) it.state else "",
                                    noAnswer = if (it == Constant.CallState.NO_ANSWER) it.state else "",
                                    invalidNo = if (it == Constant.CallState.INVALID_NUMBER) it.state else "",
                                    callDateTime = formattedDateTime.format(Calendar.getInstance().time.time)
                                )
                            )
                        )
                        coroutineScope.launch {
                            delay(listState.callData.recallTime)
                            if (uiState.isCalling) {
                                viewModel.event(CallEvent.StartCalling) {
                                    callToNumber(it)
                                }
                            }
                        }
                        isPopupVisible = false
                    }
                )
            }
        }
    }
}


@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    isEnabled: Boolean = true,
    text: String = "Button",
    textColor: Color = Color.White,
    buttonColor: Color = Color.Blue,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        onClick = onButtonClick,
        enabled = isEnabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(text = text, color = textColor, fontSize = 16.sp, modifier = Modifier.padding(10.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun getTelephonyCallback(
    onCallStateChanged: () -> Unit,
): TelephonyCallback {
    return object : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                onCallStateChanged()
            }
        }
    }
}

fun getPhoneStateListener(
    onCallStateChanged: () -> Unit
): PhoneStateListener {
    return object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            super.onCallStateChanged(state, incomingNumber)
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                onCallStateChanged()
            }
        }
    }
}

@Composable
fun FeedbackDialog(
    popupTime: Long,
    onDismissRequest: (Constant.CallState) -> Unit,
) {
    var selectedState by remember { mutableStateOf(Constant.CallState.DEMO) }
    var dismissed by remember { mutableStateOf(false) }

    // Launch a coroutine when the dialog is first composed
    LaunchedEffect(Unit) {
        // Wait for 10 seconds
        delay(popupTime)
        // Dismiss the dialog if it's still visible after 10 seconds
        if (!dismissed) {
            onDismissRequest(selectedState)
            dismissed = true
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable {
            return@clickable
        }
        .background(color = Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp)
            ) {
                Text(text = "Select Call Feedback", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    DialogItem(
                        selectedState = selectedState,
                        callState = Constant.CallState.DEMO,
                    ) {
                        selectedState = it
                    }
                    DialogItem(
                        selectedState = selectedState,
                        callState = Constant.CallState.CALL_LATER,
                    ) {
                        selectedState = it
                    }
                    DialogItem(
                        selectedState = selectedState,
                        callState = Constant.CallState.NO_ANSWER,
                    ) {
                        selectedState = it
                    }
                    DialogItem(
                        selectedState = selectedState,
                        callState = Constant.CallState.INVALID_NUMBER,
                    ) {
                        selectedState = it
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        onDismissRequest(selectedState)
                        dismissed = true
                    }
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun DialogItem(
    modifier: Modifier = Modifier,
    selectedState: Constant.CallState,
    callState: Constant.CallState = Constant.CallState.DEMO,
    onClick: (Constant.CallState) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedState == callState,
            onClick = {
                onClick(callState)
            },
        )
        Text(text = callState.state)
    }
}