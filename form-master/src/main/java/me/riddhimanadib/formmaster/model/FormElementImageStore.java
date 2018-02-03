package me.riddhimanadib.formmaster.model;

import android.graphics.Bitmap;

public class FormElementImageStore {
    private Bitmap finalBitmap;
    private byte[] finalJpeg;
    private Bitmap tiserBitMap;

    public Bitmap getFinalBitmap() {
        return finalBitmap;
    }

    public void setFinalBitmap(Bitmap finalBitmap) {
        this.finalBitmap = finalBitmap;
    }

    public byte[] getFinalJpeg() {
        return finalJpeg;
    }

    public void setFinalJpeg(byte[] finalJpeg) {
        this.finalJpeg = finalJpeg;
    }

    public Bitmap getTiserBitMap() {
        return tiserBitMap;
    }

    public void setTiserBitMap(Bitmap tiserBitMap) {
        this.tiserBitMap = tiserBitMap;
    }
}
