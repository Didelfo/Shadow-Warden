package dev.didelfo.shadowwarden.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta

@Composable
fun createDialog(
    icono: ImageVector,
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
                imageVector = icono,
                contentDescription = "dasdasd",
                tint = colorIcono
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
                    onClick = { onClick },
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