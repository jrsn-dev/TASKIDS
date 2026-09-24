package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.viewmodel.MainViewModel

@Composable
fun DeviceConnectionSection(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val savedHost by viewModel.linkHost.collectAsState()
    val savedCode by viewModel.linkCode.collectAsState()
    val status by viewModel.linkStatus.collectAsState()
    var host by remember(savedHost) { mutableStateOf(savedHost) }
    var code by remember(savedCode) { mutableStateOf(savedCode) }

    Column(modifier.verticalScroll(rememberScrollState()).padding(12.dp)) {
        Text("Conectar celular e TV", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black, fontSize = 26.sp)
        Spacer(Modifier.height(12.dp))
        if (viewModel.isTelevision) {
            Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp)).padding(20.dp)) {
                Text("Nesta TV", color = TaskIdsColors.Blue, fontWeight = FontWeight.Bold)
                Text("Endereço: ${viewModel.tvAddress}", color = TaskIdsColors.Ink, fontSize = 22.sp)
                Text("Código: ${viewModel.tvCode}", color = TaskIdsColors.Ink, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("Abra Área dos Pais > Conectar TV no celular. Use o mesmo Wi-Fi nos dois aparelhos.",
                    color = TaskIdsColors.Muted, fontSize = 14.sp)
            }
            Spacer(Modifier.height(14.dp))
        }
        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp)).padding(20.dp)) {
            Text(if (viewModel.isTelevision) "Conectar a outra TV" else "Vincular com a TV", color = TaskIdsColors.Ink,
                fontWeight = FontWeight.Black, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(host, { host = it.filter { c -> c.isDigit() || c == '.' }.take(15) },
                label = { Text("Endereço IPv4 exibido na TV") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(code, { code = it.filter(Char::isDigit).take(6) },
                label = { Text("Código de 6 dígitos") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            PrimaryTvButton("Testar conexão", TaskIdsColors.Blue, Modifier.fillMaxWidth()) {
                viewModel.updateLinkDetails(host, code); viewModel.testLink()
            }
            Spacer(Modifier.height(10.dp))
            PrimaryTvButton("Enviar perfil para TV", TaskIdsColors.Purple, Modifier.fillMaxWidth()) {
                viewModel.updateLinkDetails(host, code); viewModel.sendToTv()
            }
            Spacer(Modifier.height(10.dp))
            PrimaryTvButton("Receber progresso da TV", TaskIdsColors.Green, Modifier.fillMaxWidth()) {
                viewModel.updateLinkDetails(host, code); viewModel.receiveFromTv()
            }
            Spacer(Modifier.height(12.dp))
            Text(status, color = TaskIdsColors.Ink, fontWeight = FontWeight.Bold)
            Text("Envio e recebimento são manuais. Ao receber, os dados do perfil com o mesmo nome neste aparelho são atualizados.",
                color = TaskIdsColors.Muted, fontSize = 13.sp)
        }
    }
}
