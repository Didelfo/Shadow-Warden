package dev.didelfo.shadowwarden.ui.screens.server

import  dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.utils.GridItem


// -----------------------------------------------------------
//                   Principal
// -----------------------------------------------------------

@Composable
fun ServerHomeScreen(navController: NavHostController){

    // Variables principales

    val isEditing = remember { mutableStateOf(false) }
    val items = remember {
        mutableStateListOf(
            GridItem(Icons.Default.Home, "Inicio"),
            GridItem(Icons.Default.Person, "Perfil"),
            GridItem(Icons.Default.Settings, "Ajustes"),
            GridItem(Icons.Default.Info, "Acerca de"),
            GridItem(Icons.Default.Phone, "Contacto"),
            GridItem(Icons.Default.Email, "Correo"),
            GridItem(Icons.Default.Star, "Favoritos"),
            GridItem(Icons.Default.ShoppingCart, "Carrito")
        )
    }

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }


    Scaffold(
        topBar = {
            ToolBarNewHome(
                title = "",
                {
                    navController.navigate(AppScreens.HomeScreen.route)
                },
                {},
                {
                    if (isEditing.value){
                        isEditing.value = false
                    } else {
                        isEditing.value = true
                    }
                }
            )
        },

        content = { paddingValues ->


            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                itemsIndexed(items) { index, item ->
                    GridItemCard(
                        item = item,
                        isEditing = isEditing.value,
                        isBeingDragged = draggingIndex == index,
                        onClick = {
                            if (!isEditing.value) {
                                // Acción real
                                println("Item clicado: ${item.text}")
                            }
                        },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .pointerInput(isEditing.value) {
                                if (!isEditing.value) return@pointerInput

                                detectDragGestures(
                                    onDragStart = { draggingIndex = index },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y

                                        val targetIndex = detectTargetIndex(
                                            items.size,
                                            draggingIndex!!,
                                            offsetX,
                                            offsetY,
                                            columns = 4
                                        )
                                        if (targetIndex != null && targetIndex != draggingIndex) {
                                            items.swap(draggingIndex!!, targetIndex)
                                            draggingIndex = targetIndex
                                            offsetX = 0f
                                            offsetY = 0f
                                        }
                                    },
                                    onDragEnd = {
                                        offsetX = 0f
                                        offsetY = 0f
                                        draggingIndex = null
                                    },
                                    onDragCancel = {
                                        offsetX = 0f
                                        offsetY = 0f
                                        draggingIndex = null
                                    }
                                )
                            }
                            .graphicsLayer {
                                if (draggingIndex == index) {
                                    translationX = offsetX
                                    translationY = offsetY
                                    scaleX = 1.05f
                                    scaleY = 1.05f
                                    shadowElevation = 8f
                                }
                            }
                    )
                }
            }

            

        }
    )

}

// -----------------------------------------------------------
//                   Funciones logicas
// -----------------------------------------------------------


fun <T> MutableList<T>.swap(from: Int, to: Int) {
    if (from in indices && to in indices) {
        val temp = this[from]
        this[from] = this[to]
        this[to] = temp
    }
}

fun detectTargetIndex(
    listSize: Int,
    fromIndex: Int,
    offsetX: Float,
    offsetY: Float,
    columns: Int
): Int? {
    val dx = (offsetX / 200).toInt()
    val dy = (offsetY / 200).toInt()
    val row = fromIndex / columns
    val col = fromIndex % columns
    val targetRow = row + dy
    val targetCol = col + dx
    val targetIndex = targetRow * columns + targetCol
    return if (targetIndex in 0 until listSize) targetIndex else null
}






// -----------------------------------------------------------
//                   Funciones composables
// -----------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBarNewHome(
    title: String,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onEditClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = VerdeMenta,
                fontFamily = OpenSanBold,
                fontSize = 26.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AzulVerdosoOscuro
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Atrás",
                    tint = VerdeMenta
                )
            }
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    painter = painterResource(R.drawable.reload),
                    contentDescription = "Refrescar",
                    tint = VerdeMenta
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Editar",
                    tint = VerdeMenta
                )
            }
        }
    )
}


@Composable
fun GridItemCard(
    item: GridItem,
    isEditing: Boolean,
    isBeingDragged: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clickable(enabled = !isEditing, onClick = onClick)
            .background(
                color = if (isEditing) Color(0xFFEDE7F6) else Color(0xFFFFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isEditing) 2.dp else 1.dp,
                color = if (isBeingDragged) Color.Red else if (isEditing) Color(0xFF7E57C2) else Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.text,
                tint = if (isEditing) Color(0xFF512DA8) else Color(0xFF3F51B5),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (isEditing) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Arrastrar",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
