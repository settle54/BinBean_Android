package com.binbean.container

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.binbean.bookmark.BookMarkFragment
import com.binbean.container.databinding.ActivityUserMainBinding
import com.binbean.map.CafeDrawingFragment
import com.binbean.map.MapFragment
import com.binbean.mypage.MyInfoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigationView()
        setIndicatorView()

        // 앱 초기 실행 시 홈 화면 설정
        if (savedInstanceState == null) { binding.bottomNavigationView.selectedItemId = R.id.fragment_home }
    }

    private fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val index = when (item.itemId) {
                R.id.fragment_home -> {
                    switchToFragment(MapFragment())
                    0
                }
                R.id.fragment_favorite -> {
                    switchToFragment(BookMarkFragment())
                    1
                }
                R.id.fragment_mypage -> {
                    switchToFragment(MyInfoFragment())
                    2
                }
                else -> return@setOnItemSelectedListener false
            }

            moveIndicatorToPosition(index)
            true
        }
    }

    fun hideNavigation() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    fun showNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun switchToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setIndicatorView() {
        binding.bottomNavigationView.post {
            val itemCount = binding.bottomNavigationView.menu.size
            val itemWidth = binding.bottomNavigationView.width / itemCount

            val params = binding.indicatorView.layoutParams
            params.width = itemWidth - dpToPx(50)
            binding.indicatorView.layoutParams = params
        }
    }

    private fun moveIndicatorToPosition(index: Int) {
        val itemCount = binding.bottomNavigationView.menu.size
        val itemWidth = binding.bottomNavigationView.width / itemCount
        val targetX = index * itemWidth + dpToPx(25)

        binding.indicatorView.animate()
            .x(targetX.toFloat())
            .setDuration(200)
            .start()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}