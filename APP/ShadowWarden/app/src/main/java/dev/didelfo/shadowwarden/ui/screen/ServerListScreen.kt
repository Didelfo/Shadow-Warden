package dev.didelfo.shadowwarden.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.config.servers.Server
import dev.didelfo.shadowwarden.config.servers.Servers
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.utils.json.JSONCreator

@Composable
fun ServerListScreen(navController: NavHostController) {

    val context: Context = LocalContext.current

    val servers = remember { getServers(context) }

    Scaffold(
        topBar = {
            viewToolBarHomeScreen(
                title = "Servidores",
                onHomeClick = {
                    navController.navigate(AppScreens.HomeScreen.route)
                },
                onAddClick = {
                    navController.navigate(AppScreens.AddServerScreen.route)
                }
            )
        },
        content = { paddingValues ->
            ServerListView(
                servers = servers,
                modifier = Modifier.padding(paddingValues),
                onItemClicck = {server ->
                    Log.d("prueba", server.name)
                }
            )
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun viewToolBarHomeScreen(
    title: String,
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                color = VerdeMenta,
                fontFamily = OpenSanBold,
                fontSize = 26.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AzulVerdosoOscuro
        ),
        actions = {
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = VerdeMenta
                )
            }
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = VerdeMenta
                )
            }
        }
    )
}


// Conseguir todos los servidores del json
private fun getServers(context: Context): ArrayList<Server> {
    if (JSONCreator().exist(context, "servers.json")){
        return JSONCreator().loadObject(context, "servers.json", Servers::class.java).listaServidores
    } else {
        return ArrayList<Server>()
    }

}


// Recicler view
@Composable
private fun ServerListView(
    servers: ArrayList<Server>,
    modifier: Modifier = Modifier,
    onItemClicck: (Server) -> Unit
){

    LazyColumn (
        modifier = modifier.fillMaxSize()
            .background(AzulOscuroProfundo)
    ){
        items(servers) { server ->
            ServerItem(
                server = server,
                onItemClicck = {onItemClicck(server)}
            )

        }
    }

}

// Vista de cada item
@Composable
private fun ServerItem(
    server: Server,
    onItemClicck: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =  16.dp, vertical = 8.dp)
            .clickable {onItemClicck}/*
        elevation = CardDefaults.cardColors(
            containerColor = AzulVerdosoOscuro
        )
       */
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono (asumiendo que tienes los iconos como recursos vectoriales)
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Server Icon",
                tint = VerdeMenta,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = server.name,
                color = VerdeMenta,
                fontSize = 18.sp,
                fontFamily = OpenSanBold,
                modifier = Modifier.weight(1f)
            )

            // Podrías añadir más elementos como estado, etc.
        }
    }
}