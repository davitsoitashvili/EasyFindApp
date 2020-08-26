package com.example.easyfindapp.fragments.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyfindapp.R
import com.example.easyfindapp.activities.DetailsActivity
import com.example.easyfindapp.adapters.DashBoardRecyclerAdapter
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.models.PostModel
import com.example.easyfindapp.models.DeveloperUserModel
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.DEVELOPER
import com.example.easyfindapp.utils.POST
import com.example.easyfindapp.utils.RECYCLER_ITEM_MODEL
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.loader_layout.*

class DashboardFragment : BaseFragment() {
    private var adapter: DashBoardRecyclerAdapter? = null
    private var postsList: MutableList<PostModel>? = null
    private var developersList: MutableList<DeveloperUserModel>? = null
    override fun getFragmentLayout() = R.layout.fragment_dashboard

    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        checkDashboardDisplay()
        refreshPage()
    }

    private fun checkDashboardDisplay() {
        if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
            getResponse(EndPoints.GET_POSTS)
        } else {
            getResponse(EndPoints.GET_DEVELOPERS)
        }
    }

    private fun getResponse(path: String) {
        ResponseLoader.getResponse(path, spinLoaderView, object : ResponseCallback {
            override fun onSuccess(response: String) {
                if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
                    postsList =
                        Gson().fromJson(response, Array<PostModel>::class.java)
                            .toMutableList()
                    setAdapter(postsList!!, null)
                } else {
                    developersList =
                        Gson().fromJson(response, Array<DeveloperUserModel>::class.java)
                            .toMutableList()
                    setAdapter(null, developersList)
                }
            }

            override fun onFailure(response: String) {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show()
            }

            override fun onError(response: String) {
                Toast.makeText(context, response, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setAdapter(
        posts: MutableList<PostModel>?,
        developers: MutableList<DeveloperUserModel>?
    ) {
        adapter = DashBoardRecyclerAdapter(posts, developers) { p, i ->
            openDetailsActivity(p, i)
        }
        itemView!!.dashboardRecyclerView.layoutManager = LinearLayoutManager(context)
        itemView!!.dashboardRecyclerView.adapter = adapter
    }

    private fun openDetailsActivity(position: Int, identifier: String) {
        val intent = Intent(requireActivity(), DetailsActivity::class.java)
        if (identifier == DEVELOPER) {
            intent.putExtra(RECYCLER_ITEM_MODEL, developersList!![position])
        } else if (identifier == POST) {
            intent.putExtra(RECYCLER_ITEM_MODEL, postsList!![position])
        }
        startActivity(intent)
    }

    private fun refreshPage() {
        val refreshLayout = itemView!!.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            clearRecyclerViewData()
            Handler().postDelayed({
                checkDashboardDisplay()
                refreshLayout.isRefreshing = false
            }, 100)
        }
    }

    private fun clearRecyclerViewData() {
        if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
            postsList?.clear()
        } else {
            developersList?.clear()
        }
        adapter?.notifyDataSetChanged()
    }
}