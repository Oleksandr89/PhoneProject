package com.epam.rd.advphone.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.adapters.ConcreteSmsRecyclerViewAdapter;
import com.epam.rd.advphone.databinding.ActivitySmsBinding;
import com.epam.rd.advphone.models.Sms;
import com.epam.rd.advphone.viewmodels.PickContactViewModel;
import com.epam.rd.advphone.viewmodels.SmsViewModel;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.epam.rd.advphone.Constants.CONTACT_NAME;
import static com.epam.rd.advphone.Constants.CONTACT_NUMBER;

public class SmsActivity extends AppCompatActivity {
    private static final int MAX_SMS_ASCII_CHARACTER_LENGTH = 918;
    private static final int MAX_SMS_UNICODE_CHARACTER_LENGTH = 402;
    private static final int MAX_SMS_COUNT = 6;
    private static final int MAX_UNICODE_SINGLE_SMS_LENGTH = 70;
    private static final int MAX_UNICODE_MULTIPLE_SMS_LENGTH = 67;
    private static final int MAX_ASCII_SINGLE_SMS_LENGTH = 160;
    private static final int MAX_ASCII_MULTIPLE_SMS_LENGTH = 153;

    private Toolbar toolbar;
    private MutableLiveData<Boolean> isNewMessage;
    private ActivitySmsBinding smsBinding;
    private TextView smsTextLength;
    private TextView smsCount;
    private EditText smsText;
    private ImageButton sendSmsBtn;
    private Map<String, String> recipients;
    private SmsManager smsManager;
    private ArrayList<String> smsArrayList;
    private int singleSmsLength;
    private final int MAX_RECIPIENT_COUNT = 5;
    private SmsViewModel smsViewModel;
    private LayoutInflater inflater;
    private FlexboxLayout recipientsContainer;
    private RecyclerView concreteSmsRecyclerView;
    private ConcreteSmsRecyclerViewAdapter concreteSmsRecyclerViewAdapter;
    private InputMethodManager imm;
    private YoYo.YoYoString yoYoString;
    private TextView countOfRecipientsTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();
        initToolbar();
        getRecipientFromIntent();
        setupTextChangedListener();
        setupSendSmsBtnOnClickListener();
        setupConcreteSmsRecyclerView();

        isNewMessage.observe(this, aBoolean -> {
            if (aBoolean) {
                toolbar.setTitle(R.string.new_message);
            } else {
                destroyAddContactButton();
                recipientsContainer.removeAllViews();
                //set recipient number or name as toolBar title
                Map.Entry<String, String> stringEntry = recipients.entrySet().iterator().next();
                toolbar.setTitle(stringEntry.getValue() != null ? stringEntry.getValue() : stringEntry.getKey());
            }
        });

