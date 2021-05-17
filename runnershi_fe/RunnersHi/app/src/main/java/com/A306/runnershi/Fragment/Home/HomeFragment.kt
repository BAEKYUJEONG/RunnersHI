package com.A306.runnershi.Fragment.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventClick()
    }

    private fun eventClick(){

        val mainActivity = activity as MainActivity
        val eventFragment = EventFragment()

        eventCardView.setOnClickListener{
            mainActivity.makeCurrentFragment(eventFragment)
        }

        eventImageView.setOnClickListener{
            mainActivity.makeCurrentFragment(eventFragment)
        }

        eventTextView.setOnClickListener{
            mainActivity.makeCurrentFragment(eventFragment)
        }
    }
}