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
import com.example.easyfindapp.models.DeveloperUserModel
import com.example.easyfindapp.models.PostModel
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.DEVELOPER
import com.example.easyfindapp.utils.POST
import com.example.easyfindapp.utils.RECYCLER_ITEM_MODEL
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dashboard.view.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_favourites.view.*

class FavouritesFragment : BaseFragment() {
    private var adapter: DashBoardRecyclerAdapter? = null
    private var posts: MutableList<PostModel>? = null
    private var developers: MutableList<DeveloperUserModel>? = null

    override fun getFragmentLayout() = R.layout.fragment_favourites

    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        checkFavouritesDisplay()
        refreshPage()
    }

    private fun checkFavouritesDisplay() {
        if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
            getFavourites(EndPoints.GET_FAVOURITE_POSTS)
        } else {
            getFavourites(EndPoints.GET_FAVOURITE_DEVELOPERS)
        }
    }

    private fun getFavourites(path: String) {
        ResponseLoader.getResponseWithID(
            path,
            UserPreference.getData(UserPreference.USER_ID)!!,
            null,
            object : ResponseCallback {
                override fun onSuccess(response: String) {
                    if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
                        posts =
                            Gson().fromJson(response, Array<PostModel>::class.java)
                                .toMutableList()
                        setFavouritesAdapter(posts, null)
                    } else {
                        developers =
                            Gson().fromJson(response, Array<DeveloperUserModel>::class.java)
                                .toMutableList()
                        setFavouritesAdapter(null, developers)
                    }
                }

                override fun onFailure(response: String) {
                    Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
                }

                override fun onError(response: String) {
                    Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun setFavouritesAdapter(
        posts: MutableList<PostModel>?,
        developers: MutableList<DeveloperUserModel>?
    ) {
        adapter = DashBoardRecyclerAdapter(posts, developers) { p, i ->
            openDetailsActivity(p, i)
        }
        itemView!!.favouritesRecyclerView.layoutManager = LinearLayoutManager(context)
        itemView!!.favouritesRecyclerView.adapter = adapter
    }

    private fun openDetailsActivity(position: Int, identifier: String) {
        val intent = Intent(requireActivity(), DetailsActivity::class.java)
        if (identifier == DEVELOPER) {
            intent.putExtra(RECYCLER_ITEM_MODEL, developers!![position])
        } else if (identifier == POST) {
            intent.putExtra(RECYCLER_ITEM_MODEL, posts!![position])
        }
        startActivity(intent)
    }

    private fun refreshPage() {
        val refreshLayout = itemView!!.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            clearRecyclerViewData()
            Handler().postDelayed({
                checkFavouritesDisplay()
                refreshLayout.isRefreshing = false
            }, 100)
        }
    }

    private fun clearRecyclerViewData() {
        if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
            posts?.clear()
        } else {
            developers?.clear()
        }
        adapter?.notifyDataSetChanged()
    }
}