package com.spiraldev.cryptoticker.ui.coinsList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spiraldev.cryptoticker.R
import com.spiraldev.cryptoticker.adapters.CoinsListAdapter
import com.spiraldev.cryptoticker.adapters.OnItemClickCallback
import com.spiraldev.cryptoticker.databinding.FragmentListBinding
import com.spiraldev.cryptoticker.ui.MainNavigationFragment
import com.spiraldev.cryptoticker.util.extensions.doOnChange
import kotlinx.android.synthetic.main.fragment_list.*


class CoinListFragment : MainNavigationFragment(), OnItemClickCallback {

    private val viewModel by viewModels<CoinListViewModel> { viewModelFactory }
    private lateinit var binding: FragmentListBinding
    private var coinsListAdapter = CoinsListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = this@CoinListFragment.viewModel
            }
        observeViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        viewModel.loadCoinsFromApi()
    }

    override fun initializeViews() {
        coinsListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coinsListAdapter
        }
    }

    override fun observeViewModel() {
        viewModel.isLoading.doOnChange(this) {
            coinsListLoading.visibility =
                if (viewModel.isListEmpty() && it) View.VISIBLE else View.GONE

            if (it) {
                coinsListErrorView.visibility = View.GONE
            }
        }

        viewModel.coinsListData.doOnChange(this) {
            coinsListAdapter.updateList(it)

            coinsListErrorView.visibility =
                if (viewModel.isListEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.favouriteStock.doOnChange(this) {
            it?.let {
                showToast(
                    getString(if (it.isFavourite) R.string.added_to_favourite else R.string.removed_to_favourite).format(
                        it.symbol
                    )
                )
            }
        }
    }

    override fun onItemClick(symbol: String) {

    }

    override fun onFavouriteClicked(symbol: String) {
        viewModel.updateFavouriteStatus(symbol)
    }
}