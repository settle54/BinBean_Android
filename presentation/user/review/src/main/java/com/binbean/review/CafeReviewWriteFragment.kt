package com.binbean.review

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.arieum.review.R
import com.arieum.review.databinding.FragmentCafeReviewWriteBinding
import com.binbean.domain.cafe.Cafe
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.ReviewImage
import com.binbean.domain.cafe.ReviewPostRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CafeReviewWriteFragment : DialogFragment() {

    private var _binding: FragmentCafeReviewWriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CafeReviewWriteViewModel by viewModels()

    private lateinit var starViews: List<AppCompatButton>
    private var rating: Int = 0
    private var cafe: Cafe? = null
    private var cafeDetail: CafeDetail? = null

    private val photoList = mutableListOf<Uri>()
    private lateinit var adapter: PhotoAdapter

    // 갤러리를 여는 함수
    private val getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                photoList.add(it)
                adapter.notifyItemInserted(photoList.size - 1)
                updatePhotoRcvVisibility()
            }
        }

    companion object {
        fun newInstance(cafe: Cafe, rating: Int): CafeReviewWriteFragment {
            return CafeReviewWriteFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("cafe", cafe)
                    putInt("rating", rating)
                }
            }
        }

        fun newInstance(detail: CafeDetail, rating: Int): CafeReviewWriteFragment {
            return CafeReviewWriteFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("cafeDetail", detail)
                    putInt("rating", rating)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)

        arguments?.let {
            rating = it.getInt("rating")
            cafe = it.getSerializable("cafe") as? Cafe
            cafeDetail = it.getSerializable("cafeDetail") as? CafeDetail
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafeReviewWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        starViews = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        setInitStarUI(rating)

        binding.tvCafeName.text = cafeDetail?.cafeName.orEmpty() ?: cafe?.name.orEmpty()
        binding.tvAddress.text = cafeDetail?.cafeAddress.orEmpty() ?: cafe?.address.orEmpty()

        binding.btnClose.setOnClickListener { dismiss() }

        initClickListener()
        initPhotoRcv()
        observeViewModel()
    }

    private fun setInitStarUI(rating: Int) {
        for (i in starViews.indices) {
            starViews[i].setBackgroundResource(
                if (i < rating) R.drawable.ic_filled_star else R.drawable.ic_empty_star
            )
        }
    }

    private fun updateStarRating(rating: Int) {
        for (i in starViews.indices) {
            starViews[i].setBackgroundResource(
                if (i < rating) R.drawable.ic_filled_star else R.drawable.ic_empty_star
            )
        }
    }

    /**
     * 클릭 리스너 초기화 함수
     */
    private fun initClickListener() {
        binding.uploadPhotoWindow.setOnClickListener {
            getImage.launch("image/*")
        }

        starViews.forEachIndexed { index, starView ->
            starView.setOnClickListener {
                val selectedRating = index + 1
                updateStarRating(selectedRating)
            }
        }

        binding.btnSubmitReview.setOnClickListener {
            val reviewText = binding.etReview.text.toString()
            val reviewScore = rating.toDouble()

            val imageList = photoList.map { uri ->
                ReviewImage(url = uri.toString())
            }

            val reviewRequest = ReviewPostRequest(
                reviewText = reviewText,
                reviewScore = reviewScore,
                reviewImgUrlList = imageList
            )

            val cafeId = cafeDetail?.id ?: cafe?.id
            if (cafeId != null) {
                viewModel.postReview(cafeId, reviewRequest)
            } else {
                Toast.makeText(requireContext(), "카페 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * 사진 리사이클러 뷰 초기화 함수
     */
    private fun initPhotoRcv() {
        initPhotoRcvAdapter()
        val recyclerView = binding.photoRcv
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        updatePhotoRcvVisibility()
    }

    /**
     * 사진 리사이클러 뷰 어댑터 설정 함수
     */
    private fun initPhotoRcvAdapter() {
        adapter = PhotoAdapter(
            photoList,
            onClick = { getImage.launch("image/*") },
            onDelete = {
                photoList.removeAt(it)
                adapter.notifyItemRemoved(it)
                updatePhotoRcvVisibility()
            }
        )
    }

    /**
     * 사진 리사이클러뷰 가시성 제어 함수
     */
    private fun updatePhotoRcvVisibility() {
        binding.photoRcv.visibility = if (photoList.isEmpty()) View.GONE else View.VISIBLE
        binding.uploadPhotoWindow.visibility = if (photoList.isEmpty()) View.VISIBLE else View.GONE
    }

    /**
     * 뷰모델 관찰 함수
     */
    private fun observeViewModel() {
        viewModel.reviewPostState.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "리뷰가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                dismiss()
            }.onFailure {
                Toast.makeText(requireContext(), "리뷰 등록에 실패했습니다: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}