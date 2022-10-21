package com.armius.dicoding.storyapp.ui.home

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.armius.dicoding.storyapp.R
import com.armius.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.armius.dicoding.storyapp.ui.detail.DetailActivity
import com.armius.dicoding.storyapp.ui.maps.MapsActivity
import com.armius.dicoding.storyapp.ui.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    @Test
    fun load_ListStories_Success() {
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))
    }

    @Test
    fun load_DetailStory_Success() {
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        intended(hasComponent(DetailActivity::class.java.name))
        onView(withId(R.id.iv_detail_story)).check(matches(isDisplayed()))
    }

    @Test
    fun goto_Map_Success() {
        Intents.init()
        onView(withId(R.id.fab_to_maps)).perform(longClick())
        intended(hasComponent(MapsActivity::class.java.name))
    }

    @Test
    fun goto_AddStory_Success() {
        onView(withId(R.id.fab_to_addstory)).perform(longClick())
        intended(hasComponent(AddStoryActivity::class.java.name))
    }
}