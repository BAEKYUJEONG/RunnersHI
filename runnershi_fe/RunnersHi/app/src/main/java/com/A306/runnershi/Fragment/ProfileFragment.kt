package com.A306.runnershi.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.history.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var imgRes = intArrayOf()
    var place = arrayOf(
            "노원구" , "군포", "대야동", "송파구", "강남구"
    )
    var time = intArrayOf(
            55, 48, 23, 12, 37
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

//        val adapter1 = RecyclerAdapter()
//        profile_history.adapter = adapter1
//
//        //profile_history.layoutManager = LinearLayoutManager(this)
//        profile_history.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // RecyclerView의 Adapter 클래스
    inner class RecyclerAdapter :RecyclerView.Adapter<RecyclerAdapter.ViewHolderClass>(){

        // 항목 구성을 위해 사용할 ViewHolder 객체가 필요할 때 호출되는 메서드
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            // 항목으로 사용할 View 객체를 생성한다.
            val itemView = layoutInflater.inflate(R.layout.history, null)
            val holder = ViewHolderClass(itemView)

            return holder
        }

        // ViewHolder를 통해 항목을 구성할 때 항목 내의 View 객체에 데이터를 셋팅한다.
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.placeTextView.text = place[position]
            holder.timeNumber.setSelection(time[position])
        }

        // RecyclerView의 항목 개수를 반환한다.
        override fun getItemCount(): Int {
            return place.size
        }

        // ViewHolder 클래스
        inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView){
            // 항목 View 내부의 View 객체의 주소값을 담는다.
            val placeTextView = itemView.placeTextView
            val timeNumber = itemView.timeNumber
        }
    }

    public fun onClick() {

    }
}