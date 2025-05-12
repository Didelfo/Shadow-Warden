package dev.didelfo.shadowwarden.utils.manager


import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import dev.didelfo.shadowwarden.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.Cian
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.theme.VerdeEsmeralda
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta

object AddServerManager {

//===========================================
//         Variables Fundamentales
//===========================================

    // Varriables de Icono Head
    private var statusHeadIconSecure:Boolean = false
    private var textType:String = "Palabras"

    // Variables iconos
    private var icon1:Boolean = false
    private var icon2:Boolean = false
    private var icon3:Boolean = false

    // Varialbes TextField
    private var showTextFiel:Boolean = false
    private var nameServer by mutableStateOf("")

    // Variables Button
    private var textButton:String = "Generar"

    // Varialbe Mostrar vista palabras
    private var showWords:Boolean = false


//===========================================
//         Funciones Logicas
//===========================================


    


//===========================================
//         Funciones Composable
//===========================================
    @Composable
    fun getIconHead() {
        Icon(
            painter =  if (statusHeadIconSecure) {
                painterResource(R.drawable.lock_close)
            } else {
                painterResource(R.drawable.lock_open)
            },
            contentDescription = "Back",
            tint = if (statusHeadIconSecure) {
                VerdeEsmeralda
            } else {
                RojoCoral
            },
            modifier = Modifier.size(35.dp)
        )
    }


    @Composable
    fun getTextDescripcion(){
        Text(
            text =
                when (textType){
                    "Palabras" -> "Genera las palabras de seguridad."
                    "QR" -> "Escanea el QR."
                    "Nombre" -> "Introduce el nombre del servidor."
                    else -> ""
            },
            color = VerdeMenta,
            fontSize = 16.sp,
            fontFamily = OpenSanNormal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)

        )
    }


    @Composable
    fun getIconsStatus(){
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (icon1) {
                            VerdeEsmeralda
                        } else {
                            VerdeMenta
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.generate_words),
                    contentDescription = "Icono",
                    tint = AzulVerdosoOscuro,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(15.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (icon2) {
                            VerdeEsmeralda
                        } else {
                            VerdeMenta
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.qr),
                    contentDescription = "Icono",
                    tint = AzulVerdosoOscuro,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(15.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (icon3) {
                            VerdeEsmeralda
                        } else {
                            VerdeMenta
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.rename),
                    contentDescription = "Icono",
                    tint = AzulVerdosoOscuro,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    @Composable
    fun getTextField(){
        if (showTextFiel){
            TextField(
                value = nameServer,
                onValueChange = { nameServer = it },
                modifier = Modifier
                    .width(280.dp)
                    .height(48.dp)
                    .background(AzulVerdosoOscuro, RoundedCornerShape(16.dp))
                    .border(2.dp, Cian, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(17.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AzulVerdosoOscuro,
                    unfocusedContainerColor = AzulVerdosoOscuro,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = OpenSanNormal,
                    color = VerdeMenta,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
    }

    @Composable
    fun getButton(){
        Button(
            onClick = {
                when (textButton){
                    "Generar" -> {

                    }
                    "Escanear" -> {

                    }
                    "Nombrar" -> {

                    }
                    "Finalizar" -> {

                    }
                    else -> {}
                }
            },
            modifier = Modifier
                .width(150.dp)
                .height(40.dp)
                .background(AzulVerdosoOscuro, RoundedCornerShape(20.dp))
                .border(2.dp, color = Cian, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulVerdosoOscuro,
                contentColor = VerdeMenta
            )
        ) {
            Text(
                text = textButton,
                fontSize = 16.sp,
                fontFamily = OpenSanBold
            )
        }
    }


}