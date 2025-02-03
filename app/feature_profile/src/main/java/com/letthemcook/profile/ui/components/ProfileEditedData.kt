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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateEmail
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validateName
import com.letthemcook.core.domain.validation.AuthorizationDataValidator.validatePhoneNumber
import com.letthemcook.core.domain.validation.result.EmailValidationResult
import com.letthemcook.core.domain.validation.result.NameValidationResult
import com.letthemcook.core.domain.validation.result.PhoneNumberValidationResult
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiAction
import com.letthemcook.profile.domain.viewModels.editedProfile.EditedProfileUiState
import com.letthemcook.theme.base.LocalAppTheme
import com.letthemcook.theme.components.textFields.MultiLineTextField
import com.letthemcook.theme.components.textFields.ValidatedTextField

@Composable
fun ProfileEditedData(
    modifier: Modifier = Modifier,
    uiState: EditedProfileUiState,
    onUiAction: (EditedProfileUiAction) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    // TODO birthDate

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Name",
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.name.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Surname",
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.surname.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Email",
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.email.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Phone number",
                    style = LocalAppTheme.current.typography.bodyMedium
                )
                Text(
                    text = uiState.phoneNumber.text.toString(),
                    style = LocalAppTheme.current.typography.bodyMedium
                )
            }
        } else {
            MultiLineTextField(
                modifier = Modifier.fillMaxWidth(),
                state = uiState.about,
                labelText = "About"
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.name,
                label = "Name",
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
                label = "Surname",
                validationFunction = { toValidateText ->
                    when (validateName(toValidateText)) {
                        NameValidationResult.OK -> ""
                        NameValidationResult.OnlyLettersAllowed -> "Only letters are allowed in surname."
                    }
                }
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.email,
                label = "Email",
                validationFunction = { toValidateText ->
                    when (validateEmail(toValidateText)) {
                        EmailValidationResult.OK -> ""
                        EmailValidationResult.Empty -> ""
                        EmailValidationResult.WrongFormat -> "Please follow the email format, such as example@email.com."
                    }
                }
            )
            ValidatedTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = uiState.phoneNumber,
                label = "Phone number",
                isPhoneNumber = true,
                validationFunction = { toValidateText ->
                    when (validatePhoneNumber(toValidateText)) {
                        PhoneNumberValidationResult.OK -> ""
                        PhoneNumberValidationResult.Empty -> ""
                        PhoneNumberValidationResult.TooShort -> "Phone number is too short."
                        PhoneNumberValidationResult.TooLong -> "Phone number is too long."
                        PhoneNumberValidationResult.OnlyNumbersAllowed -> "Only numbers are allowed."
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
                            onUiAction(EditedProfileUiAction.ChangePassword)
                        }
                        .padding(6.dp),
                    text = "Change password",
                    style = LocalAppTheme.current.typography.bodyMedium,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}