package com.binbean.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.binbean.main.databinding.ActivityAdminMainBinding
import com.binbean.navigation.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminMainBinding
    private val INDICATOR_WIDTH_RATIO = 0.7
    private val viewModel: AdminMainViewModel by viewModels()

    // 네비게이션을 숨기는 프래그먼트
    private val hideNavDestinations = setOf(
        R.id.register_basic,
        R.id.register_hours,
        R.id.register_drawing,
        R.id.my_page_password
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    /**
     * 네비게이션 초기화
     */
    private fun initNavigation() {
        val navController =
            binding.fragmentContainer.getFragment<NavHostFragment>().navController
        binding.bottomNavigationView.setupWithNavController(navController)
        val nav = binding.bottomNavigationView
        val itemCount = nav.menu.size

        initIndicatorPosition(nav, itemCount)
        setUpNaviagtion(nav, itemCount, navController)
        hideBottomNavigation(navController)
    }

    /**
     * 네비게이션 작동 함수
     */
    private fun setUpNaviagtion(
        nav: BottomNavigationView,
        itemCount: Int,
        navController: NavController
    ) {
        nav.setOnItemSelectedListener {
            // 1. Indicator 이동
            val selectedIndex = getMenuItemIndex(nav.menu, it)
            val (itemWidth, indicatorWidth) = getItemAndIndicatorWidth(nav, itemCount)
            moveIndicatorTo(selectedIndex, itemWidth, indicatorWidth)

            // 2. 네비게이션 이동 (백스택 비우기)
            navController.popBackStack(navController.graph.startDestinationId, false)

            when (it.itemId) {
                R.id.navi_registration -> {
                    val id = viewModel.getCafeId()
                    val target = if (id != -1) R.id.navi_modification else R.id.navi_registration
                    navController.navigate(target)
                }

                else -> {
                    navController.navigate(it.itemId)
                }
            }
            true
        }
    }

    /**
     * 인디케이터 위치를 초기화 하는 함수
     */
    private fun initIndicatorPosition(nav: BottomNavigationView, itemCount: Int) {
        nav.post {
            val (itemWidth, indicatorWidth) = getItemAndIndicatorWidth(nav, itemCount)
            val selectedIndex = getMenuItemIndexById(nav.menu, nav.selectedItemId).coerceAtLeast(0)

            val indicatorParams = binding.indicatorBar.layoutParams
            indicatorParams.width = indicatorWidth
            binding.indicatorBar.layoutParams = indicatorParams

            moveIndicatorTo(selectedIndex, itemWidth, indicatorWidth)
        }
    }

    /**
     * 바텀 네비게이션을 숨기는 함수
     */
    private fun hideBottomNavigation(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.visibility =
                if (destination.id in hideNavDestinations) View.GONE else View.VISIBLE
        }
    }

    /**
     * 메뉴 아이템으로 인덱스를 찾는 함수
     */
    private fun getMenuItemIndex(menu: Menu, menuItem: MenuItem): Int {
        for (i in 0 until menu.size) {
            if (menu[i].itemId == menuItem.itemId) return i
        }
        return -1
    }

    /**
     * 메뉴 아이템의 아이디로 인덱스를 찾는 함수
     */
    private fun getMenuItemIndexById(menu: Menu, itemId: Int): Int {
        for (i in 0 until menu.size) {
            if (menu[i].itemId == itemId) return i
        }
        return -1
    }

    /**
     * 아이템의 크기로 인디케이터의 크기를 계산하는 함수
     */
    private fun getItemAndIndicatorWidth(
        nav: BottomNavigationView,
        itemCount: Int
    ): Pair<Int, Int> {
        val itemWidth = nav.width / itemCount
        val indicatorWidth = (itemWidth * INDICATOR_WIDTH_RATIO).toInt()
        return Pair(itemWidth, indicatorWidth)
    }

    /**
     * 인디케이터를 이동시키는 함수
     */
    private fun moveIndicatorTo(selectedIndex: Int, itemWidth: Int, indicatorWidth: Int) {
        // 한 아이템의 시작 위치 + (한 칸의 (1-0.7)/2) 만큼 이동 = 가운데 정렬
        val leftMargin = selectedIndex * itemWidth + ((itemWidth - indicatorWidth) / 2)
        binding.indicatorBar.animate()
            .translationX(leftMargin.toFloat())
            .setDuration(300)
            .start()
    }

}