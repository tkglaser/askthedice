package com.tkglaser.askthedice;

import java.io.FileNotFoundException;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class PictureList {
	
	private static PictureList _inst = null;
	
	public static PictureList getInst()
	{
		if (_inst == null)
		{
			_inst = new PictureList();
		}
		return _inst;
	}
	
	private String strCurrentFile = null;
	private Bitmap bmpCurrent = null;
 	private Random r = new Random();
 	private boolean bDecided = false;
 	private String sRoll = null;
 	private String sDecided = null;
 	
	private PictureList()
	{
	}
		
	public void save(Bundle b)
	{
		b.putBoolean("bDecided", bDecided);
		b.putString("strCurrentFile", strCurrentFile);
	}
	
	public void restore(Bundle b, Context c) throws FileNotFoundException
	{
		reset(c);
		if (b.containsKey("bDecided"))
			bDecided = b.getBoolean("bDecided");
		if (b.containsKey("strCurrentFile"))
			strCurrentFile = b.getString("strCurrentFile");
		
		bmpCurrent = null;
		if (bDecided)
			bmpCurrent = BitmapFactory.decodeStream(c.openFileInput(strCurrentFile));
	}
	
	public boolean hasBeenRolled()
	{
		return bDecided;
	}
	
	public void reset(Context c)
	{
		bDecided = false;
		bmpCurrent = null;
		sRoll = c.getString(R.string.string_roll_the_dice);
		sDecided = c.getString(R.string.string_decided);
	}
	
	public void roll(Context c) throws FileNotFoundException
	{
		String[] fileList = c.fileList();
		int idx = r.nextInt(fileList.length);
		strCurrentFile = fileList[idx];
		bmpCurrent = BitmapFactory.decodeStream(c.openFileInput(strCurrentFile));
		bDecided = true;
	}
	
	public Bitmap getPicture(Context c)
	{
		return bmpCurrent;
	}
	
	public String getString()
	{
		if (!bDecided)
		{
			return sRoll;
		}
		return sDecided;
	}
	
	public int count(Context c)
	{
		return c.fileList().length;
	}
	
	public boolean isEmpty(Context c)
	{
		return count(c) == 0;
	}
}
