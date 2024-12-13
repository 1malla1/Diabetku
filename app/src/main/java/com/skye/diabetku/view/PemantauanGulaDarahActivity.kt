package com.skye.diabetku.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.skye.diabetku.R
import com.skye.diabetku.databinding.ActivityPemantauanGulaDarahBinding
import com.skye.diabetku.adapter.BloodGlucoseAdapter
import com.skye.diabetku.data.remote.response.DataItem
import com.skye.diabetku.viewmodel.PemantauanGulaDarahViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PemantauanGulaDarahActivity : AppCompatActivity() {
    private val pemantauanGulaDarahViewModel: PemantauanGulaDarahViewModel by viewModels() {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityPemantauanGulaDarahBinding
    private lateinit var glucoseAdapter: BloodGlucoseAdapter

    private var selectedStartDate: Long? = null
    private var selectedEndDate: Long? = null
    private var selectedFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemantauanGulaDarahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setFab()
        setRecyclerView()
        setToggleButton()

        binding.tvTanggalTerpilih.text = getTodayDate()
        setDailyFilter()
        filterData()

        pemantauanGulaDarahViewModel.result.observe(this) { bloodGlucoseList ->
            bloodGlucoseList?.let {
                glucoseAdapter.submitList(it)
                filterData()
            }
        }

        pemantauanGulaDarahViewModel.getBloodGlucose()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val drawable: Drawable? = toolbar.navigationIcon
        drawable?.let {
            DrawableCompat.setTint(it, getColor(R.color.white))
            toolbar.navigationIcon = it
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun setToggleButton() {
        if (binding.toggleRentangButton.checkedButtonId == View.NO_ID) {
            binding.toggleRentangButton.check(R.id.buttonHarian)
        }
        binding.toggleRentangButton.setSelectionRequired(true)
        binding.toggleRentangButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonHarian -> {
                        binding.tvTanggalTerpilih.text = getTodayDate()
                        setDailyFilter()
                    }
                    R.id.buttonMingguan -> {
                        binding.tvTanggalTerpilih.text = getCurrentWeekRange()
                        setWeeklyFilter()
                    }
                    R.id.buttonBulanan -> {
                        binding.tvTanggalTerpilih.text = getCurrentMonthYear()
                        setMonthlyFilter()
                    }
                }
                filterData()
            } else {
                binding.toggleRentangButton.check(binding.toggleRentangButton.checkedButtonId)
            }
        }

        binding.tvTanggalTerpilih.setOnClickListener {
            when (binding.toggleRentangButton.checkedButtonId) {
                R.id.buttonHarian -> setDailyPicker()
                R.id.buttonMingguan -> setWeeklyPicker()
                R.id.buttonBulanan -> setMonthlyPicker()
            }
        }
    }
    private fun setDailyFilter() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        selectedStartDate = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        selectedEndDate = calendar.timeInMillis
    }
    private fun setWeeklyFilter() {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        selectedStartDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        selectedEndDate = calendar.timeInMillis
    }
    private fun setMonthlyFilter() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        selectedStartDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        selectedEndDate = calendar.timeInMillis

        Log.d("MonthlyFilter", "Start Date: $selectedStartDate, End Date: $selectedEndDate")
    }

    private fun setDailyPicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                val startOfDay = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val endOfDay = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 23, 59, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis

                selectedStartDate = startOfDay
                selectedEndDate = endOfDay

                val selectedDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(selectedStartDate)
                binding.tvTanggalTerpilih.text = selectedDate

                filterData()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun setWeeklyPicker() {
        val calendar = Calendar.getInstance()
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Pilih Rentang Tanggal")
            .build()

        dateRangePicker.show(supportFragmentManager, "WEEKLY_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selection.first)
            val endDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selection.second)
            binding.tvTanggalTerpilih.text = "$startDate - $endDate"
            selectedStartDate = selection.first
            selectedEndDate = selection.second
            filterData()
        }
    }
    private fun setMonthlyPicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            { _, year, month, _ ->
                val startOfMonth = Calendar.getInstance().apply {
                    set(year, month, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val endOfMonth = Calendar.getInstance().apply {
                    set(year, month, getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis

                val monthYearText = "${java.text.DateFormatSymbols().months[month]} $year"
                binding.tvTanggalTerpilih.text = monthYearText
                selectedStartDate = startOfMonth
                selectedEndDate = endOfMonth
                filterData()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.findViewById<View>(
            resources.getIdentifier("day", "id", "android")
        )?.visibility = View.GONE

        datePickerDialog.show()
    }

    private fun filterData() {
        pemantauanGulaDarahViewModel.result.observe(this) { bloodGlucoseList ->
            bloodGlucoseList?.let { list ->
                val filteredList = list.filter { dataItem ->
                    try {
                        val dataDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(dataItem.formattedDate)?.time

                        Log.d("FilterData", "Data Date: $dataDate, Start Date: $selectedStartDate, End Date: $selectedEndDate")

                        dataDate != null && dataDate >= (selectedStartDate ?: 0) && dataDate <= (selectedEndDate ?: Long.MAX_VALUE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }
                glucoseAdapter.submitList(filteredList)
                updateChart(filteredList)
            }
        }
    }
    private fun updateChart(filteredList: List<DataItem>) {
        val entries = ArrayList<Entry>()
        val xAxisLabels = ArrayList<String>()
        val groupedData = mutableMapOf<Long, MutableList<Float>>()

        val dateFormat: SimpleDateFormat
        var shouldGroupData = true

        when (binding.toggleRentangButton.checkedButtonId) {
            R.id.buttonHarian -> {
                dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                shouldGroupData = false
            }
            R.id.buttonMingguan -> {
                dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            }
            R.id.buttonBulanan -> {
                dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            }
            else -> {
                dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            }
        }

        filteredList.forEach { dataItem ->
            try {
                val dataDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(dataItem.formattedDate)?.time
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dataTime = timeFormat.parse(dataItem.formattedTime)?.time
                val glucoseLevel = dataItem.glucoseValue?.toFloat() ?: 0f

                Log.d("GlucoseChart", "Processing data: Date = ${dataItem.formattedDate}, Glucose = $glucoseLevel")

                if (dataDate != null) {
                    if (shouldGroupData) {
                        val dayStart = dataDate / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000)

                        if (groupedData[dayStart] == null) {
                            groupedData[dayStart] = mutableListOf()
                        }
                        groupedData[dayStart]?.add(glucoseLevel)
                        Log.d("GlucoseChart", "Grouped data: DayStart = $dayStart, GlucoseLevel = $glucoseLevel")
                    } else {
                        if (dataTime != null) {
                            val formattedTime = timeFormat.format(dataTime)
                            val index = (entries.size).toFloat()

                            entries.add(Entry(index, glucoseLevel))
                            xAxisLabels.add(formattedTime)
                            Log.d("GlucoseChart", "Adding entry for Harian: Time = $formattedTime, Glucose = $glucoseLevel")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("GlucoseChart", "Error processing data item: ${dataItem.formattedDate}", e)
            }
        }

        if (shouldGroupData) {
            val averageData = groupedData.map { (date, glucoseLevels) ->
                val averageGlucose = glucoseLevels.average().toFloat()
                val formattedDate = dateFormat.format(date)
                Pair(averageGlucose, formattedDate)
            }

            Log.d("GlucoseChart", "Grouped and averaged data: $averageData")

            averageData.forEachIndexed { index, (averageGlucose, formattedDate) ->
                entries.add(Entry(index.toFloat(), averageGlucose))
                xAxisLabels.add(formattedDate)
            }
        }

        val lineDataSet = LineDataSet(entries, "Glucose Level").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.RED)
            setDrawValues(true)
        }

        val lineData = LineData(lineDataSet)

        val lineChart = findViewById<LineChart>(R.id.lineChart)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.granularity = 1f

        lineChart.invalidate()
    }

    private fun setFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddGlucoseDataActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setRecyclerView() {
        glucoseAdapter = BloodGlucoseAdapter { data ->
            navigateToEditData(data.id)
        }
        binding.rvBloodGlucose.layoutManager = LinearLayoutManager(this)
        binding.rvBloodGlucose.adapter = glucoseAdapter

    }


    private fun getTodayDate(): String {
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return formatter.format(today)
    }
    private fun getCurrentWeekRange(): String {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = calendar.time
        val startDateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(startDate)

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = calendar.time
        val endDateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(endDate)

        return "$startDateFormatted - $endDateFormatted"
    }
    private fun getCurrentMonthYear(): String {
        val calendar = Calendar.getInstance()
        val month = java.text.DateFormatSymbols().months[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)

        return "$month $year"
    }

    private fun navigateToEditData(id: Int?) {
        id?.let {
            val intent = Intent(this, EditGlucoseDataActivity::class.java).apply {
                putExtra("BLOOD_GLUCOSE_ID", it)
            }
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        pemantauanGulaDarahViewModel.getBloodGlucose()
    }

}