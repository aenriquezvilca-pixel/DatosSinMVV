package com.example.datossinmvv

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun AgregarUsuario(user: User, dao: UserDao) {
    dao.insert(user)
}

suspend fun getUsers(dao: UserDao): String {
    val users = dao.getAll()
    var listaUsuarios = ""
    for (user in users) {
        listaUsuarios += "${user.uid} - ${user.firstName} - ${user.lastName}\n"
    }
    return listaUsuarios
}

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    var db: UserDatabase
    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }

    db = crearDatabase(context)

    val dao = db.userDao()

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name: ") },
            singleLine = true
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name:") },
            singleLine = true
        )
        Button(
            onClick = {
                val user = User(0, firstName, lastName)
                coroutineScope.launch {
                    AgregarUsuario(user = user, dao = dao)
                }
                firstName = ""
                lastName = ""
            }
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    val data = getUsers(dao = dao)
                    dataUser.value = data
                }
            }
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))
        Text(text = dataUser.value, fontSize = 16.sp)
    }
}
