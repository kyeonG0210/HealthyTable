package com.example.healthytable

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.healthytable.ui.food.FoodListFragment
import com.example.healthytable.ui.record.RecordFragment
import com.example.healthytable.ui.settings.SettingsFragment
import com.example.healthytable.ui.community.CommunityFragment
import com.example.healthytable.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 툴바 설정
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // 햄버거 버튼 설정
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 드로어 헤더에 사용자 이름 표시
        val headerView = navigationView.getHeaderView(0)
        val userNameText = headerView.findViewById<TextView>(R.id.userNameText)
        val prefs = getSharedPreferences("UserInfo", MODE_PRIVATE)
        val name = prefs.getString("name", "사용자")
        userNameText.text = "$name 님"

        // 초기 화면
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, HomeFragment())
//            .commit()
        // 초기 화면 로딩 조건
        if (intent.getBooleanExtra("navigateToCommunity", false)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunityFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }


        // BottomNavigation 처리
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_list -> FoodListFragment()
                R.id.nav_record -> RecordFragment()
                R.id.nav_community -> CommunityFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }

        // Drawer 메뉴 클릭 처리
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // 로그아웃 항목은 별도 처리
            if (menuItem.itemId == R.id.nav_logout) {
                val prefs = getSharedPreferences("UserInfo", MODE_PRIVATE)
                prefs.edit().putBoolean("isLoggedIn", false).apply()

                Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            } else {
                // 나머지 항목은 프래그먼트로 전환
                val fragment = when (menuItem.itemId) {
                    R.id.nav_home -> HomeFragment()
                    R.id.nav_list -> FoodListFragment()
                    R.id.nav_record -> RecordFragment()
                    R.id.nav_community -> CommunityFragment()
                    R.id.nav_settings -> SettingsFragment()
                    else -> HomeFragment()
                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
        if (intent.getBooleanExtra("navigateToCommunity", false)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommunityFragment())
                .commit()
        }
    }

    // 드로어 사용자 이름 갱신 함수
    fun updateUserNameInDrawer() {
        val headerView = navigationView.getHeaderView(0)
        val userNameText = headerView.findViewById<TextView>(R.id.userNameText)
        val prefs = getSharedPreferences("UserInfo", MODE_PRIVATE)
        val name = prefs.getString("name", "사용자")
        userNameText.text = "$name 님"
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}