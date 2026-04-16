package com.example.untildone.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.ui.theme.Emerald500
import com.example.untildone.ui.theme.UntilDoneTheme

@Composable
fun AuthScreen(
    onGoogleLogin: () -> Unit,
    onLocalLogin: (email: String, password: String) -> Unit,
    onSignUp: (name: String, email: String, password: String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors
    var isLoginMode by remember { mutableStateOf(true) }

    // Form state
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val displayError = localError ?: errorMessage

    // Clear errors when switching modes
    LaunchedEffect(isLoginMode) {
        localError = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .rotate(-6f)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = colors.buttonPrimary.copy(alpha = 0.2f),
                    spotColor = colors.buttonPrimary.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(colors.buttonPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.GpsFixed,
                contentDescription = "UntilDone Logo",
                tint = colors.buttonPrimaryContent,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App name
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = colors.textPrimary)) {
                    append("Until")
                }
                withStyle(SpanStyle(color = Emerald500)) {
                    append("Done")
                }
            },
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Great plans deserve relentless execution.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tab toggle: Login / Sign Up
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.inputBackground)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isLoginMode) colors.cardBackground else Color.Transparent)
                    .clickable { isLoginMode = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isLoginMode) colors.textPrimary else colors.textTertiary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isLoginMode) colors.cardBackground else Color.Transparent)
                    .clickable { isLoginMode = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (!isLoginMode) colors.textPrimary else colors.textTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form fields
        if (!isLoginMode) {
            AuthTextField(
                value = name,
                onValueChange = { name = it; localError = null },
                placeholder = "Full Name"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        AuthTextField(
            value = email,
            onValueChange = { email = it; localError = null },
            placeholder = "Email Address",
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it; localError = null },
            placeholder = "Password",
            isPassword = true
        )

        if (!isLoginMode) {
            Spacer(modifier = Modifier.height(12.dp))
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; localError = null },
                placeholder = "Confirm Password",
                isPassword = true
            )
        }

        // Error message
        if (displayError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = displayError,
                style = MaterialTheme.typography.bodySmall,
                color = colors.destructive,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.buttonPrimary)
                .clickable {
                    if (email.isBlank() || password.isBlank()) {
                        localError = "Please fill in all fields"
                        return@clickable
                    }
                    if (!isLoginMode) {
                        if (name.isBlank()) {
                            localError = "Please enter your name"
                            return@clickable
                        }
                        if (password.length < 6) {
                            localError = "Password must be at least 6 characters"
                            return@clickable
                        }
                        if (password != confirmPassword) {
                            localError = "Passwords do not match"
                            return@clickable
                        }
                        onSignUp(name, email.trim(), password)
                    } else {
                        onLocalLogin(email.trim(), password)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoginMode) "Log In" else "Create Account",
                style = MaterialTheme.typography.titleMedium,
                color = colors.buttonPrimaryContent
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // OR divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(colors.border)
            )
            Text(
                text = "  OR  ",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textTertiary
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(colors.border)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue with Google
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.cardBackground)
                .clickable(onClick = onGoogleLogin),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "G",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colors = UntilDoneTheme.colors
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = colors.textTertiary)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.inputBackground,
            unfocusedContainerColor = colors.inputBackground,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            cursorColor = colors.textPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        trailingIcon = if (isPassword) {
            {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                    else Icons.Outlined.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = colors.textTertiary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            }
        } else null
    )
}
