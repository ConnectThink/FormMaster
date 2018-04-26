package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementImage;
import me.riddhimanadib.formmaster.model.FormElementImageStore;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;


public class FormElementImageViewHolder extends BaseViewHolder {

    private HorizontalScrollView horizontal_scroll;
    private LinearLayout mLinearLayout;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private ImageView mImageView;
    private int mPosition;
    private CameraView CameraViewLayoutView;
    private MaterialDialog mdr_progress;
    private MaterialDialog show_picture_big;
    private FormElementImageTouchImageView img_to_show;
    private FormElementImageStore[] imageStore;
    private String title;
    private String content;
    private int min_pic = 0;
    private int max_pic = 0;
    private int i;

    private FormElementImage mFormElementImage;

    enum RequestSizeOptions {
        RESIZE_FIT,
        RESIZE_INSIDE,
        RESIZE_EXACT,
        RESIZE_CENTRE_CROP
    }

    public FormElementImageViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mLinearLayout = (LinearLayout) v.findViewById(R.id.linear_container);
        mImageView = (ImageView) v.findViewById(R.id.new_camera_image);
        horizontal_scroll = (HorizontalScrollView) v.findViewById(R.id.horizontal_scroll);
        mReloadListener = reloadListener;
    }

    static Bitmap resizeBitmap(Bitmap bitmap, int reqWidth, int reqHeight, RequestSizeOptions options) {
        try {
            if (reqWidth > 0 && reqHeight > 0 && (options == RequestSizeOptions.RESIZE_FIT ||
                    options == RequestSizeOptions.RESIZE_INSIDE ||
                    options == RequestSizeOptions.RESIZE_EXACT || options == RequestSizeOptions.RESIZE_CENTRE_CROP)) {

                Bitmap resized = null;
                if (options == RequestSizeOptions.RESIZE_EXACT) {
                    resized = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
                } else {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    float scale = Math.max(width / (float) reqWidth, height / (float) reqHeight);
                    if (scale > 1 || options == RequestSizeOptions.RESIZE_FIT) {
                        resized = Bitmap.createScaledBitmap(bitmap, (int) (width / scale), (int) (height / scale), false);
                    }
                    if (scale > 1 || options == RequestSizeOptions.RESIZE_CENTRE_CROP) {
                        int smaller_side = (height - width) > 0 ? width : height;
                        int half_smaller_side = smaller_side / 2;
                        Rect initialRect = new Rect(0, 0, width, height);
                        Rect finalRect = new Rect(initialRect.centerX() - half_smaller_side, initialRect.centerY() - half_smaller_side,
                                initialRect.centerX() + half_smaller_side, initialRect.centerY() + half_smaller_side);
                        bitmap = Bitmap.createBitmap(bitmap, finalRect.left, finalRect.top, finalRect.width(), finalRect.height(), null, true);
                        //keep in mind we have square as request for cropping, otherwise - it is useless
                        resized = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
                    }

                }
                if (resized != null) {
                    if (resized != bitmap) {
                        bitmap.recycle();
                    }
                    return resized;
                }
            }
        } catch (Exception e) {
            Log.w("AIC", "Failed to resize cropped image, return bitmap before resize", e);
        }
        return bitmap;
    }

    static Bitmap drawTextToBitmap(Context gContext, Bitmap bitmap, String gText, String gText2, int startPosX, int startPosY) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(255, 75, 75));
        // text size in pixels
        paint.setTextSize((int) (38 * scale));
        // text shadow
        paint.setShadowLayer(2f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        canvas.drawText(gText, startPosX, startPosY, paint);
        canvas.drawText(gText2, startPosX, startPosY + paint.getTextSize(), paint);
        return bitmap;
    }

    @Override
    public void bind(int position, BaseFormElement formElement, final Context context) {
        super.bind(position, formElement, context);

        mFormElement = formElement;
        mPosition = position;
        mFormElementImage = (FormElementImage) formElement;
        imageStore = mFormElementImage.getImageStore();

        if (imageStore != null) {
            for (i = 0; i < imageStore.length; i++) {
                final ImageView iView = new ImageView(new ContextThemeWrapper(context, R.style.image_inline), null, 0);
                iView.setEnabled(formElement.isEnabled());
                iView.setMaxWidth(mImageView.getWidth());
                iView.setMinimumHeight(mImageView.getHeight());
                iView.setImageBitmap(imageStore[i].getTiserBitMap());
                iView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img_to_show.setImageBitmap(imageStore[i].getFinalBitmap());
                        show_picture_big.show();
                    }
                });
                mLinearLayout.addView(iView);
                mLinearLayout.invalidate();
            }
        }

        final ArrayList<FormElementImageStore> imageStoreRunTime = new ArrayList<FormElementImageStore>();

        title = (formElement.getTitle() != null) ? formElement.getTitle() : "";
        content = (formElement.getValue() != null) ? formElement.getValue() : "";

        min_pic = mFormElementImage.getMinPictureCount();
        max_pic = mFormElementImage.getMaxPictureCount();

        //mEditTextValue.setHint(formElement.getHint());
        mLinearLayout.setFocusableInTouchMode(true);
        mLinearLayout.setEnabled(formElement.isEnabled());
        mImageView.setEnabled(mLinearLayout.isEnabled());

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Если задано ограничение на максимальное количество, а оно задано в 1
                if (max_pic > mLinearLayout.getChildCount()) {
                    CameraViewLayoutView = (CameraView) LayoutInflater.from(context).inflate(
                            R.layout.camera_view, null);

                    View RL_View = LayoutInflater.from(context).inflate(
                            R.layout.form_element_image_review, null);

                    img_to_show = (FormElementImageTouchImageView)
                            RL_View.findViewById(R.id.img_to_show);

                    show_picture_big = new MaterialDialog.Builder(context)
                            .customView(RL_View, false)
                            .positiveText(R.string.ok)
                            .build();


                    mdr_progress =
                            new MaterialDialog.Builder(context)
                                    .title(R.string.process)
                                    .content(R.string.wait)
                                    .progress(true, 1).build();

                    MaterialDialog mdr = new MaterialDialog.Builder(context)
                            .customView(CameraViewLayoutView, false)
                            .negativeText(R.string.cancel)
                            .positiveText(R.string.create)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    CameraViewLayoutView.stop();
                                    dialog.dismiss();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    CameraViewLayoutView.addCameraKitListener(new CameraKitEventListener() {
                                        @Override
                                        public void onEvent(CameraKitEvent cameraKitEvent) {

                                        }

                                        @Override
                                        public void onError(CameraKitError cameraKitError) {

                                        }

                                        @Override
                                        public void onImage(CameraKitImage cameraKitImage) {
                                            // Create a bitmap
                                            final Bitmap result_bitmap = drawTextToBitmap(context, cameraKitImage.getBitmap(), title, content, 200, 200);
                                            byte[] result = cameraKitImage.getJpeg();
                                            final ImageView iView = new ImageView(new ContextThemeWrapper(context, R.style.image_inline), null, 0);
                                            Bitmap resized_bitmap = resizeBitmap(result_bitmap, mImageView.getWidth(), mImageView.getHeight(), RequestSizeOptions.RESIZE_CENTRE_CROP);  //Bitmap.createScaledBitmap(result_bitmap, mImageView.getWidth(), aspect_ratio, true);

                                            iView.setMaxWidth(mImageView.getWidth());
                                            iView.setMinimumHeight(mImageView.getHeight());
                                            iView.setImageBitmap(resized_bitmap);
                                            iView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    img_to_show.setImageBitmap(result_bitmap);
                                                    show_picture_big.show();
                                                }
                                            });

                                            mLinearLayout.addView(iView);
                                            mLinearLayout.invalidate();
                                            CameraViewLayoutView.stop();


                                            FormElementImageStore tmp_FormElementImageStore = new FormElementImageStore();
                                            tmp_FormElementImageStore.setFinalBitmap(result_bitmap);
                                            tmp_FormElementImageStore.setTiserBitMap(resized_bitmap);

                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            result_bitmap.compress(Bitmap.CompressFormat.PNG, 85, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            tmp_FormElementImageStore.setFinalJpeg(byteArray);

                                            imageStoreRunTime.add(tmp_FormElementImageStore);

                                            imageStore = imageStoreRunTime.toArray(new FormElementImageStore[imageStoreRunTime.size()]);
                                            mFormElementImage.setImageStore(imageStore);
                                            mdr_progress.dismiss();

                                            horizontal_scroll.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    horizontal_scroll.smoothScrollTo(mLinearLayout.getChildAt(mLinearLayout.getChildCount() - 1).getLeft(), mLinearLayout.getChildAt(mLinearLayout.getChildCount() - 1).getBottom());

                                                }
                                            });
                                        }

                                        @Override
                                        public void onVideo(CameraKitVideo cameraKitVideo) {

                                        }
                                    });
                                    CameraViewLayoutView.captureImage();
                                    mdr_progress.show();
                                    dialog.dismiss();
                                }
                            }).build();
                    mdr.show();
                    CameraViewLayoutView.start();
                } else {
                    MaterialDialog mdr =
                            new MaterialDialog.Builder(context)
                                    .title(R.string.warning)
                                    .positiveText(R.string.ok)
                                    .content(R.string.max_achieved).build();
                    mdr.show();
                }
            }
        });
    }

}
