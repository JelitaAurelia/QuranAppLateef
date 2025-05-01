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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType

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

    AnimatedVisibility(
        visible = showProfileDialog,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Back Button and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp)) // Balance the layout
            }

            // Profile Picture Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(231.dp) // Increased height to stretch background downwards
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profilebg),
                    contentDescription = "Profile Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                user?.photoUrl?.let { photoUrl ->
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .size(165.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(76.dp)) // Increased spacing to push content down

            // Display Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Card untuk Username
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Nama Pengguna", color = Color.Gray, fontSize = 17.sp)
                        Text(
                            text = user?.displayName ?: "Pengguna Tidak Dikenal",
                            color = Color.White,
                            fontSize = 19.sp,
                            modifier = Modifier.padding(vertical = 13.dp)
                        )
                    }
                }

                // Card untuk Email Id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("ID Email", color = Color.Gray, fontSize = 17.sp)
                        Text(
                            text = user?.email ?: "Tidak ada email",
                            color = Color.White,
                            fontSize = 19.sp,
                            modifier = Modifier.padding(vertical = 13.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(71.dp))

            // Logout Button
            Button(
                onClick = {
                    onLogout()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B7065),
                    contentColor = Color.White
                )
            ) {
                Text("Logout")
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

