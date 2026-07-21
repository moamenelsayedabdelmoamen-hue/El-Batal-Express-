package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ui.theme.ElBatalExpressTheme
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
object RegisterRoute

@Serializable
object RegistrationSuccessRoute

@Serializable
object SubscriptionRoute

@Serializable
object RestaurantDetailsRoute

@Serializable
object OrdersRoute

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ElBatalExpressTheme {
        val navController = rememberNavController()
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
          ) {
            composable<LoginRoute> {
              LoginScreen(
                onLoginSuccess = { navController.navigate(HomeRoute) },
                onRegisterClick = { navController.navigate(RegisterRoute) }
              )
            }
            composable<RegisterRoute> {
              RegisterScreen(onRegisterSuccess = { navController.navigate(HomeRoute) })
            }
            composable<SubscriptionRoute> {
                SubscriptionPlansScreen()
            }
            composable<RegistrationSuccessRoute> {
              RegistrationSuccessScreen()
            }
            composable<HomeRoute> {
              HomeScreen(
                onEditRestaurant = { navController.navigate(RestaurantDetailsRoute) },
                onViewOrders = { navController.navigate(OrdersRoute) },
                onLogout = {
                  navController.navigate(LoginRoute) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                  }
                }
              )
            }
            composable<RestaurantDetailsRoute> {
              RestaurantDetailsScreen(onBack = { navController.popBackStack() })
            }
            composable<OrdersRoute> {
              OrdersScreen(onBack = { navController.popBackStack() })
            }
          }
        }
      }
    }
  }
}
