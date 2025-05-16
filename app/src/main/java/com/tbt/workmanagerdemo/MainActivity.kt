package com.tbt.workmanagerdemo

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tbt.workmanagerdemo.ui.theme.WorkManagerDemoTheme
import java.util.Calendar
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerDemoTheme {
                val context = LocalContext.current
                val calendar = remember { Calendar.getInstance() }

                val showDatePicker = remember { mutableStateOf(false) }
                val showTimePicker = remember { mutableStateOf(false) }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = "android.permission.POST_NOTIFICATIONS"
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { granted ->
                            if (!granted) {
                                Toast.makeText(context, "Bildirim izni gerekli", Toast.LENGTH_SHORT).show()
                                exitProcess(0)
                            }
                        }
                    )

                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(permission)
                    }
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { showDatePicker.value = true }) {
                        Text("Tarih Seç")
                    }

                    Button(onClick = { showTimePicker.value = true }) {
                        Text("Saat Seç")
                    }

                    Button(onClick = {
                        val selectedTime = calendar.timeInMillis
                        val now = System.currentTimeMillis()

                        Log.d("ZAMAN", "Seçilen: $selectedTime, Şimdi: $now")

                        if (selectedTime > now) {
                            val delayInMillis = selectedTime - now

                            scheduleNotification(
                                context = context,
                                delayInMillis = delayInMillis,
                                title = "Ders Zamanı!",
                                message = "Çalışma zamanı ⏰"
                            )
                            Toast.makeText(context, "Bildirim ayarlandı", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Geçmiş bir zaman seçilemez", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Hatırlatma Kur")
                    }
                }

                if (showDatePicker.value) {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                            // Calendar saati sıfırlama, karışıklığı önler
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            showDatePicker.value = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                if (showTimePicker.value) {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            showTimePicker.value = false
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            }
        }
    }
}
