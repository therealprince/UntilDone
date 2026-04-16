package com.example.untildone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.data.Journey
import com.example.untildone.data.SessionManager
import com.example.untildone.ui.theme.Emerald500
import com.example.untildone.ui.theme.UntilDoneTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateJourneySheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    onCreate: (Journey) -> Unit,
    userId: Long,
    categories: List<String>,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    isDefaultCategory: (String) -> Boolean,
    units: List<String>,
    onAddUnit: (String) -> Unit
) {
    if (!isOpen) return

    val colors = UntilDoneTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("90") }
    var dailyTarget by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf(if (units.isNotEmpty()) units.last() else "sessions") }
    var tag by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first() else "SKILL") }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showAddUnitDialog by remember { mutableStateOf(false) }
    var newUnitName by remember { mutableStateOf("") }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = {
                Text("Add Category", color = colors.textPrimary)
            },
            text = {
                TextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("e.g. CODING", color = colors.textTertiary) },
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
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        val cat = newCategoryName.uppercase().trim()
                        onAddCategory(cat)
                        tag = cat
                        newCategoryName = ""
                        showAddCategoryDialog = false
                    }
                }) {
                    Text("Add", color = Emerald500)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newCategoryName = ""
                    showAddCategoryDialog = false
                }) {
                    Text("Cancel", color = colors.textTertiary)
                }
            },
            containerColor = colors.elevatedSurface
        )
    }

    // Add Unit Dialog
    if (showAddUnitDialog) {
        AlertDialog(
            onDismissRequest = { showAddUnitDialog = false },
            title = {
                Text("Add Unit", color = colors.textPrimary)
            },
            text = {
                TextField(
                    value = newUnitName,
                    onValueChange = { newUnitName = it },
                    placeholder = { Text("e.g. chapters", color = colors.textTertiary) },
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
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newUnitName.isNotBlank()) {
                        val u = newUnitName.lowercase().trim()
                        onAddUnit(u)
                        unit = u
                        newUnitName = ""
                        showAddUnitDialog = false
                    }
                }) {
                    Text("Add", color = Emerald500)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newUnitName = ""
                    showAddUnitDialog = false
                }) {
                    Text("Cancel", color = colors.textTertiary)
                }
            },
            containerColor = colors.elevatedSurface
        )
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = colors.elevatedSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(colors.textTertiary.copy(alpha = 0.3f))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Start a Mission",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.tagBackground)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Goal input
            FormLabel(text = "GOAL")
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text("e.g. Learn React Native", color = colors.textTertiary)
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category tags with add/delete
            FormLabel(text = "CATEGORY")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { t ->
                    val isSelected = tag == t
                    val isDefault = isDefaultCategory(t)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.background(colors.buttonPrimary)
                                } else {
                                    Modifier.border(
                                        1.dp, colors.border, RoundedCornerShape(8.dp)
                                    )
                                }
                            )
                            .clickable { tag = t }
                            .padding(start = 12.dp, end = if (!isDefault) 6.dp else 12.dp, top = 6.dp, bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = t,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) colors.buttonPrimaryContent else colors.tagText
                            )
                            // Delete button for custom categories
                            if (!isDefault) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete $t",
                                    tint = if (isSelected) colors.buttonPrimaryContent.copy(alpha = 0.7f)
                                    else colors.textTertiary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            onDeleteCategory(t)
                                            if (tag == t) {
                                                tag = categories.firstOrNull() ?: "SKILL"
                                            }
                                        }
                                )
                            }
                        }
                    }
                }

                // Add category button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, colors.border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { showAddCategoryDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Category",
                        tint = colors.textTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target & Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Target
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel(text = "TARGET")
                    TextField(
                        value = target,
                        onValueChange = { target = it.filter { c -> c.isDigit() } },
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
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                // Unit Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel(text = "UNIT")
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.inputBackground)
                                .clickable { unitDropdownExpanded = true }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = unit,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = colors.textPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select unit",
                                    tint = colors.textTertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = unitDropdownExpanded,
                            onDismissRequest = { unitDropdownExpanded = false },
                            modifier = Modifier.background(colors.dropdownBackground)
                        ) {
                            units.forEach { u ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = u,
                                            color = if (u == unit) colors.textPrimary
                                            else colors.textSecondary,
                                            fontWeight = if (u == unit) FontWeight.Bold
                                            else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        unit = u
                                        unitDropdownExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider(color = colors.border)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Emerald500,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Add Custom",
                                            color = Emerald500,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                },
                                onClick = {
                                    unitDropdownExpanded = false
                                    showAddUnitDialog = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily minimum
            FormLabel(text = "DAILY MINIMUM ($unit)")
            TextField(
                value = dailyTarget,
                onValueChange = { dailyTarget = it.filter { c -> c.isDigit() } },
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            val isEnabled = title.isNotBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isEnabled) colors.buttonPrimary
                        else colors.textTertiary.copy(alpha = 0.3f)
                    )
                    .then(
                        if (isEnabled) Modifier.clickable {
                            onCreate(
                                Journey(
                                    userId = userId,
                                    title = title,
                                    tag = tag,
                                    progress = 0,
                                    target = target.toIntOrNull() ?: 90,
                                    dailyTarget = dailyTarget.toIntOrNull() ?: 1,
                                    unit = unit
                                )
                            )
                            title = ""
                            target = "90"
                            dailyTarget = "1"
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Commit to Mission",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isEnabled) colors.buttonPrimaryContent
                    else colors.textTertiary
                )
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    val colors = UntilDoneTheme.colors
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize = 10.sp
        ),
        color = colors.textTertiary,
        modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
    )
}
