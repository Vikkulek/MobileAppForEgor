package com.example.yp

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.yp.data.network.ApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class GroupActivity : AppCompatActivity() {
    private lateinit var selectedDate: Calendar

    private lateinit var tableLayout: TableLayout
    private lateinit var textViewDate: TextView
    private lateinit var buttonSelectDate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.table_scrollview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tableLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedDate = Calendar.getInstance()

        tableLayout = findViewById(R.id.tableLayout)
        textViewDate = findViewById(R.id.textViewDate)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)

        val startData = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        textViewDate.text = dateFormat.format(startData.time)

        buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        lifecycleScope.launch {
            loadData()
        }

        Log.d("MyTag", "started")
    }

    private fun showDatePickerDialog() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                textViewDate.text = dateFormat.format(selectedDate.time)
                lifecycleScope.launch {
                    loadData()
                }
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private suspend fun loadData() {
        clearTable()

        val okHttpClient = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .sslSocketFactory(createUnsafeSslSocketFactory(), createUnsafeTrustManager())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.197.218:7009/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        try {
            val teachers = apiService.getTeachers()
            val paras = apiService.getParas()
            val classrooms = apiService.getClassrooms()
            val typeRooms = apiService.getTypeRooms()
            val subjects = apiService.getSubjects()
            val groups = apiService.getGroups()
            val days = apiService.getDays()

            for (group in groups) {
                val tableRow = TableRow(this@GroupActivity)

                val groupName = TextView(this@GroupActivity).apply {
                    text = group.name
                    setPadding(8, 8, 8, 8)
                }

                tableRow.addView(groupName)

                val currentGroupLessons = paras.filter { it.groupId == group.id }
                for (currentLesson in currentGroupLessons) {
                    var strToAdd = ""

                    if (currentLesson != null) {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val selectedDateString = textViewDate.text.toString()

                        val day = days.find { it.date == currentLesson.dayDate }
                        val lessonDateString = day?.date

                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = inputFormat.parse(lessonDateString)
                        val formattedlessonDateString = dateFormat.format(date)

                        if (selectedDateString != formattedlessonDateString) {
                            val lessonDetails = TextView(this@GroupActivity).apply {
                                text = ""
                                setPadding(8, 8, 8, 8)
                            }
                            tableRow.addView(lessonDetails)

                            Log.d("MyTag", "$selectedDateString $lessonDateString")
                            continue;
                        }

                        val classroom = classrooms.find { it.id == currentLesson.classroomId }
                        val typeRoom = classroom?.let { typeRooms.find { it.id == classroom.typeRoomId } }
                        val subject = subjects.find { it.id == currentLesson.subjectId }
                        val teacher = teachers.find { it.id == currentLesson.teacherId }

                        strToAdd = "${subject?.name}\n" +
                                    "Кабинет: ${classroom?.typeRoomId}, ${typeRoom?.name}\n" +
                                    "${teacher?.surname} ${teacher?.name} ${teacher?.patronymic}"
                    }

                    val lessonDetails = TextView(this@GroupActivity).apply {
                        text = strToAdd
                        setPadding(8, 8, 8, 8)
                    }

                    tableRow.addView(lessonDetails)
                }

                tableLayout.addView(tableRow)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MyTag", e.message.toString())
        }
    }

    private fun clearTable() {
        tableLayout.removeAllViews()
        addHeaders()
    }

    private fun addHeaders() {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )

        val textView1 = TextView(this)
        textView1.text = "Группы"
        textView1.setPadding(8, 8, 8, 8)
        textView1.textSize = 16f
        textView1.gravity = Gravity.CENTER
        textView1.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView1)

        val textView2 = TextView(this)
        textView2.text = "1 пара"
        textView2.setPadding(8, 8, 8, 8)
        textView2.textSize = 16f
        textView2.gravity = Gravity.CENTER
        textView2.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView2)

        val textView3 = TextView(this)
        textView3.text = "2 пара"
        textView3.setPadding(8, 8, 8, 8)
        textView3.textSize = 16f
        textView3.gravity = Gravity.CENTER
        textView3.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView3)

        val textView4 = TextView(this)
        textView4.text = "3 пара"
        textView4.setPadding(8, 8, 8, 8)
        textView4.textSize = 16f
        textView4.gravity = Gravity.CENTER
        textView4.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView4)

        val textView5 = TextView(this)
        textView5.text = "4 пара"
        textView5.setPadding(8, 8, 8, 8)
        textView5.textSize = 16f
        textView5.gravity = Gravity.CENTER
        textView5.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView5)

        val textView6 = TextView(this)
        textView6.text = "5 пара"
        textView6.setPadding(8, 8, 8, 8)
        textView6.textSize = 16f
        textView6.gravity = Gravity.CENTER
        textView6.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView6)

        val textView7 = TextView(this)
        textView7.text = "6 пара"
        textView7.setPadding(8, 8, 8, 8)
        textView7.textSize = 16f
        textView7.gravity = Gravity.CENTER
        textView7.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView7)

        val textView8 = TextView(this)
        textView8.text = "7 пара"
        textView8.setPadding(8, 8, 8, 8)
        textView8.textSize = 16f
        textView8.gravity = Gravity.CENTER
        textView8.setTypeface(null, Typeface.BOLD)
        tableRow.addView(textView8)

        tableLayout.addView(tableRow)
    }

    fun createUnsafeSslSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(createUnsafeTrustManager()), null)
        return sslContext.socketFactory
    }

    fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }
}