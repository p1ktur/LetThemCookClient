package com.letthemcook.profile.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.format.prettyString
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateEmail
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateName
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePhoneNumber
import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.NameValidationResult
import com.letthemcook.core.domain.validation.result.PhoneNumberValidationResult
import com.letthemcook.profile.R
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiAction
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.dialogs.DatePickerDialog
import com.letthemcook.theme.components.textFields.MultiLineTextField
import com.letthemcook.theme.components.textFields.ValidatedTextField

@Composable
fun ProfileEditedData(
    modifier: Modifier = Modifier,
    uiState: EditedProfileUiState,
    onUiAction: (EditedProfileUiAction) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    var isBirthDateDialogShown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable {
                    isExpanded = !isExpanded
                    if (!isExpanded) {
                        onUiAction(EditedProfileUiAction.UpdateUserData)
                    }
                }
                .padding(4.dp),
            imageVector = if (isExpanded) {
                Icons.Default.Done
            } else {
                Icons.Default.Edit
            },
            contentDescription = "Expand Button",
            tint = LocalAppTheme.current.text
        )
        if (!isExpanded) {
            if (uiState.about.text.isNotEmpty()) Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.about),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                if (uiState.about.text.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.about.text.toString(),
                        style = LocalAppTheme.current.typography.bodyMedium
                    )
                }
            }
            if (uiState.name.text.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.name),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.name.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            if (uiState.surname.text.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.surname),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.surname.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            if (uiState.email.text.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.email),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.email.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            if (uiState.birthDate != null) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.birth_date),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.birthDate.prettyString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            if (uiState.phoneNumber.text.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.phone_number),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.phoneNumber.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
        } else {
            val pleaseFollowEmailFormat = stringResource(R.string.please_follow_email_format)
            val phoneNumberTooShort = stringResource(R.string.phone_number_too_short)
            val phoneNumberTooLong = stringResource(R.string.phone_number_too_long)
            val onlyNumbersAllowed = stringResource(R.string.only_numbers_allowed)

            MultiLineTextField(
                modifier = Modifier.fillMaxWidth(),
                state = uiState.about,
                labelText = stringResource(R.string.about)
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.name,
                label = stringResource(R.string.name),
                validationFunction = { toValidateText ->
                    when (validateName(toValidateText)) {
                        NameValidationResult.OK -> ""
                        NameValidationResult.OnlyLettersAllowed -> "Only letters are allowed in name."
                    }
                }
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.surname,
                label = stringResource(R.string.surname),
                validationFunction = { toValidateText ->
                    when (validateName(toValidateText)) {
                        NameValidationResult.OK -> ""
                        NameValidationResult.OnlyLettersAllowed -> "Only letters are allowed in surname."
                    }
                }
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        isBirthDateDialogShown = true
                    }
                    .padding(8.dp),
                text = stringResource(R.string.birth_date) + ": " +
                        (uiState.birthDate?.prettyString() ?: stringResource(R.string.unspecified)),
                style = LocalAppTheme.current.typography.bodyLarge
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.email,
                label = stringResource(R.string.email),
                validationFunction = { toValidateText ->
                    when (validateEmail(toValidateText)) {
                        EmailValidationResult.OK -> ""
                        EmailValidationResult.Empty -> ""
                        EmailValidationResult.WrongFormat -> pleaseFollowEmailFormat
                    }
                }
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.phoneNumber,
                label = stringResource(R.string.phone_number),
                isPhoneNumber = true,
                validationFunction = { toValidateText ->
                    when (validatePhoneNumber(toValidateText)) {
                        PhoneNumberValidationResult.OK -> ""
                        PhoneNumberValidationResult.Empty -> ""
                        PhoneNumberValidationResult.TooShort -> phoneNumberTooShort
                        PhoneNumberValidationResult.TooLong -> phoneNumberTooLong
                        PhoneNumberValidationResult.OnlyNumbersAllowed -> onlyNumbersAllowed
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onUiAction(EditedProfileUiAction.NavigateToChangePassword)
                        }
                        .padding(6.dp),
                    text = stringResource(R.string.change_password),
                    style = LocalAppTheme.current.typography.bodyMedium,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }

    DatePickerDialog(
        isShown = isBirthDateDialogShown,
        onDateSelected = { date ->
            onUiAction(EditedProfileUiAction.SetBirthDate(date))

            isBirthDateDialogShown = false
        },
        onDismiss = {
            isBirthDateDialogShown = false
        }
    )
}