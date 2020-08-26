package com.example.easyfindapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easyfindapp.R
import androidx.fragment.app.Fragment
import com.example.easyfindapp.fragments.home.details.DeveloperDetailsFragment
import com.example.easyfindapp.fragments.home.details.PostDetailsFragment
import com.example.easyfindapp.models.DeveloperUserModel
import com.example.easyfindapp.models.PostModel
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.EMPLOYER
import com.example.easyfindapp.utils.RECYCLER_ITEM_MODEL

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        init()
    }

    private fun init() {
        getModelData()
    }

    private fun getModelData() {
        val detailsModel: Any =
            intent.getParcelableExtra<DeveloperUserModel>(RECYCLER_ITEM_MODEL) as Any
        chooseRequiredFragment(detailsModel)
    }

    private fun chooseRequiredFragment(detailsModel: Any) {
        if (UserPreference.getData(UserPreference.ROLE) == EMPLOYER) {
            val developerDetailsFragment = DeveloperDetailsFragment()
            developerDetailsFragment.developerUserModel = detailsModel as DeveloperUserModel
            openDetailsFragment(developerDetailsFragment, "DeveloperDetailsFragment")
        } else {
            val postDetailsActivity = PostDetailsFragment()
            postDetailsActivity.postModel = detailsModel as PostModel
            openDetailsFragment(postDetailsActivity, "PostDetailsFragment")
        }
    }

    private fun openDetailsFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.detailsFragmentContainer, fragment, tag)
        transaction.commit()
    }
}