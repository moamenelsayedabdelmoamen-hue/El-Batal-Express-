package com.example

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage
import com.example.ui.theme.Primary
import com.example.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email_hint)) })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(R.string.password_hint)) })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginSuccess, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text(stringResource(R.string.login_button))
        }
        TextButton(onClick = onRegisterClick) {
            Text(stringResource(R.string.register_link))
        }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Picker
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .border(2.dp, Primary, CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Logo",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.restaurant_name_hint)) })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email_hint)) })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.phone_hint)) })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(R.string.password_hint)) })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRegisterSuccess, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text(stringResource(R.string.register_button))
        }
    }
}
data class Plan(val id: Int, val captains: Int, val price: Int, val name: String, val subtitle: String, val isPopular: Boolean = false)

@Composable
fun RegistrationSuccessScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.registration_success_message), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SubscriptionPlansScreen() {
    val plans = listOf(
        Plan(1, 2, 400, "2 كابتن", "الباقة الأساسية"),
        Plan(2, 4, 600, "4 كابتن", "الباقة المتوسطة"),
        Plan(3, 6, 800, "6 كابتن", "الباقة المميزة", true),
        Plan(4, 8, 1000, "8 كابتن", "الباقة المتقدمة")
    )
    var selectedPlanId by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("card") }
    var phoneNumber by remember { mutableStateOf("") }
    var fallbackUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    if (fallbackUrl != null) {
        AlertDialog(
            onDismissRequest = { fallbackUrl = null },
            modifier = Modifier.fillMaxSize(0.9f),
            text = {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            webViewClient = WebViewClient()
                            loadUrl(fallbackUrl!!)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            },
            confirmButton = {
                Button(onClick = { fallbackUrl = null }) { Text("إغلاق") }
            }
        )
    }

    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("اختر طريقة الدفع") },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paymentMethod == "card", onClick = { paymentMethod = "card" })
                        Text("بطاقة ائتمان")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paymentMethod == "wallet", onClick = { paymentMethod = "wallet" })
                        Text("محفظة إلكترونية")
                    }
                    if (paymentMethod == "wallet") {
                        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("رقم الهاتف") })
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showPaymentDialog = false
                    isLoading = true
                    val plan = plans.find { it.id == selectedPlanId }
                    if (plan != null) {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val request = PaymentRequest(
                                    amount = plan.price,
                                    email = "user@email.com",
                                    first_name = "Moamen",
                                    last_name = "Test",
                                    payment_method = paymentMethod,
                                    phone_number = if (paymentMethod == "wallet") phoneNumber else null
                                )
                                val response = RetrofitClient.instance.createPayment(request)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(response.url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.util.Log.e("PaymentError", "External browser failed, using fallback", e)
                                        fallbackUrl = response.url
                                    }
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("PaymentError", "Detailed error creating payment", e)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    android.widget.Toast.makeText(
                                        context,
                                        "خطأ: ${e.message ?: "غير معروف"}",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }) {
                    Text("متابعة")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.subscription_title), color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(plans) { plan ->
                    PlanCard(
                        plan = plan,
                        isSelected = selectedPlanId == plan.id,
                        onSelect = { selectedPlanId = plan.id },
                        onSubscribe = {
                            selectedPlanId = plan.id
                            showPaymentDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanCard(plan: Plan, isSelected: Boolean, onSelect: () -> Unit, onSubscribe: () -> Unit) {
    val borderColor = if (isSelected) Primary else Color.DarkGray
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val backgroundColor = Color(0xFF1E1E1E)

    Card(
        modifier = Modifier.fillMaxWidth().border(borderWidth, borderColor, RoundedCornerShape(16.dp)).clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (plan.isPopular) {
                Text(stringResource(R.string.most_popular), color = Primary, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Icon
            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(plan.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(plan.subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(stringResource(R.string.plan_price, plan.price), color = Color.Yellow, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            HorizontalDivider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Features
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.feature_fast_delivery), color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.feature_tech_support), color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(onClick = onSubscribe, shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Text(stringResource(R.string.subscribe_now), color = Color.Black)
            }
        }
    }
}


@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        // Summary Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoCard(stringResource(R.string.new_orders_label), "5")
            InfoCard(stringResource(R.string.in_progress_label), "2")
            InfoCard(stringResource(R.string.completed_label), "10")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {}) {
            Text(stringResource(R.string.create_order_button))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = {}) {
            Text(stringResource(R.string.view_orders_button))
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(modifier = Modifier.width(100.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
