package com.singularity.ipcaplus.calendar

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.singularity.ipcaplus.Backend
import com.singularity.ipcaplus.R
import com.singularity.ipcaplus.Utilis
import com.singularity.ipcaplus.databinding.ActivityCalendarBinding
import com.singularity.ipcaplus.databinding.ActivityChatBinding
import com.singularity.ipcaplus.databinding.ActivityMainBinding
import com.singularity.ipcaplus.models.EventCalendar
import java.time.Year
import java.util.*

class CalendarActivity : AppCompatActivity() {

    var events = arrayListOf<EventCalendar>()
    private var eventAdapter: RecyclerView.Adapter<*>? = null
    private var eventLayoutManager: LinearLayoutManager? = null
    private lateinit var binding: ActivityCalendarBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Create the layout for this fragment
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Action Bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        supportActionBar?.title = "Calendario"

        // Set Current Date
        binding.monthTitle.text = Utilis.getMonthById(Utilis.getCurrentMonthId())
        binding.yearTitle.text = Utilis.getCurrentYear()
        binding.compactcalendarView.setCurrentDate(Date())

        // Get This Month Events
        addAllMonthEvents(binding.monthTitle.text.toString())

        // Add Event Button
        binding.fabAddEvent.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        // Calendar Interactions Events
        binding.compactcalendarView.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {

                // Show All Selected day Events
                addAllDayEvents(binding.monthTitle.text.toString(), dateClicked.date)

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                binding.monthTitle.text = Utilis.getMonthById(firstDayOfNewMonth.month+1)
                binding.yearTitle.text = Utilis.getYearByCalendarId(firstDayOfNewMonth.year).toString()

                // Refresh with new Month Events
                addAllMonthEvents(binding.monthTitle.text.toString())
            }
        })

        // Event List
        eventLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewEvents.layoutManager = eventLayoutManager
        eventAdapter = EventAdapter()
        binding.recycleViewEvents.itemAnimator = DefaultItemAnimator()
        binding.recycleViewEvents.adapter = eventAdapter
    }

    // When the support action bar back button is pressed, the app will go back to the previous activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun addAllMonthEvents(month: String) {
        Backend.getAllMonthEvents (month) { allEvents ->
            events.clear()
            events.addAll(allEvents)
            eventAdapter?.notifyDataSetChanged()

            // Add Icons into the calendar
            // Bugado <----------- vvvvvvvvvvv
            for (event in events) {
                val ev = Event(0,event.datetime.seconds * 1000, event.desc)
                binding.compactcalendarView.addEvent(ev)
            }
        }
    }


    fun addAllDayEvents(month: String, day: Int) {
        Backend.getAllMonthDayEvents (month, day) { allEvents ->
            events.clear()
            events.addAll(allEvents)
            eventAdapter?.notifyDataSetChanged()
        }
    }


    inner class EventAdapter : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

        inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_event, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.v.apply {

                // Get data
                val textViewDay = findViewById<TextView>(R.id.day_textview)
                val textViewName = findViewById<TextView>(R.id.name_textview)
                val textViewDesc = findViewById<TextView>(R.id.desc_textview)
                val textViewHour = findViewById<TextView>(R.id.hour_textview)

                // Set data
                val date = Utilis.getDate(events[position].datetime.seconds * 1000, "yyyy-MM-dd'T'HH:mm:ss.SSS")
                textViewDay.text = Utilis.getDay(date)
                textViewName.text = events[position].name
                textViewDesc.text = events[position].desc
                textViewHour.text = Utilis.getHours(date) + ":" + Utilis.getMinutes(date)

            }
        }

        override fun getItemCount(): Int {
            return events.size
        }
    }
}