package com.application.bibileapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.application.bibileapp.ui.viewmodel.BibleUIState
import com.application.bibileapp.ui.viewmodel.BibleViewModel
import com.application.bibileapp.utils.theme.GradientEnd
import com.application.bibileapp.utils.theme.GradientStart

@Composable
fun BibleScreen(
    bibleViewModel: BibleViewModel,
    navController: NavHostController
) {
    val state by bibleViewModel.state.collectAsState()
    var query by remember { mutableStateOf("john 3:16") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Bible Search",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Enter verse eg: John 3:16") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GradientStart,
                            unfocusedBorderColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (query.isEmpty()) {
                                Toast.makeText(context, "Enter verse or chapter", Toast.LENGTH_SHORT).show()
                            } else {
                                bibleViewModel.fetchVerses(query)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Search", color = Color.White)
                    }
                }
            }

            when (state) {
                is BibleUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is BibleUIState.Success -> {
                    val response = (state as BibleUIState.Success).apiResponse
                    val items = response?.verses ?: emptyList()

                    Text(
                        "Reference: ${response?.reference}",
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(items) { item ->
                            Card(
                                onClick = {
                                    response?.reference?.let { ref ->
                                        navController.navigate("detail/$ref")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))
                            ) {
                                Text(
                                    text = "${item.verse} ${item.text}",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                is BibleUIState.Failure -> {
                    Text(
                        (state as BibleUIState.Failure).message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
