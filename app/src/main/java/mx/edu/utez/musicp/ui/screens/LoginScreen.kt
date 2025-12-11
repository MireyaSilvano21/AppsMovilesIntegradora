package mx.edu.utez.musicp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import mx.edu.utez.musicp.R
import mx.edu.utez.musicp.viewmodel.LoginViewModel
import mx.edu.utez.peeeli.ui.components.buttons.PrimaryButton
import mx.edu.utez.peeeli.ui.components.images.CircularImage
import mx.edu.utez.peeeli.ui.components.inputs.PasswordField
import mx.edu.utez.peeeli.ui.components.inputs.UserInputField
import mx.edu.utez.peeeli.ui.components.texts.Link
import mx.edu.utez.peeeli.ui.components.texts.Title
import kotlin.text.isNotEmpty


@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        CircularImage(R.drawable.noposter)
        Title("PLaylist\n")

        UserInputField(
            viewModel = viewModel,
            label = "Usuario"
        )

        PasswordField(
            viewModel = viewModel,
            label = "Contraseña"
        )

        if (viewModel.loginError.value.isNotEmpty()) {
            Text(
                text = viewModel.loginError.value,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Link("¿Has olvidado la contraseña?") {
            navController.navigate("forgot_password")
        }

        PrimaryButton("Iniciar sesión") {
            viewModel.login {
                navController.navigate("main") {
                    NavOptionsBuilder.popUpTo("login") {
                        PopUpToBuilder.inclusive = true
                    } // Evita volver al login
                }
            }
        }

        Link("¿No tienes cuenta? Regístrate") {
            navController.navigate("register")
        }

    }
}
