package cc.imorning.chat.activity


import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cc.imorning.chat.R
import cc.imorning.chat.databinding.ActivityMainBinding
import cc.imorning.chat.service.NetworkService

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkService: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_message, R.id.navigation_contact, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        networkService = Intent(this, NetworkService::class.java)
        startService(networkService)
    }

    override fun onDestroy() {
        stopService(networkService)
        super.onDestroy()
    }
}