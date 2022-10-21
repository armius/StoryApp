package com.armius.dicoding.storyapp.ui.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.core.entity.StoryEntity
import com.armius.dicoding.storyapp.core.model.Story
import com.armius.dicoding.storyapp.databinding.ItemStoryBinding
import com.armius.dicoding.storyapp.ui.detail.DetailActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class StoryListAdapter : PagingDataAdapter<StoryEntity, StoryListAdapter.StoryListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListAdapter.StoryListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryListAdapter.StoryListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class StoryListViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity?) {
            with(binding) {
                story?.apply {
                    val tmp = createdAt!!.split(".")
                    val tmp2 = tmp[0].split("T")
                    val date = tmp2[0]
                    val time = tmp2[1]
                    tvItemStory.text = name
                    tvItemStoryTime.text = "$date @$time"
                    Glide.with(binding.root)
                        .load(photoUrl)
                        .error(R.drawable.image_icon)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivItemStory)
                    val detail = Story(story.name, story.description, story.photoUrl, story.createdAt)

                    itemView.setOnClickListener {
                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                itemView.context as Activity,
                                Pair(ivItemStory, "image"),
                                Pair(tvItemStory, "username"),
                                Pair(tvItemStoryTime, "datetime")
                            )
                        Intent(itemView.context, DetailActivity::class.java)
                            .apply {
                                putExtra(HomeActivity.EXTRA_KEY, detail)
                                itemView.context.startActivity(
                                    this,
                                    optionsCompat.toBundle()
                                )
                            }
                    }
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}