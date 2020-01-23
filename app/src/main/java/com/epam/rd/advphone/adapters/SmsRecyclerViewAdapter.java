package com.epam.rd.advphone.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.models.Sms;
import com.epam.rd.advphone.util.ContactBackground;
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.util.LongDateToString;
import com.epam.rd.advphone.viewmodels.SmsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SmsRecyclerViewAdapter
        extends RecyclerView.Adapter<SmsRecyclerViewAdapter.SmsHolder>
        implements ContactCommunicator {

    private final List<Sms> smsList;

    public SmsRecyclerViewAdapter() {
        smsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SmsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SmsHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SmsHolder holder, int position) {
        Sms sms = smsList.get(position);
        holder.itemView.setOnLongClickListener(v -> {
            showDialog(position, v);
            return true;
        });

        holder.itemView.setOnClickListener(v -> showSmsActivity(holder.itemView, sms.getRecipientNumber(), sms.getRecipientName()));

        holder.smsTime.setText(LongDateToString.convert(holder.itemView.getContext(), sms.getTime()));
        holder.smsShortText.setText(sms.getMessageText());
        holder.smsRecipientName.setText(sms.getRecipientName() != null ? sms.getRecipientName() : sms.getRecipientNumber());

        TextView contactIcon = holder.contactIcon;
        ContactBackground.setContactIcon(contactIcon, sms.getRecipientName());
//        if (sms.getRecipientName() != null) {
//            contactIcon.setBackgroundResource(R.drawable.background_of_existing_contact);
//            contactIcon.setText(String.valueOf(sms.getRecipientName().charAt(0)));
//        } else {
//            contactIcon.setBackgroundResource(R.drawable.background_of_not_existing_contact);
//            contactIcon.setText("");
//        }
    }

    private void showDialog(int position, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.delete)
                .setMessage(R.string.delete_sms_chain)
                .setPositiveButton(R.string.ok_btn, (dialog, which) -> ViewModelProviders.of((FragmentActivity) v.getContext()).get(SmsViewModel.class)
                        .deleteMessagesByRecipient(smsList.get(position).getRecipientNumber()))
                .setNegativeButton(R.string.cancel_btn, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void setSmsList(List<Sms> smsList) {
        this.smsList.clear();
        this.smsList.addAll(smsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    class SmsHolder extends RecyclerView.ViewHolder {
        final TextView smsTime;
        final TextView smsShortText;
        final TextView contactIcon;
        final TextView smsRecipientName;

        SmsHolder(@NonNull View itemView) {
            super(itemView);
            smsTime = itemView.findViewById(R.id.smsTime);
            contactIcon = itemView.findViewById(R.id.contactIcon);
            smsShortText = itemView.findViewById(R.id.smsShortText);
            smsRecipientName = itemView.findViewById(R.id.smsRecipientName);
        }
    }
}
