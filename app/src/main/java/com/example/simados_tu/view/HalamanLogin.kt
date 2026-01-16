package com.example.simados_tu.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simados_tu.R
import com.example.simados_tu.viewmodel.LoginStatus
import com.example.simados_tu.viewmodel.LoginViewModel
import com.example.simados_tu.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanLogin(
    onLoginSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Bagian Logo
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(248.dp)
                .padding(bottom = 12.dp)
        )

        //Card Input (Container Putih dengan Shadow/Elevation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(60.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selamat Datang",
                    color = colorResource(R.color.green),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Input Email
                OutlinedTextField(
                    value = uiState.loginDetails.email,
                    onValueChange = { viewModel.updateUiState(uiState.loginDetails.copy(email = it)) },
                    label = { Text(text = "Email")},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Password
                OutlinedTextField(
                    value = uiState.loginDetails.password,
                    onValueChange = {
                        viewModel.updateUiState(uiState.loginDetails.copy(password = it))
                    },
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    //menambahkan visualTransformation untuk menyembunyikan teks password
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Login
                Button(
                    onClick = { viewModel.login() },
                    enabled = uiState.isEntryValid && uiState.loginStatus !is LoginStatus.Loading,
                    modifier = Modifier
                        .height(50.dp)
                        .width(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.yellow),
                        contentColor = colorResource(R.color.green)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        // 3. Status Login (Loading atau Error di luar Card)
        when (val status = uiState.loginStatus) {
            is LoginStatus.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = colorResource(R.color.green)
                )
            }
            is LoginStatus.Success -> {
                // LaunchedEffect memastikan navigasi hanya dipanggil sekali
                androidx.compose.runtime.LaunchedEffect(status.token) {
                    onLoginSuccess(status.token)
                }
            }
            else -> {}
        }
    }

    // AlertDialog untuk Error
    if (uiState.loginStatus is LoginStatus.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetLoginStatus() },
            title = {
                Text(
                    text = "Login Gagal",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.red)
                )
            },
            text = {
                Text(
                    text = (uiState.loginStatus as LoginStatus.Error).message,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetLoginStatus() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.green)
                    )
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}