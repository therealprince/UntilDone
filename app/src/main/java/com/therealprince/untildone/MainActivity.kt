package com.therealprince.untildone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.therealprince.untildone.data.AppDatabase
import com.therealprince.untildone.data.BackupManager
import com.therealprince.untildone.data.BackupReceiver
import com.therealprince.untildone.data.DailyLog
import com.therealprince.untildone.data.FocusSession
import com.therealprince.untildone.data.Journey
import com.therealprince.untildone.data.SessionManager
import com.therealprince.untildone.data.User
import com.therealprince.untildone.data.hashPassword
import com.therealprince.untildone.ui.components.BottomNavBar
import com.therealprince.untildone.ui.components.CreateJourneySheet
import com.therealprince.untildone.ui.screens.AnalyticsScreen
import com.therealprince.untildone.ui.screens.AuthScreen
import com.therealprince.untildone.ui.screens.DashboardScreen
import com.therealprince.untildone.ui.screens.FocusTimerScreen
import com.therealprince.untildone.ui.screens.JourneyDetailScreen
import com.therealprince.untildone.ui.screens.MissionsScreen
import com.therealprince.untildone.ui.screens.SettingsScreen
import com.therealprince.untildone.ui.theme.UntilDoneTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val sessionManager = SessionManager(this)
        val backupManager = BackupManager(this)

        setContent {
            UntilDoneApp(db, sessionManager, backupManager)
        }
    }
}

