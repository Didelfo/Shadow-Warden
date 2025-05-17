package dev.didelfo.shadowwarden.ui.screens.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.theme.VerdeEsmeralda
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta

@Composable
fun createDialogInfo(
    icono: Painter,
    colorIcono: Color,
    titulo: String,
    colorTitulo: Color,
    texto: String,
    boton: String,
    onClick: () -> Unit
){
    AlertDialog(
        icon = {
            Icon(
                painter = icono,
                contentDescription = "dasdasd",
                tint = colorIcono,
                modifier = Modifier.size(25.dp)
            )
        },
        title = {
            Text(
                text = titulo,
                color = colorTitulo,
                fontFamily = OpenSanBold
            )
        },
        text = {
            Text(
                text = texto,
                color = VerdeMenta,
                fontFamily = OpenSanNormal
            )
        },
        onDismissRequest = {},
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onClick() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulVerdosoOscuro,
                        contentColor = VerdeMenta
                    )

                ) {
                    Text(
                        text = boton,
                        color = VerdeMenta,
                        fontFamily = OpenSanBold
                    )
                }
            }
        },
        containerColor = AzulOscuroProfundo,

        )
}


@Composable
fun createDialogOpti(
    icono: Painter,
    colorIcono: Color,
    titulo: String,
    colorTitulo: Color,
    texto: String,
    boton1: String,
    color1: Color,
    boton2: String,
    color2: Color,
    onClick1: () -> Unit,
    onClick2: () -> Unit
){
    AlertDialog(
        icon = {
            Icon(
                painter = icono,
                contentDescription = "dasdasd",
                tint = colorIcono,
                modifier = Modifier.size(25.dp)
            )
        },
        title = {
            Text(
                text = titulo,
                color = colorTitulo,
                fontFamily = OpenSanBold
            )
        },
        text = {
            Text(
                text = texto,
                color = VerdeMenta,
                fontFamily = OpenSanNormal
            )
        },
        onDismissRequest = {},
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onClick1() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color1
                    )
                ) {
                    Text(
                        text = boton1,
                        color = AzulOscuroProfundo,
                        fontFamily = OpenSanBold
                    )
                }

                Spacer(modifier = Modifier.width(25.dp))

                Button(
                    onClick = { onClick2() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color2
                    )
                ) {
                    Text(
                        text = boton2,
                        color = AzulOscuroProfundo,
                        fontFamily = OpenSanBold
                    )
                }

            }
        },
        containerColor = AzulOscuroProfundo,

        )
}
