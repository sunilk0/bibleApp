package com.application.bibileapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.application.bibileapp.ui.viewmodel.BibleUIState
import com.application.bibileapp.ui.viewmodel.BibleViewModel

@Composable
fun BibleScreen(
    bibleViewModel: BibleViewModel,
    navController: NavHostController
) {
      val state by bibleViewModel.state.collectAsState()
    var query by remember { mutableStateOf("john3:16") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {  bibleViewModel.fetchVerses(query) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
            value= query,
            onValueChange = { query = it },
            label = {Text("Enter verse eg: John 3:16")
            })

        Button(onClick = {
            if(query.isEmpty()){
                Toast.makeText(context,"Enter verse or chapter",Toast.LENGTH_SHORT).show()
            }else {
                bibleViewModel.fetchVerses(query)
            }
                         }, modifier = Modifier.padding(8.dp)) {
            Text("Search")
        }

        when(state){
            is BibleUIState.Loading -> CircularProgressIndicator()
            is BibleUIState.Success -> {
            val response =  (state as BibleUIState.Success).apiResponse
                val items = response?.verses ?: emptyList()
                //Reference heading:
               Text("Reference: ${response?.reference }")
                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), contentPadding = PaddingValues(16.dp)) {
                    items(items) { item ->
                        Text(
                            text = "${item.verse} ${item.text}", modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                            , color = Color.Black
                        )
                    }
                }
            }
            is BibleUIState.Failure -> {
                Text((state as BibleUIState.Failure).message, color = Color.Red)
            }
        }
    }

}

@Composable
fun secondScreen(){
    Button(onClick = {

    }) {
        Text("Second screen")
    }
}

@Composable
fun thirdScreen(){
Button(onClick = {

}) {
    Text("Third scren")
}
}