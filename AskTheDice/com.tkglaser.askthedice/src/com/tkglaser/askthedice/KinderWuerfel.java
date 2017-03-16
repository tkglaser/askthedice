package com.tkglaser.askthedice;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class KinderWuerfel extends SherlockActivity implements OnClickListener,
	ImageSwitcher.ViewFactory
{

	TextView		tv		= null;
	ImageSwitcher	imgSw	= null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		tv = (TextView) findViewById(R.id.tvResult);
		imgSw = (ImageSwitcher) findViewById(R.id.MainBackground);

		//img.setClickable(true);
		imgSw.setOnClickListener(this);
		imgSw.setFactory(this);
		
		imgSw.setInAnimation(this, R.anim.blow_in);
		imgSw.setOutAnimation(this, R.anim.blow_out);
		
		reset();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		try
		{
			PictureList.getInst().restore(savedInstanceState, this);
		}
		catch (FileNotFoundException e)
		{
		}
		updateGUI();
	}

	protected void updateGUI()
	{
		try
		{
			Bitmap b = PictureList.getInst().getPicture(this);
			//img.setImageBitmap(b);
			tv.setText(PictureList.getInst().getString());
			
			if (b == null)
			{
				((ImageView) imgSw.getNextView()).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imgSw.setImageResource(R.drawable.mainbackground);
			}
			else
			{
				((ImageView) imgSw.getNextView()).setScaleType(ImageView.ScaleType.FIT_CENTER);
				imgSw.setImageDrawable(new BitmapDrawable(getResources(), b));
			}
			
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		PictureList.getInst().save(outState);
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		pictureListIsEmpty();
	}

	protected boolean pictureListIsEmpty()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (PictureList.getInst().isEmpty(this))
		{
			builder.setTitle(R.string.welcome)
				   .setMessage(R.string.lets_take_pics)
			       .setCancelable(false)
			       .setPositiveButton(R.string.lets_go, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   TakePicture(null);
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
		else if (PictureList.getInst().count(this) == 1)
		{
			builder.setTitle(R.string.nearly_there)
			   .setMessage(R.string.take_another_pic)
		       .setCancelable(false)
		       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		return false;
	}

	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		reset();
	}

	private void reset()
	{
		PictureList.getInst().reset(this);
		updateGUI();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
	   inflater.inflate(R.menu.main_menu, (com.actionbarsherlock.view.Menu) menu);
	   return super.onCreateOptionsMenu(menu);
	}

	private void showRandomPicture()
	{
		if (pictureListIsEmpty())
		{
			return;
		}

		try
		{
			PictureList.getInst().roll(this);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
		}
		updateGUI();
	}
	
	int CAMERA_PIC_REQUEST = 2; 
	
	public void TakePicture(View v)
	{
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == CAMERA_PIC_REQUEST && data != null && data.getExtras().containsKey("data"))
	    {
	        Bitmap bitmapimage = (Bitmap) data.getExtras().get("data");
			FileOutputStream fos;
			try {
				String filename = UUID.randomUUID().toString() + ".jpg";
				fos = openFileOutput(filename, Context.MODE_PRIVATE);
				bitmapimage.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
			}
	    }
	    else 
	    {
	        Toast.makeText(getBaseContext(), R.string.no_picture_taken, Toast.LENGTH_LONG).show();
	    }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_take_picture:
			TakePicture(null);
			return true;
		case R.id.menu_item_manager:
			startActivity(new Intent(this.getBaseContext(), ImageManager.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v)
	{
		if (!PictureList.getInst().hasBeenRolled())
		{
			showRandomPicture();
		}
		else
		{
			reset();
		}
	}

	@Override
	public View makeView()
	{
		ImageView v = new ImageView(this);
		
		int paddip = (int) (getResources().getDisplayMetrics().density * 25f);
		
		v.setPadding(paddip, paddip, paddip, paddip);
        v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        v.setLayoutParams(new ImageSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return v;

	}
}