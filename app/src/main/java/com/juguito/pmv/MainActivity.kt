package com.juguito.pmv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juguito.pmv.ui.theme.PMVTheme
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// ---- Data Classes para parsear la API ----
data class ApiResponse(val previsiones: List<Prevision>)
data class Prevision(val line: Int, val destino: String, val hora: String, val seconds: Int, val composition: Composition? = null)
data class Composition(val Head: Int?, val Tail: Int?)

val Context.dataStore by preferencesDataStore(name = "settings")
val FAVORITES_KEY = stringSetPreferencesKey("favorites")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PMVTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MetroApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroApp() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    var selectedStop by remember { mutableStateOf("") }
    var previsiones by remember { mutableStateOf<List<Prevision>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val favoritesFlow = remember { getFavorites(context) }
    val favorites by favoritesFlow.collectAsState(initial = emptySet())

    // Tus paradas
    val stops: Map<String, Int> = mapOf(
        "Aeroport" to 121,
        "Alameda" to 14,
        "Albalat dels Sorells" to 5,
        "Alberic" to 36,
        "Alboraia - Palmaret" to 10,
        "Alboraia Peris Aragó" to 9,
        "Alfauir" to 128,
        "Alginet" to 43,
        "Almàssera" to 8,
        "Amistat" to 23,
        "Àngel Guimerà" to 17,
        "Aragó" to 24,
        "Ausiàs March" to 42,
        "Av. del Cid" to 18,
        "Ayora" to 22,
        "Bailén" to 109,
        "Bétera" to 107,
        "Fondo de Benaguasil" to 69,
        "Benaguasil" to 70,
        "Benicalap" to 97,
        "Beniferri" to 54,
        "Benimaclet" to 12,
        "Benimàmet" to 57,
        "Benimodo" to 40,
        "Burjassot" to 72,
        "Burjassot - Godella" to 73,
        "Campament" to 59,
        "Campanar" to 53,
        "Campus" to 103,
        "Cantereria" to 56,
        "Carlet" to 41,
        "Colón" to 15,
        "Col·legi El Vedat" to 50,
        "Dr. Lluch" to 83,
        "El Clot" to 108,
        "Empalme" to 55,
        "Entrepins" to 65,
        "Espioca" to 45,
        "Estadi Ciutat de València" to 130,
        "Platja Malva-rosa" to 81,
        "Facultats – Manuel Broseta" to 13,
        "Faitanar" to 200,
        "Fira València" to 106,
        "Florista" to 99,
        "Foios" to 6,
        "Font Almaguer" to 44,
        "Francesc Cubells" to 122,
        "Fuente del Jarro" to 62,
        "Garbí" to 98,
        "Godella" to 74,
        "Grau – La Marina" to 123,
        "Horta Vella" to 80,
        "Jesús" to 25,
        "L'Alcúdia" to 39,
        "L'Eliana" to 67,
        "La Cadena" to 85,
        "La Canyada" to 63,
        "La Carrasca" to 88,
        "La Coma" to 111,
        "La Cova" to 183,
        "La Granja" to 101,
        "Cabanyal" to 84,
        "La Pobla de Farnals" to 2,
        "La Pobla de Vallbona" to 68,
        "La Presa" to 184,
        "La Vallesa" to 64,
        "Platja les Arenes" to 82,
        "Les Carolines - Fira" to 58,
        "Ll. Llarga - Terramelar" to 114,
        "Llíria" to 71,
        "Machado" to 11,
        "Manises" to 119,
        "Marítim" to 115,
        "Neptú" to 126,
        "Marxalenes" to 95,
        "Mas del Rosari" to 110,
        "Masia de Traver" to 185,
        "Masies" to 79,
        "Massalavés" to 37,
        "Massamagrell" to 3,
        "Massarrojos" to 76,
        "Canyamelar" to 127,
        "Meliana" to 7,
        "Mislata" to 20,
        "Mislata - Almassil" to 21,
        "Moncada - Alfara" to 77,
        "Montesol" to 66,
        "Montortal" to 38,
        "Museros" to 4,
        "Nou d'Octubre" to 19,
        "Omet" to 46,
        "Orriols" to 129,
        "Paiporta" to 31,
        "Palau de Congressos" to 100,
        "Paterna" to 60,
        "Patraix" to 26,
        "Picanya" to 32,
        "Picassent" to 47,
        "Pl. Espanya" to 51,
        "Pont de Fusta" to 92,
        "Trinitat" to 91,
        "Quart de Poblet" to 117,
        "Rafelbunyol" to 1,
        "Realón" to 49,
        "Reus" to 94,
        "Riba-roja de Túria" to 186,
        "Rocafort" to 75,
        "Roses" to 120,
        "Safranar" to 27,
        "Sagunt" to 93,
        "Salt de l'Aigua" to 118,
        "Sant Isidre" to 28,
        "Sant Joan" to 102,
        "Sant Miquel dels Reis" to 131,
        "Sant Ramon" to 48,
        "Parc Científic" to 113,
        "Santa Rita" to 61,
        "Seminari - CEU" to 78,
        "Beteró" to 86,
        "Tarongers – Ernest Lluch" to 87,
        "Túria" to 52,
        "Tomás y Valiente" to 112,
        "Gallipont - Torre del Virrei" to 201,
        "Torrent" to 33,
        "Torrent Avinguda" to 34,
        "Tossal del Rei" to 132,
        "Trànsits" to 96,
        "À Punt" to 105,
        "Universitat Politècnica" to 89,
        "València la Vella" to 188,
        "València Sud" to 30,
        "Vicent Andrés Estellés" to 104,
        "Vicente Zaragozá" to 90,
        "Castelló" to 35,
        "Xàtiva" to 16,
        "Alacant" to 190,
        "Amado Granell-Montolivet" to 192,
        "Ciutat Arts i Ciències - Justícia" to 194,
        "Moreres" to 196,
        "Natzaret" to 197,
        "Oceanogràfic" to 195,
        "Quatre Carreres" to 193,
        "Russafa" to 191
    )

    val sortedStops = remember(favorites) {
        val favStops = stops.filterKeys { it in favorites }
        val nonFavStops = stops.filterKeys { it !in favorites }
        favStops + nonFavStops
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp)
    ) {
        Text("Selecciona una parada:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedStop,
                onValueChange = {},
                readOnly = true,
                label = { Text("Parada") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sortedStops.forEach { (name, code) ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name)
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            val newFavorites = if (name in favorites) {
                                                favorites - name
                                            } else {
                                                favorites + name
                                            }
                                            saveFavorites(context, newFavorites)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (name in favorites) {
                                            Icons.Default.Star // ⭐ rellena
                                        } else {
                                            Icons.Outlined.StarBorder // ☆ vacía
                                        },
                                        contentDescription = "Favorito",
                                        tint = if(name in favorites) Color.Yellow else Color.Gray
                                    )
                                }
                            }
                        },
                        onClick = {
                            selectedStop = name
                            expanded = false

                            // Llamada a la API
                            scope.launch {
                                isLoading = true
                                val result = getMetroInfo(code)
                                previsiones = result ?: emptyList()
                                isLoading = false
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if(isLoading){
            CircularProgressIndicator()
        } else if (previsiones.isEmpty()) {
            Text("No hay previsiones disponibles", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(previsiones) { prevision ->
                    val vagones = if (prevision.composition?.Head == prevision.composition?.Tail) 1 else 2
                    val minutos = if (prevision.seconds > 60) prevision.seconds / 60 else 0
                    val segundos = prevision.seconds % 60
                    val resto = if(minutos != 0) "${minutos} m ${segundos}s" else "${segundos}s"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🚇 Línea: ${prevision.line}", style = MaterialTheme.typography.bodyLarge)
                            Text("🎯 Destino: ${prevision.destino}", style = MaterialTheme.typography.bodyLarge)
                            val vagones = when {
                                prevision.composition != null -> if (prevision.composition.Head != prevision.composition.Tail) 2 else 1
                                else -> "N/A"
                            }
                            Text("🚋 Vagones: $vagones", style = MaterialTheme.typography.bodyLarge)
                            Text("🕒 Hora: ${prevision.hora}, en ${resto}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (selectedStop.isNotEmpty()) {
                    val stopId = stops[selectedStop]!!
                    scope.launch {
                        isLoading = true
                        val result = getMetroInfo(stopId)
                        previsiones = result ?: emptyList()
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Recargar")
        }

    }

}

// ---- Función de red ----
suspend fun getMetroInfo(stopId: Int): List<Prevision>? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://metroapi.alexbadi.es/prevision/$stopId/parse")
        .build()

    return withContext(Dispatchers.IO) {
        try {
            client.newCall(request).execute().use { response ->

                val body = response.body?.string()

                if (!response.isSuccessful || body == null) return@withContext null

                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val adapter = moshi.adapter(ApiResponse::class.java)
                val parsed = adapter.fromJson(body)

                parsed?.previsiones
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

suspend fun saveFavorites(context: Context, favorites: Set<String>) {
    context.dataStore.edit { prefs ->
        prefs[FAVORITES_KEY] = favorites
    }
}

fun getFavorites(context: Context): Flow<Set<String>> {
    return context.dataStore.data.map { prefs ->
        prefs[FAVORITES_KEY] ?: emptySet()
    }
}



