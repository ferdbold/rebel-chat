package com.mirego.rebelchat.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirego.rebelchat.R;
import com.mirego.rebelchat.controllers.MessageController;
import com.mirego.rebelchat.controllers.MessageControllerImpl;
import com.mirego.rebelchat.transition.ScaleTransition;
import com.mirego.rebelchat.utilities.Encoding;
import com.mirego.rebelchat.utilities.RandomString;
import com.mirego.rebelchat.views.DrawingCanvas;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity {

    private static String EXTRA_USER_ID = "extra_user_id";

    private MessageController messageController;
    private String currentUserId;
    private Handler messageHandler;
    private Runnable messageCallback;

    @Bind(R.id.root)
    View root;

    @Bind(R.id.canvas)
    View screenView;

    @Bind(R.id.message_image)
    ImageView messageImage;

    @Bind(R.id.message_text)
    TextView messageText;

    @Bind(R.id.drawing_canvas)
    DrawingCanvas drawingCanvas;

    public static Intent newIntent(Activity fromActivity, String userId) {
        Intent intent = new Intent(fromActivity, MessageActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        messageHandler = new Handler();


        messageController = new MessageControllerImpl();
        currentUserId = getIntent().getStringExtra(EXTRA_USER_ID);

        setRandomString();

        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(true);

        getWindow().setExitTransition(createTransition(true));
        getWindow().setEnterTransition(createTransition(false));
        getWindow().setReturnTransition(createTransition(true));
        getWindow().setReenterTransition(createTransition(false));


        messageCallback = new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);

                Bitmap screenshot = Bitmap.createBitmap(screenView.getWidth(), screenView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(screenshot);
                screenView.layout(0, 0, screenView.getWidth(), screenView.getHeight());
                screenView.draw(c);
                String base64Image = Encoding.encodeImageToBase64(screenshot);

                String text = messageText.getText().toString();

                messageController.sendMessage(getApplicationContext(), currentUserId, text, base64Image, new MessageController.SendMessageCallback() {
                    @Override
                    public void onSendMessageSuccess() {
                        dismissLoadingIndicator(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(root, R.string.message_send_success, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSendMessageFail() {
                        dismissLoadingIndicator(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(root, R.string.message_send_error, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        };
    }

    private Transition createTransition(boolean goingOut) {
        TransitionSet transitionSet = new TransitionSet();

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setInterpolator(goingOut ? new AccelerateInterpolator() : new DecelerateInterpolator());
        autoTransition.addTarget(R.id.message_image);

        Slide slideUp = new Slide(Gravity.BOTTOM);
        slideUp.setInterpolator(goingOut ? new AccelerateInterpolator() : new DecelerateInterpolator());
        slideUp.addTarget(R.id.message_text);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.addTarget(R.id.btn_logout);
        scaleTransition.addTarget(R.id.btn_shuffle);
        scaleTransition.addTarget(R.id.btn_snap);
        scaleTransition.addTarget(R.id.btn_small_brush);
        scaleTransition.addTarget(R.id.btn_medium_brush);
        scaleTransition.addTarget(R.id.btn_large_brush);
        scaleTransition.addTarget(R.id.btn_color_red);
        scaleTransition.addTarget(R.id.btn_color_white);
        scaleTransition.addTarget(R.id.btn_color_green);

        transitionSet.addTransition(autoTransition);
        transitionSet.addTransition(slideUp);
        transitionSet.addTransition(scaleTransition);

        transitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        return transitionSet;
    }

    @Override
    public void onBackPressed() {
        startActivity(HomeActivity.newIntent(this), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @OnClick(R.id.btn_logout)
    void onLogoutPressed() {
        startActivity(HomeActivity.newIntent(this), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @OnClick(R.id.btn_shuffle)
    void onShufflePressed() {
        setRandomString();
    }

    @OnClick(R.id.btn_snap)
    void onSnapPressed() {
        takeAndSendScreenshot();
    }

    @OnClick(R.id.message_text)
    void onWriteEdit() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(messageText, InputMethodManager.SHOW_IMPLICIT);
        takeAndSendScreenshot();
    }

    @OnClick(R.id.btn_small_brush)
    void onSmallBrushPressed() { this.drawingCanvas.SetBrushSize(DrawingCanvas.SMALL_BRUSH); }

    @OnClick(R.id.btn_medium_brush)
    void onMediumBrushPressed() { this.drawingCanvas.SetBrushSize(DrawingCanvas.MEDIUM_BRUSH); }

    @OnClick(R.id.btn_large_brush)
    void onLargeBrushPressed() { this.drawingCanvas.SetBrushSize(DrawingCanvas.LARGE_BRUSH); }

    @OnClick(R.id.btn_color_red)
    void onRedColorPressed() { this.drawingCanvas.SetBrushColor(Color.RED); }

    @OnClick(R.id.btn_color_green)
    void onGreenColorPressed() { this.drawingCanvas.SetBrushColor(Color.GREEN); }

    @OnClick(R.id.btn_color_white)
    void onWhiteColorPressed() { this.drawingCanvas.SetBrushColor(Color.WHITE); }

    private void setRandomString() {
        String randomString = RandomString.generate(16);
        messageText.setText(randomString);
    }

    private void takeAndSendScreenshot() {
        showLoadingIndicator(getString(R.string.message_send_progress));

        messageHandler.post(messageCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageHandler.removeCallbacks(messageCallback);
    }
}
