package it.polito.mobile.androidassignment2.StudentFlow;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.dummy.DummyContent;


public class ItemFragment extends Fragment implements AdapterView.OnItemClickListener{


	private ListView mListView;
	private Communicator communicator;
	private ListAdapter mAdapter;
	private Button mButtonSearch;
	private boolean isSearchEnabled=true;
	private int kind;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */

	public ItemFragment() {
	}
	public void setCommunicator(Communicator comm) {
		this.communicator = comm;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: Change Adapter to display your content
		mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_item, container, false);

		// Set the adapter
		mListView = (ListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
		mButtonSearch= (Button) view.findViewById(R.id.button_search);
		if(isSearchEnabled) {
			mButtonSearch.setVisibility(Button.VISIBLE);
			mButtonSearch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					communicator.goSearch(kind);
				}
			});;
	}
		else
			mButtonSearch.setVisibility(Button.INVISIBLE);
		// Set OnItemClickListener so we can be notified on item clickss
		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	public void setIsSearchEnabled(boolean isSearchEnabled) {
		this.isSearchEnabled = isSearchEnabled;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}
}

