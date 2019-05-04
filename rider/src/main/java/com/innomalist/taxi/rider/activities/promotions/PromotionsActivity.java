package com.innomalist.taxi.rider.activities.promotions;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.innomalist.taxi.common.components.BaseActivity;
import com.innomalist.taxi.rider.R;
import com.innomalist.taxi.rider.activities.promotions.adapters.PromotionsRecyclerViewAdapter;
import com.innomalist.taxi.rider.databinding.ActivityPromotionsBinding;
import com.innomalist.taxi.rider.events.GetPromotionsRequestEvent;
import com.innomalist.taxi.rider.events.GetPromotionsResultEvent;
import com.tylersuehr.esr.ContentItemLoadingStateFactory;
import com.tylersuehr.esr.EmptyStateRecyclerView;
import com.tylersuehr.esr.ImageTextStateDisplay;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PromotionsActivity extends BaseActivity {
    ActivityPromotionsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PromotionsActivity.this, R.layout.activity_promotions);
        initializeToolbar("Promotions");
        binding.recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING, ContentItemLoadingStateFactory.newListLoadingState(this));
        binding.recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY, new ImageTextStateDisplay(this, com.innomalist.taxi.common.R.drawable.empty_state, "Oops!", "Nothing to show here :( Come back later maybe..."));
        binding.recyclerView.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR, new ImageTextStateDisplay(this, com.innomalist.taxi.common.R.drawable.empty_state, "SORRY...!", "Something went wrong :("));
        binding.recyclerView.invokeState(EmptyStateRecyclerView.STATE_LOADING);
        eventBus.post(new GetPromotionsRequestEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnPromotionsReceived(GetPromotionsResultEvent event) {
        if(event.hasError()){
            binding.recyclerView.invokeState(EmptyStateRecyclerView.STATE_ERROR);
            return;
        }
        if(event.promotions.size() == 0) {
            binding.recyclerView.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
            return;
        }
        binding.recyclerView.invokeState(EmptyStateRecyclerView.STATE_OK);
        PromotionsRecyclerViewAdapter promotionsRecyclerViewAdapter = new PromotionsRecyclerViewAdapter(event.promotions);
        LinearLayoutManager llm = new LinearLayoutManager(PromotionsActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(llm);
        binding.recyclerView.setAdapter(promotionsRecyclerViewAdapter);
    }
}