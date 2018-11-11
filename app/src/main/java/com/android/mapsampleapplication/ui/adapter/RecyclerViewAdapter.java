package com.android.mapsampleapplication.ui.adapter;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.mapsampleapplication.R;
import com.android.mapsampleapplication.model.Vehicle;
import com.android.mapsampleapplication.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context context;
    private ArrayList<Vehicle> mData;

    private LayoutInflater mLayoutInflater;

    private boolean mIsSpaceVisible = true;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    private WeakReference<ItemClickListener> mCallbackRef;

    public RecyclerViewAdapter(Context ctx, ArrayList<Vehicle> data, ItemClickListener listener) {
        this.context =ctx;
        mLayoutInflater = LayoutInflater.from(ctx);
        mData = data;
        mCallbackRef = new WeakReference<>(listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View v = mLayoutInflater.inflate(R.layout.list_details, parent, false);
            return new MyItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = mLayoutInflater.inflate(R.layout.transparent_header_view, parent, false);
            return new HeaderItem(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyItem) {
            Vehicle dataItem = getItem(position);
            List<Address> addressDetails = Utils.getPlaceDetails(context,Double.valueOf(dataItem.getCoordinate().getLatitude()),Double.valueOf(dataItem.getCoordinate().getLatitude()));
            String text = String.format(context.getString(R.string.vehicleText), dataItem.getType(), dataItem.getId(), addressDetails.get(0).getLocality()+","+addressDetails.get(0).getCountryName());

            ((MyItem) holder).mCurrentView.setText(text);
            ((MyItem) holder).mPosition = position;
        } else if (holder instanceof HeaderItem) {
            ((HeaderItem) holder).mSpaceView.setVisibility(mIsSpaceVisible ? View.VISIBLE : View.GONE);
            ((HeaderItem) holder).mPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private Vehicle getItem(int position) {
        return mData.get(position - 1);
    }

    class MyItem extends HeaderItem {
        TextView mCurrentView;

        public MyItem(View itemView) {
            super(itemView);
            mCurrentView = itemView.findViewById(R.id.textValue);

        }
    }

    class HeaderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mSpaceView;
        int mPosition;

        public HeaderItem(View itemView) {
            super(itemView);
            mSpaceView = itemView.findViewById(R.id.space);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ItemClickListener callback = mCallbackRef != null ? mCallbackRef.get() : null;
            if (callback != null) {
                callback.onItemClicked(mPosition);
            }

        }
    }

    public void hideSpace() {
        mIsSpaceVisible = false;
        notifyItemChanged(0);
    }

    public void showSpace() {
        mIsSpaceVisible = true;
        notifyItemChanged(0);
    }
}