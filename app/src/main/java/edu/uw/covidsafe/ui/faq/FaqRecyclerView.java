package edu.uw.covidsafe.ui.faq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.util.LinkedList;
import java.util.List;

public class FaqRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    List<String> q = new LinkedList<>();
    List<String> a = new LinkedList<>();
    private Context mContext;

    public FaqRecyclerView(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_row, parent, false);
        RecyclerView.ViewHolder volder = new FaqRecyclerView.FAQCard(view);
        return volder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextView)((FAQCard)holder).question.findViewById(R.id.faqQuestion)).setText(q.get(position));
        ((FAQCard)holder).question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView)(((FAQCard)holder).question.findViewById(R.id.chevron));
                if (((FAQCard)holder).answer.getVisibility() == View.GONE) {
                    iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_keyboard_arrow_down_gray_24dp));
                    ((FAQCard)holder).answer.setVisibility(View.VISIBLE);
                }
                else {
                    iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_navigate_before_black_24dp));
                    ((FAQCard)holder).answer.setVisibility(View.GONE);
                }
            }
        });
        ((TextView)((FAQCard)holder).answer.findViewById(R.id.faqAnswer)).setText(a.get(position));
    }

    public void setData(List<String> q, List<String> a) {
        this.q = q;
        this.a = a;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return q.size();
    }

    public class FAQCard extends RecyclerView.ViewHolder {
        ConstraintLayout question;
        ConstraintLayout answer;

        FAQCard(@NonNull View itemView) {
            super(itemView);
            this.question = itemView.findViewById(R.id.question);
            this.answer = itemView.findViewById(R.id.answer);
            this.answer.setVisibility(View.GONE);
        }
    }
}
