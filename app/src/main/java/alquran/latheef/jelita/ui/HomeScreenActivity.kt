package alquran.latheef.jelita.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import alquran.latheef.jelita.R
import alquran.latheef.jelita.utiils.AccountHistoryManager
import com.google.firebase.auth.FirebaseUser
import alquran.latheef.jelita.utiils.FirebaseUtils

@Composable
fun HomeScreen(navController: NavController) {
    val currentUser = FirebaseUtils.getCurrentUser()
    var showProfileDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopBar(user = currentUser) { showProfileDialog = true }
        }
    ) { padding ->
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF324851), Color(0xFF4F6457), Color(0xFF324851)),
            start = Offset(0f, 0f),
            end = Offset(1900f, 880f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            TabSection(navController)
        }
    }

    if (showProfileDialog) {
        ProfileDialog(
            user = currentUser,
            onDismiss = { showProfileDialog = false },
            onLogout = {
                FirebaseUtils.signOut(context) {
                    val intent = Intent(context, SplashScreenActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(user: FirebaseUser?, onProfileClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Al-Qur'an",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        actions = {
            user?.photoUrl?.let { photoUrl ->
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    contentScale = ContentScale.Crop
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF324851)
        )
    )
}

@Composable
fun ProfileDialog(user: FirebaseUser?, onDismiss: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val accountHistory = remember { AccountHistoryManager.getAccountHistory(context) }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                user?.photoUrl?.let { photoUrl ->
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.displayName ?: "Unknown User",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user?.email ?: "No email",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                if (accountHistory.isNotEmpty()) {
                    Text(
                        "Riwayat Akun",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    accountHistory.forEach {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            AsyncImage(
                                model = it["photoUrl"],
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = it["name"] ?: "", color = Color.White, fontSize = 14.sp)
                                Text(text = it["email"] ?: "", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onLogout()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B7065), contentColor = Color.White)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}


@Composable
fun TabSection(navController: NavController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Surah", "Juz")

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF324851),
            contentColor = Color.White
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray
                        )
                    }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> SurahTab(navController)
            1 -> JuzTab(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF34675c)) {
        val items = listOf("Doa", "Quran", "Bookmark")
        val icons = listOf(
            R.drawable.doa,
            R.drawable.quran,
            R.drawable.solat
        )
        var selectedItem by remember { mutableStateOf(1) } // Default ke Quran (beranda)

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item,
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    when (index) {
                        0 -> navController.navigate("doa")
                        1 -> navController.navigate("home")
                        2 -> navController.navigate("bookmark")
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF03A9F4),
                    unselectedIconColor = Color(0xFFB0BEC5),
                    indicatorColor = Color(0xFF2D3033),
                    selectedTextColor = Color(0xFF2D3033),
                    unselectedTextColor = Color(0xFFB0BEC5)
                )

            )
        }
    }
}

