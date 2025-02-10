package com.letthemcook.theme.components.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.letthemcook.theme.R
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.buttons.TextButton
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    isShown: Boolean,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isShown) return

    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    val colors = DatePickerDefaults.colors(
        titleContentColor = LocalAppTheme.current.text,
        headlineContentColor = LocalAppTheme.current.text,
        weekdayContentColor = LocalAppTheme.current.text,
        subheadContentColor = LocalAppTheme.current.text,
        navigationContentColor = LocalAppTheme.current.text,
        yearContentColor = LocalAppTheme.current.text,
        currentYearContentColor = LocalAppTheme.current.text,
        selectedYearContentColor = LocalAppTheme.current.background,
        dayContentColor = LocalAppTheme.current.text,
        selectedDayContentColor = LocalAppTheme.current.background,
        todayContentColor = LocalAppTheme.current.text,
        todayDateBorderColor = LocalAppTheme.current.text,
        dividerColor = LocalAppTheme.current.text,
        containerColor = LocalAppTheme.current.screenThree,
        selectedYearContainerColor = LocalAppTheme.current.highlightColor,
        selectedDayContainerColor = LocalAppTheme.current.highlightColor
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = colors,
        shape = RoundedCornerShape(16.dp),
        confirmButton = {
            TextButton(
                modifier = Modifier.size(120.dp, 40.dp),
                text = stringResource(R.string.ok),
                onClick = {
                    datePickerState.selectedDateMillis?.let { timeMillis ->
                        val date = Instant.ofEpochMilli(timeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()

                        onDateSelected(date)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.size(120.dp, 40.dp),
                text = stringResource(R.string.cancel),
                onClick = onDismiss
            )
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = colors
        )
    }
}