package kk.domoRolls.ru

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kk.domoRolls.ru.registration.RegistrationScreen
import kk.domoRolls.ru.ui.theme.DomoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DomoTheme {
               RegistrationScreen()
            }
        }
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                // Handle the updated status if needed
                if (task.isSuccessful) {
                    val updated = task.result
                    var test: String = FirebaseRemoteConfig.getInstance().getString("otpMessage")

                    Log.e(ContentValues.TAG, "Config params updated: $updated")
                    Toast.makeText(
                        this,
                        "Fetch and activate succeeded $test",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Fetch failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DomoTheme {
       // RegistrationScreen()
    }
}