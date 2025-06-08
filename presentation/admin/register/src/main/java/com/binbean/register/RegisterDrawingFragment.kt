package com.binbean.register

import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.binbean.admin.dto.FloorDetail
import com.binbean.admin.dto.FloorWrapper
import com.binbean.domain.cafe.FloorPlanResponse
import com.binbean.domain.cafe.ObjectItem
import com.binbean.domain.cafe.PositionDto
import com.binbean.register.databinding.FragmentRegisterDrawingBinding
import com.binbean.ui.CanvasView
import com.binbean.register.drawing.ObjectListAdapter
import com.binbean.register.drawing.RecyclerViewDecoration
import org.json.JSONArray
import org.json.JSONObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDrawingFragment : Fragment() {
    private lateinit var binding: FragmentRegisterDrawingBinding
    private val viewModel: CafeRegisterViewModel by activityViewModels()

    private val floorViews = mutableMapOf<Int, com.binbean.ui.CanvasView>()
    private var currentFloor = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.canvasView.mode = CanvasView.Mode.ADMIN

        setupObjectRecyclerView()
        showFloor(currentFloor)
        setupObjectDragAndDrop()
        setupFloorControl()

        binding.btnSubmit.setOnClickListener {
            val floorWrappers = floorViews.keys.map { floorNumber ->
                extractFloorDataToDto(floorNumber)
            }
            viewModel.setFloorList(floorWrappers)
            viewModel.registerCafe(requireContext())
        }
    }

    private fun setupObjectRecyclerView() {
        binding.objectRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.objectRecyclerView.adapter = ObjectListAdapter().apply {
            val objectList = listOf(
                ObjectItem("chair", "의자", com.binbean.ui.R.drawable.obj_seat),
                ObjectItem("toilet", "화장실", com.binbean.ui.R.drawable.obj_toilet),
                ObjectItem("door", "문", com.binbean.ui.R.drawable.obj_door),
                ObjectItem("window", "창문", com.binbean.ui.R.drawable.obj_window),
                ObjectItem("counter", "카운터", com.binbean.ui.R.drawable.obj_casher),
                ObjectItem("table", "테이블", com.binbean.ui.R.drawable.obj_table)
            )
            submitList(objectList)
        }
        binding.objectRecyclerView.addItemDecoration(RecyclerViewDecoration(24))
    }

    private fun setupObjectDragAndDrop() {
        val dragListener = View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val type = event.clipData.getItemAt(0).text.toString()
                    addObjectView(type, event.x, event.y)
                }
            }
            true
        }
        binding.drawingContainer.setOnDragListener(dragListener)
    }

    private fun addObjectView(type: String, x: Float, y: Float) {
        val canvasView = binding.drawingContainer.getChildAt(0) as? CanvasView ?: return
        val gridX = (x - canvasView.getOffsetX()) / 50f
        val gridY = y / 50f

        val objectType = when (type) {
            "chair" -> CanvasView.ObjectType.SEAT
            "door" -> CanvasView.ObjectType.DOOR
            "toilet" -> CanvasView.ObjectType.TOILET
            "counter" -> CanvasView.ObjectType.COUNTER
            "window" -> CanvasView.ObjectType.WINDOW
            "table" -> CanvasView.ObjectType.TABLE
            else -> return
        }

        Log.d("DragDrop", "Dropped item type=$type at raw=($x, $y), grid=($gridX, $gridY)")
        Log.d("DragDrop", "CanvasView offsetX=${binding.canvasView.getOffsetX()}")

        canvasView.addInteractiveObject(gridX, gridY, objectType)
    }

    private fun showFloor(floor: Int) {
        binding.drawingContainer.removeAllViews()

        val editorView = floorViews.getOrPut(floor) { CanvasView(requireContext()) }
        editorView.mode = CanvasView.Mode.ADMIN

        binding.drawingContainer.addView(editorView, FrameLayout.LayoutParams(1000, FrameLayout.LayoutParams.MATCH_PARENT))
        binding.tvCurrentFloor.text = "${floor}층"
    }

    private fun setupFloorControl() {
        binding.btnNextFloor.setOnClickListener {
            val nextFloor = currentFloor + 1

            if (floorViews.containsKey(nextFloor)) {
                // 이미 존재하는 층 → 바로 이동
                currentFloor = nextFloor
                showFloor(currentFloor)
                Toast.makeText(requireContext(), "${currentFloor}층으로 이동했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 새로운 층 → 알림 띄우기
                AlertDialog.Builder(requireContext())
                    .setTitle("도면 추가")
                    .setMessage("${nextFloor}층 도면을 추가하시겠습니까?")
                    .setPositiveButton("추가") { _, _ ->
                        currentFloor = nextFloor
                        showFloor(currentFloor)
                        Toast.makeText(requireContext(), "${currentFloor}층 도면이 추가되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }

        binding.btnPrevFloor.setOnClickListener {
            if (currentFloor > 1) {
                currentFloor--
                showFloor(currentFloor)
                Toast.makeText(requireContext(), "${currentFloor}층으로 이동했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractFloorDataToDto(floor: Int): FloorWrapper {
        val canvasView = floorViews[floor] ?: return FloorWrapper(
            floorList = FloorDetail(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            floorNumber = floor,
            maxSeats = 0
        )

        val floorDetail = canvasView.extractObjectsForSave()

        return FloorWrapper(
            floorList = floorDetail,
            floorNumber = floor,
            maxSeats = floorDetail.seatPosition.size
        )
    }

    private fun toJsonArray(positions: List<PositionDto>): JSONArray {
        val array = JSONArray()
        for (pos in positions) {
            val obj = JSONObject().apply {
                put("x", pos.x)
                put("y", pos.y)
            }
            array.put(obj)
        }
        return array
    }
}