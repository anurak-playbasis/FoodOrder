package nilezia.app.foodorder.ui.history

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_history.*
import nilezia.app.foodorder.R
import nilezia.app.foodorder.base.BaseMvpFragment
import nilezia.app.foodorder.model.HistoryItem
import nilezia.app.foodorder.ui.MainActivityContract
import nilezia.app.foodorder.ui.history.adapter.OrderHistoryAdapter
import nilezia.app.foodorder.ui.repository.OrderRepository


class OrderHistoryFragment : BaseMvpFragment<OrderHistoryContract.View, OrderHistoryContract.Presenter>(), OrderHistoryContract.View {

    override var mPresenter: OrderHistoryContract.Presenter = OrderHistoryPresenter()
    lateinit var mAdapter: OrderHistoryAdapter

    companion object {
        fun newInstance() = OrderHistoryFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override fun setupLayout(): Int = R.layout.fragment_history

    override fun bindView(view: View) {


    }

    override fun setupView() {
        setupAdapter()
        setupRecyclerView()
        setupSwiftRefresh()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
                    .apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
            adapter = mAdapter
        }
    }

    private fun setupAdapter() {
        mAdapter = OrderHistoryAdapter(onItemOrderHistorySelect())

    }

    override fun setupInstance() {
        mPresenter.registerRepository(OrderRepository(context!!))
        mPresenter.requestHistory()
    }

    private fun setupSwiftRefresh() {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            mPresenter.requestHistory()
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {

    }

    override fun onUpdateHistoryList(items: MutableList<HistoryItem>) {
        swipeRefresh.isRefreshing = false
        mAdapter.historyItems = items
        mAdapter.notifyDataSetChanged()
    }

    private fun onItemOrderHistorySelect(): (HistoryItem) -> Unit = {

        val listener = activity as MainActivityContract.View
        listener.goToDetail(it)


    }


}
