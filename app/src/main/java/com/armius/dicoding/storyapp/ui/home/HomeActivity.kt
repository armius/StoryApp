package com.armius.dicoding.storyapp.ui.home

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.utils.Preference
import com.armius.dicoding.storyapp.databinding.ActivityHomeBinding
import com.armius.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.armius.dicoding.storyapp.ui.login.LoginActivity
import com.armius.dicoding.storyapp.ui.maps.MapsActivity
import com.armius.dicoding.storyapp.ui.viewmodel.ViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = GridLayoutManager(this, GRID_SPAN_COUNT)
        binding.rvStory.layoutManager = layoutManager
        getData()

        binding.fabToAddstory.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
        binding.fabToMaps.setOnClickListener {
            val intent = Intent(this@HomeActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        binding.fabLogout.setOnClickListener {
            logout()
        }
    }

    private fun getData(){
        val adapter = StoryListAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        homeViewModel.story.observe(this, {
            adapter.submitData(lifecycle, it)
        })
    }

    private fun logout(){
        val preference = Preference(this)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(preference.getUserName())
        builder.setMessage(getString(R.string.msg_logout_confirmation))
        builder.setCancelable(true)
        builder.setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
            preference.clearLoginPreference()
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
        builder.setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener{ dialog, _ ->
            dialog.cancel()
        })
        builder.show()
    }

    companion object {
        const val EXTRA_KEY = "story_detail"
        const val GRID_SPAN_COUNT = 2
    }
}