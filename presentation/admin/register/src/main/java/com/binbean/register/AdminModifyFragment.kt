package com.binbean.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binbean.domain.cafe.Cafe
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.CafeInfoImgItem
import com.binbean.map.adapter.CafeInfoImgAdapter
import com.binbean.map.adapter.ViewPagerAdapter
import com.binbean.register.databinding.FragmentAdminModifyBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminModifyFragment : Fragment() {
    private lateinit var binding: FragmentAdminModifyBinding
    private val modifyViewModel: CafeModifyViewModel by activityViewModels()
    private lateinit var adapter: CafeInfoImgAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        observeCafeInfoImg()
        observeServerCafeData()
        setClickListeners()
    }

    /**
     * 클릭리스너 설정 함수
     */
    private fun setClickListeners() {
        binding.modifyButton.setOnClickListener {
            val action = AdminModifyFragmentDirections.actionModificationToBasic(true)
            findNavController().navigate(action)
        }
    }

    /**
     * 카페 이미지 어댑터 초기화 함수
     */
    private fun initAdapter() {
        adapter = CafeInfoImgAdapter()
        binding.rvCafeImage.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCafeImage.adapter = adapter
    }

    /**
     * 카페 이미지 변화 관찰 함수
     */
    private fun observeCafeInfoImg() {
        modifyViewModel.photoList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    /**
     * 카페 상세 데이터 변화 관찰 함수
     */
    private fun observeServerCafeData() {
        modifyViewModel.cafeDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                binding.cafeName.text = it.cafeName
                binding.tvBusinessHours.text = it.endTime + "까지"
                binding.tvStoreName.text = it.cafeName
                binding.tvAddress.text = it.cafeAddress
                binding.tvPhoneNumber.text = it.cafePhone
                binding.tvScore.text = String.format("%.1f", it.reviewAvg)

                adapter.submitList(it.cafeImgUrl.map { img -> CafeInfoImgItem(img.url) })

                setupViewPager(detail)
            }
        }
    }

    /**
     * 뷰페이저 설정 함수
     */
    private fun setupViewPager(detail: CafeDetail) {
        val adapter = ViewPagerAdapter(this, cafe = null, cafeDetail = detail)
        binding.viewPager.adapter = adapter
        attachTabs()
    }

    /**
     * 탭 레이아웃 설정 함수
     */
    private fun attachTabs() {
        binding.viewPager.isNestedScrollingEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "상세정보"
                1 -> "리뷰"
                else -> ""
            }
        }.attach()
    }

}