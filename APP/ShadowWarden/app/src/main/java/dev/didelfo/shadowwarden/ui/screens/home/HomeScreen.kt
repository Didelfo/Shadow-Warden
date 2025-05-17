package dev.didelfo.shadowwarden.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import dev.didelfo.shadowwarden.R
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.servers.Server
import dev.didelfo.shadowwarden.config.servers.Servers
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.ui.screens.utils.loadingView
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias
import java.io.File

@Composable
fun HomeScreen(navController: NavHostController){

    val context:Context = LocalContext.current

    val servers = remember { getServers(context) }

    var loading by remember { mutableStateOf<Boolean>(false) }

    val user = JSONCreator().loadObject(context, "user.json", User::class.java)

    Scaffold(
        topBar = {
            ToolBar(
                title = "Servidores",
                userrr = user,
                onImageClick = {

                },
                onAddClick = {
                    navController.navigate(AppScreens.AddServerScreen.route)
                }
            )
        },

        content = { paddingValues ->

            ServersRecyclerView(
                servers = servers,
                modifier = Modifier.padding(paddingValues),
                onItemClick = {server ->
                    // activamos la pantalla de carga
                    loading = true

                    // Intentamos conectar
                    WSController.connect(server)
                    WSController.sendMessage("Movil conectado con exito")

                    // quitamos la pantalla de carga
                    loading = false
                }
            )
        }
    )

    loadingView(loading)

}

// -----------------------------------------------------------
//                   Funciones logicas
// -----------------------------------------------------------

// Conseguir todos los servidores del json
private fun getServers(context: Context): ArrayList<Server> {

    if (File(context.filesDir, "servers.dat").exists()){

        val jsonEncrip = JsonEncripter(context, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))

        val jsonString: String = jsonEncrip.decryptJson(jsonEncrip.readEncryptedFile("servers.dat"))

        return Gson().fromJson(jsonString, Servers::class.java).listaServidores
    } else {
        return ArrayList<Server>()
    }

}

// -----------------------------------------------------------
//                   Funciones composables
// -----------------------------------------------------------

// ToolBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBar(
    title: String,
    userrr: User,
    onImageClick: () -> Unit,
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
        navigationIcon = {
            IconButton(onClick = onImageClick) {
                val imageRequest = ImageRequest.Builder(LocalContext.current)
                    .data(userrr.url)
                    .crossfade(true)
                    .build()

                Image(
                    painter = rememberAsyncImagePainter(imageRequest),
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(52.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add",
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}

// RecyclerView
@Composable
private fun ServersRecyclerView(
    servers: ArrayList<Server>,
    modifier: Modifier = Modifier,
    onItemClick: (Server) -> Unit
){

    LazyColumn (
        modifier = modifier.fillMaxSize()
            .background(AzulOscuroProfundo)
    ){
        items(servers) { server ->
            ServerItem(
                server = server,
                onItemClicck = {onItemClick(server)}
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
            .clickable {onItemClicck()},

        colors = CardDefaults.cardColors(
            containerColor = AzulGrisElegante // o cualquier color que desees
        )
    /*
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
                painter = painterResource(R.drawable.server),
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