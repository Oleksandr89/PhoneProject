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
import com.epam.rd.advphone.util.ContactCommunicator;
import com.epam.rd.advphone.util.LongDateToString;
import com.epam.rd.advphone.viewmodels.SmsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ConcreteSmsRecyclerViewAdapter
        extends RecyclerView.Adapter<ConcreteSmsRecyclerViewAdapter.ConcreteSmsHolder>
        implements ContactCommunicator {

    private final List<Sms> smsList;
    private final int RECIPIENT = 0;

    public ConcreteSmsRecyclerViewAdapter() {
        smsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ConcreteSmsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(viewType == RECIPIENT ? R.layout.single_sms_from_recipient_text
                : R.layout.single_sms_to_recipient_text, parent, false);
        return new ConcreteSmsHolder(itemView.getRootView());
    }

    @Override
    public void onBindViewHolder(@NonNull ConcreteSmsHolder holder, int position) {
        Sms sms = smsList.get(position);

        TextView recipientIcon = holder.recipientIcon;
        if (recipientIcon != null) {
            if (sms.getRecipientName() != null) {
                recipientIcon.setBackgroundResource(R.drawable.background_of_existing_contact);
                recipientIcon.setText(String.valueOf(sms.getRecipientName().charAt(0)));
            } else {
                recipientIcon.setBackgroundResource(R.drawable.background_of_not_existing_contact);
                recipientIcon.setText("");
            }
        }

        holder.smsTime.setText(LongDateToString.convert(holder.itemView.getContext(), sms.getTime()));
        holder.smsText.setText(sms.getMessageText());
        holder.itemView.setOnLongClickListener(v -> {
            showDialog(v, position);
            return true;
        });
    }

    private void showDialog(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.delete_single_message)
                .setPositiveButton(R.string.ok_btn, (dialog, which) -> ViewModelProviders.of((FragmentActivity) v.getContext()).get(SmsViewModel.class)
                        .deleteMessagesById(smsList.get(position).getId()))
                .setNegativeButton(R.string.cancel_btn, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public int getItemViewType(int position) {
        int NOT_RECIPIENT = 1;
        return smsList.get(position).isRecipient() ? RECIPIENT : NOT_RECIPIENT;
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

    class ConcreteSmsHolder extends RecyclerView.ViewHolder {
        final TextView smsTime;
        final TextView smsText;
        final TextView recipientIcon;

        ConcreteSmsHolder(View itemView) {
            super(itemView);
            smsTime = itemView.findViewById(R.id.smsTime);
            smsText = itemView.findViewById(R.id.smsText);
            recipientIcon = itemView.findViewById(R.id.recipientIcon);
        }
    }
}
