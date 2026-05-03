package com.echoes.app.ui.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.echoes.app.R
import kotlinx.coroutines.launch

/**
 * Fragment displaying aggregate analytics derived from the user's capsule archive.
 *
 * Shows media-type distribution, lock-status breakdown, unlock-condition composition,
 * and recent creation activity. All data is computed locally from Room; no network
 * calls are required.
 */
class InsightsFragment : Fragment() {

    private val viewModel: InsightsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_insights, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> renderState(view, state) }
            }
        }

        viewModel.loadInsights()
    }

    private fun renderState(view: View, state: InsightsUiState) {
        val emptyView = view.findViewById<TextView>(R.id.insightsEmptyTitle)
        val contentGroup = view.findViewById<View>(R.id.insightsContentGroup)

        if (!state.hasData && !state.isLoading) {
            emptyView.visibility = View.VISIBLE
            contentGroup.visibility = View.GONE
            return
        }

        emptyView.visibility = View.GONE
        contentGroup.visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.totalCapsulesValue).text =
            state.totalCapsules.toString()
        view.findViewById<TextView>(R.id.textCapsulesValue).text =
            state.textCapsules.toString()
        view.findViewById<TextView>(R.id.imageCapsulesValue).text =
            state.imageCapsules.toString()

        view.findViewById<TextView>(R.id.lockedCapsulesValue).text =
            state.lockedCapsules.toString()
        view.findViewById<TextView>(R.id.unlockedCapsulesValue).text =
            state.unlockedCapsules.toString()

        view.findViewById<TextView>(R.id.dateUnlockValue).text =
            state.dateUnlockCapsules.toString()
        view.findViewById<TextView>(R.id.locationUnlockValue).text =
            state.locationUnlockCapsules.toString()
        view.findViewById<TextView>(R.id.noConditionValue).text =
            state.noConditionCapsules.toString()

        view.findViewById<TextView>(R.id.createdTodayValue).text =
            state.capsulesTodayCount.toString()
        view.findViewById<TextView>(R.id.createdThisWeekValue).text =
            state.capsulesThisWeekCount.toString()

        view.findViewById<TextView>(R.id.oldestCapsuleValue).text =
            state.oldestCapsuleLabel ?: getString(R.string.insights_no_data)
        view.findViewById<TextView>(R.id.newestCapsuleValue).text =
            state.newestCapsuleLabel ?: getString(R.string.insights_no_data)
    }
}
