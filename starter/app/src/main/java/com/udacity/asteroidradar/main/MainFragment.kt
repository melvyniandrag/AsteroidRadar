package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity){
            "you can only acess the viewmodel after onViewCreated()"
        }

        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(MainViewModel::class.java)
    }

    private var viewModelAdapter: AsteroidAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false)
        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter = AsteroidAdapter(AsteroidClickListener {
            asteroid ->  viewModel.onAsteroidClicked(asteroid)
        })

        viewModel.clickedAsteroid.observe(viewLifecycleOwner, Observer{
            it.let{
                if(it.id != -1L){
                    this.findNavController().navigate(MainFragmentDirections.actionShowDetail(selectedAsteroid = it))
                    viewModel.doneNavigating()
                }
            }
        })

        setHasOptionsMenu(true)

        binding.root.findViewById<RecyclerView>(R.id.asteroid_recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
            asteroids?.apply {
                viewModelAdapter?.asteroids = asteroids
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Always return true.
     * We are not navigating anywhere either.
     * Just trigger an update to the displayed asteroids
     *
     * I dont like this code, but not sure how to make it better.
     * viewmodel holds three sets of asteroids - todays, all the saved ones, and the week's asteroids.
     * When you pick an item, stop watching two of them and start watching the appropriate one.
     * Correction not watch, "observe"
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.show_saved_menu -> {
                viewModel.asteroids.removeObservers(viewLifecycleOwner)
                viewModel.todayAsteroids.removeObservers(viewLifecycleOwner)
                viewModel.savedAsteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { savedAsteroids ->
                    savedAsteroids?.apply {
                        viewModelAdapter?.asteroids = savedAsteroids
                    }
                })
            }
            R.id.show_today_menu -> {
                viewModel.savedAsteroids.removeObservers(viewLifecycleOwner)
                viewModel.asteroids.removeObservers(viewLifecycleOwner)
                viewModel.todayAsteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { todayAsteroids ->
                    todayAsteroids?.apply {
                        viewModelAdapter?.asteroids = todayAsteroids
                    }
                })

            }
            R.id.show_week_menu -> {
                viewModel.savedAsteroids.removeObservers(viewLifecycleOwner)
                viewModel.todayAsteroids.removeObservers(viewLifecycleOwner)
                viewModel.asteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
                    asteroids?.apply {
                        viewModelAdapter?.asteroids = asteroids
                    }
                })
            }
        }
        return true
    }
}
