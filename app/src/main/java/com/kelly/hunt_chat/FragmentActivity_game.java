package com.kelly.hunt_chat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 11/14/2017.
 */

public class FragmentActivity_game extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tabbed_game, container, false);

//        ((TabbedActivity)getActivity()).toolbar.setTitle("Games Nearby");

        return view;
    }
}