@Composable
fun UntilDoneApp(
    db: AppDatabase,
    sessionManager: SessionManager,
    backupManager: BackupManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Default to system theme; user override if explicitly set
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    var isDarkMode by remember {
        mutableStateOf(sessionManager.isDarkModePreference() ?: systemDark)
    }
    var isAuthenticated by remember { mutableStateOf(sessionManager.isLoggedIn()) }
    var currentUserId by remember { mutableLongStateOf(sessionManager.getUserId()) }
    var currentUserName by remember { mutableStateOf(sessionManager.getUserName()) }
    var currentUserEmail by remember { mutableStateOf(sessionManager.getUserEmail()) }
    var currentScreen by remember { mutableStateOf("home") }
    var activeJourneyId by remember { mutableStateOf<Long?>(null) }
    var isCreateModalOpen by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    var backupMessage by remember { mutableStateOf<String?>(null) }

    // Profile image state
    var profileImagePath by remember { mutableStateOf(sessionManager.getProfileImagePath()) }

    // Categories and units state
    var categories by remember { mutableStateOf(sessionManager.getCategories()) }
    var units by remember { mutableStateOf(sessionManager.getUnits()) }

    // Backup toggle state (reactive)
    var isPeriodicBackupEnabled by remember {
        mutableStateOf(sessionManager.isPeriodicBackupEnabled())
    }
    var backupIntervalHours by remember {
        mutableIntStateOf(sessionManager.getBackupIntervalHours())
    }

    // All journeys from SQLite
    var journeys by remember { mutableStateOf(listOf<Journey>()) }
    var journeyRefresh by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        db.journeyUpdatesFlow().collect { journeyRefresh = it }
    }

    LaunchedEffect(currentUserId, journeyRefresh) {
        if (currentUserId != -1L) {
            journeys = db.getJourneysByUser(currentUserId)
        }
    }

    // Image picker launcher
    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val file = File(context.filesDir, "profile_image.jpg")
                        inputStream?.use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        sessionManager.setProfileImagePath(file.absolutePath)
                        profileImagePath = file.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    // Bottom nav screens
    val bottomNavScreens = setOf("home", "missions", "analytics", "settings")

    UntilDoneTheme(darkTheme = isDarkMode) {
        val colors = UntilDoneTheme.colors

        if (!isAuthenticated) {
            AuthScreen(
                onGoogleLogin = {
                    authError = "Google Sign-In requires OAuth setup. Use email & password to get started."
                },
                onLocalLogin = { email, password ->
                    scope.launch {
                        val user = db.getUserByEmail(email)
                        if (user != null && user.passwordHash == hashPassword(password)) {
                            sessionManager.saveSession(user.id, user.name, user.email)
                            currentUserId = user.id
                            currentUserName = user.name
                            currentUserEmail = user.email
                            authError = null
                            isAuthenticated = true
                        } else {
                            authError = "Invalid email or password"
                        }
                    }
                },
                onSignUp = { name, email, password ->
                    scope.launch {
                        if (db.emailExists(email)) {
                            authError = "An account with this email already exists"
                        } else {
                            val user = User(
                                name = name,
                                email = email,
                                passwordHash = hashPassword(password)
                            )
                            val id = db.insertUser(user)
                            sessionManager.saveSession(id, name, email)
                            currentUserId = id
                            currentUserName = name
                            currentUserEmail = email
                            authError = null
                            isAuthenticated = true
                        }
                    }
                },
                errorMessage = authError
            )
        } else if (currentScreen == "timer") {
            BackHandler { currentScreen = "home" }

            FocusTimerScreen(
                onBack = { currentScreen = "home" },
                onSessionComplete = { durationSeconds ->
                    scope.launch {
                        db.insertFocusSession(
                            FocusSession(
                                userId = currentUserId,
                                durationSeconds = durationSeconds
                            )
                        )
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        val existingLog = db.getDailyLogByDate(currentUserId, today)
                        if (existingLog != null) {
                            db.upsertDailyLog(
                                existingLog.copy(
                                    focusMinutes = existingLog.focusMinutes + durationSeconds / 60
                                )
                            )
                        } else {
                            db.upsertDailyLog(
                                DailyLog(
                                    userId = currentUserId,
                                    date = today,
                                    focusMinutes = durationSeconds / 60
                                )
                            )
                        }
                    }
                }
            )
        } else {
            // Handle back press — navigate within app
            BackHandler(enabled = currentScreen != "home") {
                currentScreen = "home"
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .statusBarsPadding()
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        if (targetState == "detail") {
                            (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                                    (slideOutHorizontally { -it / 3 } + fadeOut())
                        } else {
                            fadeIn() togetherWith fadeOut()
                        }
                    },
                    label = "screenTransition",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        "home" -> DashboardScreen(
                            journeys = journeys.filter { !it.isComplete },
                            userName = currentUserName,
                            profileImagePath = profileImagePath,
                            onNavigate = { dest, journeyId ->
                                currentScreen = dest
                                if (journeyId != null) activeJourneyId = journeyId
                            }
                        )

                        "missions" -> MissionsScreen(
                            journeys = journeys,
                            onNavigate = { dest, journeyId ->
                                currentScreen = dest
                                if (journeyId != null) activeJourneyId = journeyId
                            }
                        )

                        "analytics" -> AnalyticsScreen(
                            userId = currentUserId,
                            db = db
                        )

                        "detail" -> JourneyDetailScreen(
                            journey = journeys.find { it.id == activeJourneyId },
                            onBack = { currentScreen = "home" },
                            onProgress = { id, amount ->
                                scope.launch {
                                    val journey = db.getJourneyById(id)
                                    if (journey != null) {
                                        db.updateJourney(
                                            journey.copy(
                                                progress = (journey.progress + amount)
                                                    .coerceAtMost(journey.target)
                                            )
                                        )
                                        val today = SimpleDateFormat(
                                            "yyyy-MM-dd", Locale.US
                                        ).format(Date())
                                        val existingLog = db.getDailyLogByDate(
                                            currentUserId, today
                                        )
                                        if (existingLog != null) {
                                            db.upsertDailyLog(
                                                existingLog.copy(
                                                    progressLogged = existingLog.progressLogged + amount,
                                                    journeysWorkedOn = existingLog.journeysWorkedOn + 1
                                                )
                                            )
                                        } else {
                                            db.upsertDailyLog(
                                                DailyLog(
                                                    userId = currentUserId,
                                                    date = today,
                                                    progressLogged = amount,
                                                    journeysWorkedOn = 1
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            onGiveUp = { id ->
                                scope.launch {
                                    db.deleteJourneyById(id)
                                }
                                currentScreen = "home"
                            }
                        )

                        "settings" -> SettingsScreen(
                            userName = currentUserName,
                            userEmail = currentUserEmail,
                            profileImagePath = profileImagePath,
                            onChangeProfilePicture = {
                                imagePickerLauncher.launch("image/*")
                            },
                            isDarkMode = isDarkMode,
                            onToggleTheme = {
                                isDarkMode = !isDarkMode
                                sessionManager.setDarkMode(isDarkMode)
                            },
                            onLogout = {
                                sessionManager.clearSession()
                                isAuthenticated = false
                                currentUserId = -1
                                currentUserName = ""
                                currentUserEmail = ""
                                currentScreen = "home"
                            },
                            onResetAccount = {
                                scope.launch {
                                    db.resetUserData(currentUserId)
                                    journeys = emptyList()
                                    backupMessage = "Account data has been reset"
                                }
                            },
                            onDeleteAccount = {
                                scope.launch {
                                    db.deleteUser(currentUserId)
                                    sessionManager.clearSession()
                                    isAuthenticated = false
                                    currentUserId = -1
                                    currentUserName = ""
                                    currentUserEmail = ""
                                    currentScreen = "home"
                                }
                            },
                            onCreateBackup = {
                                scope.launch {
                                    try {
                                        val file = backupManager.createBackup(
                                            db, currentUserId
                                        )
                                        backupMessage = "Backup created: ${file.name}"
                                    } catch (e: Exception) {
                                        backupMessage = "Backup failed: ${e.message}"
                                    }
                                }
                            },
                            onRestoreBackup = {
                                scope.launch {
                                    try {
                                        val latest = backupManager.getLatestBackup()
                                        if (latest != null) {
                                            val success = backupManager.restoreBackup(
                                                db, latest, sessionManager
                                            )
                                            if (success) {
                                                backupMessage = "Restored from ${latest.name}"
                                                currentUserId = sessionManager.getUserId()
                                                currentUserName = sessionManager.getUserName()
                                                currentUserEmail = sessionManager.getUserEmail()
                                            } else {
                                                backupMessage = "Restore failed"
                                            }
                                        } else {
                                            backupMessage = "No backup found"
                                        }
                                    } catch (e: Exception) {
                                        backupMessage = "Restore failed: ${e.message}"
                                    }
                                }
                            },
                            isPeriodicBackupEnabled = isPeriodicBackupEnabled,
                            onTogglePeriodicBackup = { enabled ->
                                isPeriodicBackupEnabled = enabled
                                sessionManager.setPeriodicBackupEnabled(enabled)
                                if (enabled) {
                                    BackupReceiver.schedule(
                                        context,
                                        backupIntervalHours.toLong()
                                    )
                                } else {
                                    BackupReceiver.cancel(context)
                                }
                            },
                            backupIntervalHours = backupIntervalHours,
                            onSetBackupInterval = { hours ->
                                backupIntervalHours = hours
                                sessionManager.setBackupIntervalHours(hours)
                                if (isPeriodicBackupEnabled) {
                                    BackupReceiver.schedule(context, hours.toLong())
                                }
                            },
                            lastBackupInfo = remember {
                                backupManager.getLatestBackup()?.let { file ->
                                    val date = SimpleDateFormat(
                                        "MMM dd, yyyy HH:mm", Locale.US
                                    ).format(Date(file.lastModified()))
                                    "Last backup: $date"
                                }
                            },
                            backupMessage = backupMessage
                        )
                    }
                }

                // FAB - only on home, missions, analytics
                if (currentScreen in setOf("home", "missions", "analytics")) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 88.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = colors.buttonPrimary.copy(alpha = 0.1f),
                                    spotColor = colors.buttonPrimary.copy(alpha = 0.1f)
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.buttonPrimary)
                                .clickable { isCreateModalOpen = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Mission",
                                tint = colors.buttonPrimaryContent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Bottom Nav - show on all main tabs
                if (currentScreen in bottomNavScreens) {
                    BottomNavBar(
                        currentScreen = currentScreen,
                        onNavigate = { currentScreen = it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Create Journey Sheet
                CreateJourneySheet(
                    isOpen = isCreateModalOpen,
                    onClose = { isCreateModalOpen = false },
                    userId = currentUserId,
                    categories = categories,
                    onAddCategory = { cat ->
                        sessionManager.addCategory(cat)
                        categories = sessionManager.getCategories()
                    },
                    onDeleteCategory = { cat ->
                        sessionManager.removeCategory(cat)
                        categories = sessionManager.getCategories()
                    },
                    isDefaultCategory = { sessionManager.isDefaultCategory(it) },
                    units = units,
                    onAddUnit = { u ->
                        sessionManager.addUnit(u)
                        units = sessionManager.getUnits()
                    },
                    onCreate = { newJourney ->
                        scope.launch {
                            db.insertJourney(newJourney)
                        }
                        isCreateModalOpen = false
                    }
                )
            }
        }
    }
}