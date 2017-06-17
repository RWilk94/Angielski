package com.rwilk.angielski.views.teaching;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.rwilk.angielski.R;
import com.rwilk.angielski.customview.CustomAdapterShowWord;
import com.rwilk.angielski.database.Word;
import com.rwilk.angielski.views.TeachingActivity;

import java.util.ArrayList;
import static com.rwilk.angielski.R.layout.fragment_show_word;
/**
 * Created by wilkr on 24.04.2017.
 */

public class ShowWord extends Fragment {

    public static ArrayList<Word> listOfWordsToDisplay;
    public LinearLayout linearLayoutShowWord;
    public ListView listViewWords;
    public static ArrayAdapter adapter;
    //public ImageButton imageViewShowWordArrowRight;

    public static ShowWord newInstance(ArrayList<Word> arrayList){
        Bundle args = new Bundle();
        ShowWord fragment = new ShowWord();
        fragment.setArguments(args);
        listOfWordsToDisplay = arrayList;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_show_word, container, false);

        linearLayoutShowWord = (LinearLayout)view.findViewById(R.id.linearLayoutShowWord);
        //imageViewShowWordArrowRight = (ImageButton)view.findViewById(R.id.imageViewShowWordArrowRight);

        /*if(listOfWordsToDisplay != null && !listOfWordsToDisplay.isEmpty()){
            for (int i = 0; i<listOfWordsToDisplay.size(); i++){
                TextView textView = new TextView(getActivity());
                String text = listOfWordsToDisplay.get(i).getEnglishWord() + " - " + listOfWordsToDisplay.get(i).getPolishWord();
                textView.setText(text);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                linearLayoutShowWord.addView(textView);
            }
        }*/


        listViewWords = (ListView) view.findViewById(R.id.listViewAllWords);//przypisanie listView
        adapter = new CustomAdapterShowWord(getContext(), listOfWordsToDisplay);
        listViewWords.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //tutaj zrobic jeszcze mozliwosc klikania w poszczegolne sloty tej listy
        //i wywalic albo naprawic trudne slowko

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        listViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String speak = listOfWordsToDisplay.get(position).getEnglishWord();
                ((TeachingActivity)getActivity()).ttsSpeech(speak);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
