package me.riddhimanadib.formmaster.model;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementImage extends BaseFormElement {

    private int MaxPictureCount = 1;
    private int MinPictureCount = 1;
    private FormElementImageStore[] ImageStore;

    public FormElementImageStore[] getImageStore() {
        return ImageStore;
    }

    public void setImageStore(FormElementImageStore[] imageStore) {
        ImageStore = imageStore;
    }

    public int getMaxPictureCount() {
        return MaxPictureCount;
    }

    public FormElementImage setMaxPictureCount(int maxPictureCount) {
        if (this.MinPictureCount>maxPictureCount) this.MaxPictureCount = this.MinPictureCount;
        else MaxPictureCount = maxPictureCount;
        return this;
    }

    public int getMinPictureCount() {
        return MinPictureCount;
    }

    public FormElementImage setMinPictureCount(int minPictureCount) {
        if (this.MaxPictureCount<minPictureCount) this.MaxPictureCount = this.MinPictureCount;
        else MinPictureCount = minPictureCount;
        return this;
    }

    public FormElementImage() {
    }

    public static FormElementImage createInstance() {
        FormElementImage formElementImage = new FormElementImage();
        formElementImage.setType(BaseFormElement.TYPE_IMAGE);
        return formElementImage;
    }

    public FormElementImage setTag(int mTag) {
        return (FormElementImage)  super.setTag(mTag);
    }

    public FormElementImage setType(int mType) {
        return (FormElementImage)  super.setType(mType);
    }

    public FormElementImage setTitle(String mTitle) {
        return (FormElementImage)  super.setTitle(mTitle);
    }

    public FormElementImage setValue(String mValue) {
        return (FormElementImage)  super.setValue(mValue);
    }

    public FormElementImage setHint(String mHint) {
        return (FormElementImage)  super.setHint(mHint);
    }

    public FormElementImage setRequired(boolean required) {
        return (FormElementImage)  super.setRequired(required);
    }

    public FormElementImage setEnabled(boolean enabled) {
        return (FormElementImage)  super.setEnabled(enabled);
    }

    
}
