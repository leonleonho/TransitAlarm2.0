package com.example.leon.transitalarm20;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.leon.transitalarm20.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentBusSchedule extends Fragment implements AbsListView.OnItemClickListener {

    private JSONArray array;
    private ArrayList<String> test;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static FragmentBusSchedule newInstance(String param1, String param2) {
        FragmentBusSchedule fragment = new FragmentBusSchedule();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentBusSchedule() {
    }

    public void addJSONArray(JSONArray array) {
        this.array = array;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        // TODO: Change Adapter to display your content
        //mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
        /*
        test.add("Hello");
        test.add("World");
        test.add("asdf");
        test.add("qwaer");
        test.add("Hello");
        test.add("World");
        test.add("asdf");
        test.add("qwaer");
        test.add("Hello");
        test.add("World");
        test.add("asdf");
        test.add("qwaer");
        */
        test = new ArrayList<String>();
        try {
            for (int i = 0; i < array.length(); ++i) {
                JSONObject temp = array.getJSONObject(i);
                test.add(temp.getString("RouteName"));
                JSONArray schedules = temp.getJSONArray("Schedules");
                for (int j = 0; j < schedules.length(); ++j) {
                    JSONObject time = schedules.getJSONObject(j);
                    test.add("\t" + time.getString("ExpectedLeaveTime"));
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, test);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itembustime, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
