package com.armius.dicoding.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.model.Story
import com.armius.dicoding.storyapp.databinding.ActivityDetailBinding
import com.armius.dicoding.storyapp.ui.home.HomeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.title = getString(R.string.story_detail)

        val storyData = intent.extras?.getParcelable<Story>(HomeActivity.EXTRA_KEY)
        with(binding) {
            val tmp = storyData?.createdAt.toString().split(".")
            val tmp2 = tmp[0].split("T")
            val date = tmp2[0]
            val time = tmp2[1]
            tvDetailStoryTime.text = "$date @$time"
            tvDetailUsername.text = storyData?.name
            tvDetailDescription.text = storyData?.description
            Glide.with(this@DetailActivity)
                .load(storyData?.photoUrl)
                .error(R.drawable.image_icon)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDetailStory)
        }
    }
}