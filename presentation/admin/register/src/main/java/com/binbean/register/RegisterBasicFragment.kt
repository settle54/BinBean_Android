package com.binbean.register

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.binbean.admin.dto.CafeRegisterRequest
import com.binbean.admin.model.PhotoItem
import com.binbean.domain.cafe.CafeDetail
import com.binbean.register.databinding.FragmentRegisterBasicBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterBasicFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBasicBinding
    private lateinit var adapter: PhotoAdapter
    private val registerViewModel: CafeRegisterViewModel by activityViewModels()
    private val modifyViewModel: CafeModifyViewModel by activityViewModels()
    private lateinit var baseRequest: CafeRegisterRequest

    // 갤러리를 여는 함수
    private val getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val currentPhotos = registerViewModel.imageItems.value.orEmpty().toMutableList()
                currentPhotos.add(PhotoItem.Local(it))
                Log.d(TAG, currentPhotos.joinToString(","))
                registerViewModel.setImageList(currentPhotos)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBasicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: RegisterBasicFragmentArgs by navArgs()
        val isRegistered = args.isRegistered

        if (isRegistered) {
            binding.title.setText(R.string.modify_cafe_basic_info)
            binding.modifiyTool.visibility = View.VISIBLE
            binding.registerButton.visibility = View.GONE
            setCafeDetailInfo()
        }

        radioButtonControl()
        initPhotoRcv()
        initClickListener()
        observeCafeInfoImg()
    }

    /**
     * 읽어 들여온 카페 상세 정보를 뷰에 바인드하는 함수
     */
    private fun setCafeDetailInfo() {
        val cafe = modifyViewModel.getCafeDetail()
        cafe?.let {
            binding.tradeWindow.setText(cafe.cafeName)
            binding.addressWindow.setText(cafe.cafeAddress)
            binding.phoneWindow.setText(cafe.cafePhone)
            binding.editAddiInfo.setText(cafe.cafeDescription)
            binding.radioWifi.checkRadioByTag(cafe.wifiAvailable)
            binding.radioCharging.checkRadioByTag(cafe.chargerAvailable)
            binding.radioKids.checkRadioByTag(cafe.kidsAvailable)
            binding.radioPets.checkRadioByTag(cafe.petAvailable)
            setCafeInfoImg(it)
        }
    }

    private fun setCafeInfoImg(cafe: CafeDetail) {
        val remotePhotos = cafe.cafeImgUrl.map { PhotoItem.Remote(it.url) }
        registerViewModel.setImageList(remotePhotos)
    }

    /**
     * cafe 값에 따라 라디오버튼 체크하는 함수
     */
    private fun RadioGroup.checkRadioByTag(value: Int?) {
        if (value == null) {
            clearCheck()
            return
        }

        for (i in 0 until 2) {
            val btn = getChildAt(i) as? RadioButton ?: continue
            if (btn.tag?.toString()?.toIntOrNull() == value) {
                check(btn.id)
                return
            }
        }

        clearCheck()
    }

    /**
     * 기본 정보 전달 힘수
     */
    private fun makeBasicRequest() {
        baseRequest = CafeRegisterRequest(
            cafeName = binding.tradeWindow.text.toString(),
            cafeAddress = binding.addressWindow.text.toString(),
            latitude = 37.870618,
            longitude = 127.744452,
            cafePhone = binding.phoneWindow.text.toString(),
            wifiAvailable = binding.radioWifi.getSelectedTagValueAsInt(),
            chargerAvailable = binding.radioCharging.getSelectedTagValueAsInt(),
            kidsAvailable = binding.radioKids.getSelectedTagValueAsInt(),
            petAvailable = binding.radioPets.getSelectedTagValueAsInt(),
            cafeDescription = binding.editAddiInfo.text.toString()
        )
    }

    /**
     * 라디오 그륩의 선택된 값의 태그를 읽어오는 함수 (불가능: 0, 가능: 1)
     */
    private fun RadioGroup.getSelectedTagValueAsInt(): Int {
        return findViewById<RadioButton>(checkedRadioButtonId).tag.toString().toInt()
    }

    /**
     * 클릭 리스너 초기화 함수
     */
    private fun initClickListener() {
        binding.uploadPhotoWindow.setOnClickListener {
            getImage.launch("image/*")
        }

        binding.registerButton.setOnClickListener {
            makeBasicRequest()
            Log.d(TAG, baseRequest.toString())
            registerViewModel.setRequest(baseRequest)
            val action = RegisterBasicFragmentDirections.actionRegistrationToHours(false)
            findNavController().navigate(action)
        }

        binding.modifyComplete.setOnClickListener {
            val action = RegisterBasicFragmentDirections.actionBasicToModification()
            findNavController().navigate(action)
        }

        binding.modifyHours.setOnClickListener {
            val action = RegisterBasicFragmentDirections.actionRegistrationToHours(true)
            findNavController().navigate(action)
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
    }

    /**
     * 사진 리사이클러 뷰 어댑터 설정 함수
     */
    private fun initPhotoRcvAdapter() {
        adapter = PhotoAdapter(
            onClick = { getImage.launch("image/*") },
            onDelete = {
                val updatedList = registerViewModel.imageItems.value.orEmpty().toMutableList().apply {
                    removeAt(it)
                }
                registerViewModel.setImageList(updatedList)
            }
        )
    }

    private fun observeCafeInfoImg() {
        registerViewModel.imageItems.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            updatePhotoRcvVisibility(list)
        }
    }

    /**
     * 사진 리사이클러뷰 가시성 제어 함수
     */
    private fun updatePhotoRcvVisibility(photos: List<PhotoItem>) {
        binding.photoRcv.visibility = if (photos.isEmpty()) View.GONE else View.VISIBLE
        binding.uploadPhotoWindow.visibility = if (photos.isEmpty()) View.VISIBLE else View.GONE
    }

    /**
     * 라디오 그룹 제어 함수
     */
    private fun radioButtonControl() {
        setupRadioGroupColors(
            binding.radioWifi, binding.wifiOn.id, binding.iconWifi, binding.textWifi
        )
        setupRadioGroupColors(
            binding.radioCharging, binding.chargingOn.id, binding.iconCharging, binding.textCharging
        )
        setupRadioGroupColors(
            binding.radioKids, binding.kidsOn.id, binding.iconKids, binding.textKids
        )
        setupRadioGroupColors(
            binding.radioPets, binding.petsOn.id, binding.iconPets, binding.textPets
        )
    }

    /**
     * 라디오 그룹 색상 변경 함수
     */
    private fun setupRadioGroupColors(
        radioGroup: RadioGroup,
        activateButtonId: Int,
        iconView: ImageView,
        textView: TextView
    ) {
        val activateIconColor =
            ContextCompat.getColor(requireContext(), com.binbean.resource.R.color.main_green)
        val activateTextColor =
            ContextCompat.getColor(requireContext(), com.binbean.resource.R.color.sub2)
        val deactivatedColor =
            ContextCompat.getColor(requireContext(), com.binbean.resource.R.color.gs_sub)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val isActivated = checkedId == activateButtonId

            val iconColor = if (isActivated) activateIconColor else deactivatedColor
            val textColor = if (isActivated) activateTextColor else deactivatedColor

            iconView.setColorFilter(iconColor)
            textView.setTextColor(textColor)
        }
    }

    companion object {
        private const val TAG = "RegisterBasicFragment"
    }
}