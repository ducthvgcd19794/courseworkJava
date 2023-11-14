package com.android.mhike;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.ViewHolder> implements Filterable {

    public enum SearchType {
        NAME,
        LOCATION,
        LENGTH,
        DATE
    }

    private static final String TAG = HikeAdapter.class.getSimpleName();

    private final Context context;
    private final List<Hike> hikes;
    private List<Hike> filteredList;
    private SearchType searchType;
    private final ItemListener listener;

    public HikeAdapter(Context context, List<Hike> hikes, ItemListener listener) {
        this.context = context;
        this.hikes = hikes;
        this.filteredList = hikes;
        this.listener = listener;
        searchType = SearchType.NAME;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_hike, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hike hike = filteredList.get(position);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(hike);
            }
        });
        holder.btnEdit.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemEdit(hike);
            }
        });
        holder.btnDelete.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemDelete(hike);
            }
        });
        holder.txtName.setText(hike.getName());
        holder.txtDate.setText(hike.getDate());
        holder.txtLocation.setText(hike.getLocation());
        if (hike.getParkingAvailable() == 1) {
            holder.txtParking.setText("Parking Available: Yes");
        } else {
            holder.txtParking.setText("Parking Available: No");
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Log.d(TAG, "performFiltering: " + charSequence);
                String query = charSequence.toString();
                if (query.isEmpty()) {
                    filteredList = hikes;
                } else {
                    List<Hike> list = new ArrayList<>();
                    for (Hike hike : hikes) {
                        if (searchType == SearchType.NAME) {
                            if (hike.getName().toLowerCase().contains(query.toLowerCase())) {
                                list.add(hike);
                            }
                        } else if (searchType == SearchType.LOCATION) {
                            if (hike.getLocation().toLowerCase().contains(query.toLowerCase())) {
                                list.add(hike);
                            }
                        } else if (searchType == SearchType.LENGTH) {
                            try {
                                int len = Integer.parseInt(query);
                                if (hike.getLength() == len) {
                                    list.add(hike);
                                }
                            } catch (NumberFormatException ignored) {}
                        } else if (searchType == SearchType.DATE){
                            if (hike.getDate().toLowerCase().contains(query.toLowerCase())) {
                                list.add(hike);
                            }
                        }
                    }
                    filteredList = list;
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<Hike>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtID;
        private final TextView txtName;
        private final TextView txtDate;
        private final TextView txtLocation;
        private final TextView txtParking;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            txtID = itemView.findViewById(R.id.txt_id);
            txtName = itemView.findViewById(R.id.txt_name);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtLocation = itemView.findViewById(R.id.txt_location);
            txtParking = itemView.findViewById(R.id.txt_parking);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface ItemListener {

        void onItemClick(Hike hike);

        void onItemEdit(Hike hike);

        void onItemDelete(Hike hike);
    }
}
