package com.pexels.photo.search.ui

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pexels.photo.search.R
import com.pexels.photo.search.ui.adapter.PexelsPhotoAdapter
import com.pexels.photo.search.viewmodels.PexelsViewModel
import com.pexels.photo.search.viewmodels.ViewModelFactory
import androidx.lifecycle.ViewModelProviders

/**
 * Control photo search user interface
 * show all photo list to RecyclerView
 * Open dialog from action bar menu for search photo implementation
 */
class MainActivity : AppCompatActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: PexelsPhotoAdapter
    private lateinit var recyclerView: RecyclerView
    var isScrolling = false
    var isPhotoSearch = false
    var currentItems = 0
    var totalItems = 0
    var scrollOutItems = 0
    var searchQuery = String()
    var connectivity: ConnectivityManager? = null
    var info: NetworkInfo? = null
    private lateinit var viewModel: PexelsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectivity = this.getSystemService(Service.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        recyclerView = findViewById(R.id.recyclerView)

        val application = requireNotNull(this).application
        val factory = ViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(PexelsViewModel::class.java)

        initialiseAdapter()
        requestForData(false, "", false)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentItems = gridLayoutManager.childCount
                totalItems = gridLayoutManager.itemCount
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition()

                if (isScrolling && currentItems + scrollOutItems == totalItems) {

                    isScrolling = false

                    if (isPhotoSearch) {
                        requestForData(true, searchQuery, false)
                    } else {
                        requestForData(false, "", false)
                    }

                }
            }

        })

    }

    // Initialize GridLayoutManager for recyclerView
    private fun initialiseAdapter() {
        gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager
        observeData()
    }

    // Initialize adapter for recyclerView
    fun observeData() {
        adapter = PexelsPhotoAdapter(this, viewModel.arrayList)
        recyclerView.adapter = adapter
    }

    // Request for parse data to viewModel
    fun requestForData(isSearch: Boolean, searchQuery: String, isAction: Boolean) {

        if (isNetworkAvailable(this))
            viewModel.getPexelsData(this, isSearch, searchQuery, isAction, adapter)
        else
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
    }

    // Internet connection checking
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.xml.search_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            val alert = AlertDialog.Builder(this)
            val editText = EditText(this)
            editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            alert.setMessage("Enter Category e.g. Nature")
            alert.setTitle("Search Wallpaper")
            alert.setView(editText)
            alert.setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->
                val query = editText.text.toString().toLowerCase()

                if (!query.isNullOrEmpty()) {
                    isPhotoSearch = true
                    searchQuery = query
                    requestForData(isPhotoSearch, searchQuery, true)
                }
            }
            alert.setNegativeButton(
                "No"
            ) { dialogInterface, i -> }
            alert.show()
        } else if (item.itemId == R.id.allPhotoSearch) {
            isPhotoSearch = false
            requestForData(isPhotoSearch, "", true)
        }

        return super.onOptionsItemSelected(item)
    }

}