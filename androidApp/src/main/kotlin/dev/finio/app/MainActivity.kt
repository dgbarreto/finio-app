package dev.finio.app

import android.Manifest
import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import dev.finio.app.deeplink.DeepLinkEvent
import dev.finio.app.deeplink.DeepLinkEventBus
import dev.finio.app.ui.auth.LoginScreen
import dev.finio.app.ui.auth.LoginScreen.Content
import dev.finio.auth.di.authModule
import dev.finio.auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if(it){
            registerFcmToken()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            registerFcmToken()
        }

        println("MainActivity onCreate deep_link: ${intent?.getStringExtra("deep_link")}")
        println("deep link uri: ${intent.data?.toString()}")
        handleDeepLinkIntent(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        println("MainActivity onNewIntent deep_link: ${intent.getStringExtra("deep_link")}")
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?){
        val deepLink = intent?.data?.toString()
        println("deep link uri: $deepLink")
        if(deepLink == "finio://budget"){
            val deepLinkEventBus: DeepLinkEventBus by inject()
            deepLinkEventBus.emit(DeepLinkEvent.OpenBudget)
        }
    }

    private fun registerFcmToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful){
                println("FCM Token: ${it.result}")

                val token = it.result
                val authRepository: AuthRepository by inject()
                lifecycleScope.launch {
                    authRepository.saveFcmToken(token)
                }
            }
        }
    }
}