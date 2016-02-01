package com.example.shi.subusapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;


public class MyRecyclerView extends Fragment{
    private static final String ARG_OPTION = "option";
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter1 mRecyclerViewAdapter1;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieDataJson2 userData;
    public OnListItemSelectedListener mListener;
    private static final int REQUEST_DATE=0;
    public static final String DATE_ARGS1 = "index";

    public static MyRecyclerView newInstance(int option){
        MyRecyclerView fragment=new MyRecyclerView();
        Bundle args = new Bundle();
        args.putInt(ARG_OPTION,option);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        setRetainInstance(true);
        userData = new MovieDataJson2(getActivity());///待解决
    }

    public  MyRecyclerView(){
        //movieData=new MovieData();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

    }

    public interface OnListItemSelectedListener{
        public void onItemClick1(int position,HashMap<String,?>movie);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener=(OnListItemSelectedListener)activity;

        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    +"must implement OnItemSelectedListener");

        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        Uri contactUri=data.getData();
        Log.d("Mydebug", "111111111111");
        if(resultCode!=Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_DATE){
            Log.d("Mydebug", "1111222222");
            //Date date=(Date) data.getSerializableExtra(MyDialogFragment.DATE_ARGS);
            String input=(String) data.getSerializableExtra(MyDialogFragment.TEXT_ARGS);
            Log.d("Mydebug", input);
            Toast.makeText(getActivity(),input,Toast.LENGTH_SHORT).show();
            //mRecyclerViewAdapter1.notifyDataSetChanged();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setRetainInstance(true);
        final View rootView = inflater.inflate(R.layout.recyclerview, container, false);
        if(MainActivity.BackColor != 0){
            rootView.setBackgroundColor(MainActivity.BackColor);
        }
        if(MainActivity.grayDrawable) {
            Resources resources = getActivity().getResources();
            Drawable btnDrawable = resources.getDrawable(R.drawable.gray_gradient);
            rootView.setBackground(btnDrawable);
        }
        mRecyclerView=(RecyclerView) rootView.findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        int option=getArguments().getInt(ARG_OPTION);
        switch (option){
            case 0:
                mLayoutManager=new LinearLayoutManager(getActivity());
                mRecyclerViewAdapter1=new MyRecyclerViewAdapter1(getActivity(),userData.getUserList(),0);
                break;
            case 1:
                mLayoutManager=new GridLayoutManager(getActivity(),4);
                mRecyclerViewAdapter1=new MyRecyclerViewAdapter1(getActivity(),userData.getUserList(),1);
                break;
            default:
                mLayoutManager=new LinearLayoutManager(getActivity());
                mRecyclerViewAdapter1=new MyRecyclerViewAdapter1(getActivity(),userData.getUserList(),0);
                break;
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter1);
        mRecyclerViewAdapter1.SetOnListItemSelectedListener(mListener);
        mRecyclerViewAdapter1.SetOnItemClickListener(new MyRecyclerViewAdapter1.OnItemClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                getActivity().startActionMode(new ActionBarCallBack(position));
            }
            @Override
            public void onOverflowMenuClicked(View v, final int position){
//                PopupMenu popup=new PopupMenu(getActivity(),v);
//                MenuInflater inflater=popup.getMenuInflater();
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()){
//                            case R.id.item_duplicate:
//                                /*mRecyclerViewAdapter1.duplicate(position);
//                                mRecyclerViewAdapter1.notifyItemInserted(position+1);*/
//                                Intent intent=new Intent(getActivity(),EditActivity.class);
//                                intent.putExtra(DATE_ARGS1,(HashMap<String,?>)userData.userList.get(position));
//                                startActivityForResult(intent, REQUEST_DATE);
//
//                                return true;
//                            case R.id.item_delete:
//                                userData.removeItem(position);
//                                mRecyclerViewAdapter1.notifyItemRemoved(position);
//                                return true;
//                            default:
//                                return false;
//                        }
//
//                    }
//                });
//                inflater.inflate(R.menu.contextual_or_popup_menu,popup.getMenu());
//                popup.show();
            }
        });
        MydownloadJsonAsynTask downloadJson= new MydownloadJsonAsynTask(mRecyclerViewAdapter1);
        String url=MovieDataJson2.FILE_SERVER+"/Users/";//rating/"+query;
        downloadJson.execute(url);
        return rootView;
    }
    private class MydownloadJsonAsynTask extends AsyncTask<String,Void,MovieDataJson2> {
        private final WeakReference<MyRecyclerViewAdapter1> adapterWeakReference;
        public MydownloadJsonAsynTask(MyRecyclerViewAdapter1 adapter){
            adapterWeakReference=new WeakReference<MyRecyclerViewAdapter1>(adapter);

        }
        @Override
        protected MovieDataJson2 doInBackground(String... urls){
            MovieDataJson2 threadMovieData=new MovieDataJson2(getActivity());
            for(String url:urls){
                threadMovieData.downloadDataJson(url);
            }
            return threadMovieData;
        }
        @Override
        protected void onPostExecute (MovieDataJson2 threadMovieData){
            userData.userList.clear();
            for(int i=0;i<threadMovieData.getSize();i++){
                userData.userList.add(threadMovieData.userList.get(i));
            }
            if(adapterWeakReference!=null){
                final MyRecyclerViewAdapter1 adapter1=adapterWeakReference.get();
                if(adapter1!=null){
                    adapter1.notifyDataSetChanged();
                }
            }

        }
    }

    public class ActionBarCallBack implements ActionMode.Callback {
        int position;
        public ActionBarCallBack(int position) {
            this.position=position;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item){
            int id= item.getItemId();
            switch(id)
            {
                case R.id.item_delete:
                    userData.removeItem(position);
                    mRecyclerViewAdapter1.notifyItemRemoved(position);
                    mode.finish();
                    break;
                case R.id.item_duplicate:
                    mRecyclerViewAdapter1.duplicate(position);
                    mRecyclerViewAdapter1.notifyItemInserted(position+1);
                    mode.finish();
                    break;
                default:
                    break;

            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu){
            mode.getMenuInflater().inflate(R.menu.contextual_or_popup_menu, menu);
            return true;
        }


        @Override
        public void onDestroyActionMode(ActionMode mode){

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode,Menu menu){
            HashMap hm=(HashMap) userData.getItem(position);
            mode.setTitle((String) hm.get("name"));
            return false;
        }
    }
}