        smsViewModel.getSmsByRecipient().observe(this, concreteSmsList -> {
            if (concreteSmsList.isEmpty() && !isNewMessage.getValue()) {
                finish();
            } else {
                concreteSmsRecyclerViewAdapter.setSmsList(concreteSmsList);
                concreteSmsRecyclerView.scrollToPosition(concreteSmsList.size() - 1);
                isNewMessage.setValue(concreteSmsList.isEmpty());
            }
        });

    }

    private void initComponents() {
        smsBinding = DataBindingUtil.setContentView(this, R.layout.activity_sms);
        smsTextLength = smsBinding.smsTextLength;
        recipientsContainer = smsBinding.recipientsContainer;
        smsCount = smsBinding.smsCount;
        smsText = smsBinding.smsText;
        sendSmsBtn = smsBinding.sendSmsBtn;
        smsManager = SmsManager.getDefault();
        recipients = new HashMap<>();
        smsArrayList = new ArrayList<>();
        inflater = getLayoutInflater();
        concreteSmsRecyclerViewAdapter = new ConcreteSmsRecyclerViewAdapter();
        imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        smsViewModel = ViewModelProviders.of(this).get(SmsViewModel.class);
        isNewMessage = new MutableLiveData<>();
        isNewMessage.setValue(true);
    }

    private void setupSendSmsBtnOnClickListener() {
        sendSmsBtn.setOnClickListener(view -> {
            if (recipients.size() > 0) {
                if (smsText.length() > 0) {
                    sendSms();
                    showToast(R.string.message_sent);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(smsText.getWindowToken(), 0);
                    }

                    Boolean temp = isNewMessage.getValue();
                    if (temp != null && temp) {
                        finish();
                    } else {
                        smsText.setText("");
                        smsArrayList.clear();
                        YoYo.with(Techniques.BounceInUp).playOn(sendSmsBtn);
                    }
                } else {
                    showToast(R.string.empty_message);
                    YoYo.with(Techniques.Shake).playOn(sendSmsBtn);
                }
            } else {
                startAddButtonAnimation();
            }
        });
    }

    private void getRecipientFromIntent() {
        String recipientNumberFromIntent = getIntent().getStringExtra(CONTACT_NUMBER);
        String recipientNameFromIntent = getIntent().getStringExtra(CONTACT_NAME);

        if (recipientNumberFromIntent != null) {
            smsViewModel.setRecipientNumber(recipientNumberFromIntent);
            recipients.put(recipientNumberFromIntent, recipientNameFromIntent);
            createTextViewForNewRecipient(recipientNumberFromIntent, recipientNameFromIntent);
        } else {
            initAddContactButton();
        }
    }

    private void createTextViewForNewRecipient(String recipientNumber, String recipientName) {
        View recipientView = inflater.inflate(R.layout.single_recipient_item_for_new_message,
                smsBinding.recipientsContainer, false);
        TextView recipientId = recipientView.findViewById(R.id.recipientId);
        recipientId.setText(recipientName != null ? recipientName : recipientNumber);
        recipientView.setOnClickListener(view -> {
            recipients.remove(recipientNumber);
            countOfRecipientsTv.setText(getStringRecipientsCount());
            recipientsContainer.removeView(recipientView);
            startAddButtonAnimation();
        });
        recipientsContainer.addView(recipientView);
            initAddContactButton();
    }

    private void initAddContactButton() {
        View pickNewRecipientBnt = inflater.inflate(R.layout.pick_new_recipient_button, smsBinding.recipientsContainer, false);

        //set recipients count
        countOfRecipientsTv = pickNewRecipientBnt.findViewById(R.id.countOfRecipientsTv);
        countOfRecipientsTv.setText(getStringRecipientsCount());
        pickNewRecipientBnt.setTag(R.string.pick_new_recipient_bnt_tag);

        pickNewRecipientBnt.setOnClickListener(v -> {
            //stopping button animation
            if (yoYoString != null && yoYoString.isRunning()) {
                yoYoString.stop();
            }

            if (recipients.size() >= MAX_RECIPIENT_COUNT) {
                showAlertDialog();
            } else {
                Intent intent = new Intent(SmsActivity.this, PickContactActivity.class);
                startActivityForResult(intent, RequestCodes.REQUEST_PICK_CONTACT);
            }
        });

        if (recipients.size() > 0) {
            destroyAddContactButton();
        }

        recipientsContainer.addView(pickNewRecipientBnt);
        startAddButtonAnimation();
    }

    private void destroyAddContactButton() {
        recipientsContainer.removeView(recipientsContainer.findViewWithTag(R.string.pick_new_recipient_bnt_tag));
    }

    private void startAddButtonAnimation() {
        if (recipients.size() == 0) {
            //reset animation if it's started
            if (yoYoString != null && yoYoString.isRunning()) {
                yoYoString.stop();
                yoYoString.stop(true);
            }

            TextView noRecipientTv = new TextView(this);
            noRecipientTv.setText(getString(R.string.no_recipient, "   "));
            noRecipientTv.setTextSize(18);
            yoYoString = YoYo.with(Techniques.Tada).pivot(10, 10).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    yoYoString = YoYo.with(Techniques.Flash).withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            noRecipientTv.setVisibility(View.GONE);
                            recipientsContainer.removeView(noRecipientTv);
                        }
                    }).playOn(recipientsContainer);
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    recipientsContainer.addView(noRecipientTv);
                }
            }).repeat(2).playOn(recipientsContainer);
        }
    }

    private void sendSms() {
        for (Map.Entry<String, String> entry : recipients.entrySet()) {
//            if (smsArrayList.size() > 1) {
//                smsManager.sendMultipartTextMessage(entry.getKey(), null, smsArrayList, null, null);
//            } else {
//                smsManager.sendTextMessage(entry.getKey(), null, smsArrayList.get(0), null, null);
//            }
            Sms newSms = new Sms(smsText.getText().toString(), entry.getKey(), entry.getValue(), System.currentTimeMillis());
            Sms newSms2 = new Sms("How are you? How are you? How are you?", entry.getKey(), entry.getValue(), System.currentTimeMillis());
            newSms2.setRecipient(true);
            ViewModelProviders.of(this).get(PickContactViewModel.class).insertNewSms(newSms);
            ViewModelProviders.of(this).get(PickContactViewModel.class).insertNewSms(newSms2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RequestCodes.REQUEST_PICK_CONTACT) {
            if (resultCode == RESULT_OK && data != null) {
                String recipientNumber = data.getStringExtra(CONTACT_NUMBER);
                String recipientName = data.getStringExtra(CONTACT_NAME);

                //if map of recipients contains value then we don't create new view
                if (recipients.put(recipientNumber, recipientName) == null) {
                    createTextViewForNewRecipient(recipientNumber, recipientName);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initToolbar() {
        toolbar = smsBinding.toolBar;
        toolbar.setTitle(R.string.new_message);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupConcreteSmsRecyclerView() {
        concreteSmsRecyclerView = smsBinding.concreteSmsRecyclerView;
        concreteSmsRecyclerView.setHasFixedSize(true);
        concreteSmsRecyclerView.setAdapter(concreteSmsRecyclerViewAdapter);
        concreteSmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupTextChangedListener() {
        smsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();

                smsArrayList = smsManager.divideMessage(text);

                if (smsArrayList.size() <= 1) {
                    singleSmsLength = isASCII(text) ? MAX_ASCII_SINGLE_SMS_LENGTH : MAX_UNICODE_SINGLE_SMS_LENGTH;
                } else {
                    singleSmsLength = isASCII(text) ? MAX_ASCII_MULTIPLE_SMS_LENGTH : MAX_UNICODE_MULTIPLE_SMS_LENGTH;
                }

                smsCount.setText(String.valueOf(smsArrayList.size()));

                int lastIndex = smsArrayList.size() - 1;
                smsTextLength.setText(String.valueOf(lastIndex != -1 ? singleSmsLength - smsArrayList.get(lastIndex).length() : 0));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //stub
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.max_count_title)
                .setMessage(R.string.max_count_text)
                .setPositiveButton(R.string.ok_btn, (dialogInterface, i) -> {
                    //stub
                }).create().show();
    }

    private void showToast(int resourceId) {
        Toast.makeText(SmsActivity.this, resourceId, Toast.LENGTH_SHORT).show();
    }

    private String getStringRecipientsCount() {
        return recipients.size() > 0 ? String.valueOf(recipients.size()) : "";
    }

    private boolean isASCII(String text) {
        return text.matches("\\A\\p{ASCII}*\\z");
    }
}