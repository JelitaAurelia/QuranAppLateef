package alquran.latheef.jelita.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import alquran.latheef.jelita.R
import alquran.latheef.jelita.navigation.HomeScreenActivity
import alquran.latheef.jelita.utiils.AccountHistoryManager
import alquran.latheef.jelita.utiils.FirebaseUtils

class SplashScreenActivity : ComponentActivity() {

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseUtils.auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseUtils.getCurrentUser()?.let {
                        AccountHistoryManager.saveAccount(this, it)
                    }
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    finish()
                }
            }
        } catch (e: ApiException) {
            // Handle error if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseUtils.getCurrentUser() != null) {
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
            return
        }

        setContent {
            SplashScreen(onGoogleSignInClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val signInClient = GoogleSignIn.getClient(this, gso)
                val signInIntent = signInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            })
        }

    }
}
@Composable
fun SplashScreen(onGoogleSignInClick: () -> Unit) {
    var scale by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        scale = 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF324851), Color(0xFF4F6457), Color(0xFFACD0C0))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash1),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(251.dp)
                    .scale(scale)
                    .animateContentSize()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Qur'an Al Latheef",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    letterSpacing = 1.5.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "أَهْلًا وَسَهْلًا",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onGoogleSignInClick) {
                Text(text = "Login with Google")
            }
        }

        Image(
            painter = painterResource(id = R.drawable.splash2),
            contentDescription = "Masjid",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

