package edu.uw.covidsafe.ui.faq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.covidsafe.R;

import java.util.List;

public class FaqListAdapter extends BaseExpandableListAdapter {
    List<String> questions;
    List<String> answers;

    public FaqListAdapter(List<String> questions, List<String> answers) {
        this.questions = questions;
        this.answers = answers;
    }

    @Override
    public int getGroupCount() {
        return questions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return questions.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return answers.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_question, parent, false);
            TextView tv = convertView.findViewById(R.id.faqgroup);
//            Log.e("state","get group "+groupPosition);
            tv.setText(String.valueOf(getGroup(groupPosition)));
//        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        Log.e("state","get child view" + groupPosition+","+childPosition);
//        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_answer, parent, false);
            TextView tv = convertView.findViewById(R.id.faqitem);
//            Log.e("state","get child "+groupPosition+","+childPosition);
            tv.setText(String.valueOf(getChild(groupPosition,childPosition)));
//        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
