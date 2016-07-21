package com.coppermobile.myweather.TabLayoutFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coppermobile.myweather.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiveDaysFragment extends Fragment {


    public FiveDaysFragment() {
        // Required empty public constructor
    }

    public static FiveDaysFragment newInstance() {
        return new FiveDaysFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_five_days, container, false);
    }

    public void setData() {

        View v = getView();
        v.findViewById(R.id.recycler_view_five_days_fragment);

    }
}
