package com.orange.phonemanage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orange.peoplemanage.ContactInfo;
import com.orange.peoplemanage.PeopleInfo;

@SuppressLint("NewApi")
public class Main extends Activity {

	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	private static final int PHONES_NUMBER_INDEX = 1;
	private ViewPager mPager;// ҳ������
	private List<View> listViews; // Tabҳ���б�
	private ImageView cursor;// ����ͼƬ
	private TextView t1, t2;// ҳ��ͷ��
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���

	private MyAdapter adapter;// ��������������
	// �����б���ͼ����
	private List<Map<String, Object>> list;// �����б�����
	public static Main ma;

	private ScrollView sv = null;

	private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	private ArrayList<ContactInfo> contactList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ����ֻ������е���ϵ��
		contactList = new ArrayList<ContactInfo>();
		getPhoneContacts();

		ma = this;

		// �Զ��岼��
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0; i < contactList.size(); i++) {
			ListView listView = new ListView(this);
			ContactInfo contactInfo = contactList.get(i);

			// ʵ�����б�����
			list = new ArrayList<Map<String, Object>>();

			// ʵ��һ���б���������
			Map<String, Object> map = new HashMap<String, Object>();
			// ���б��������������
			map.put("item1_imageivew",R.drawable.p_pic);
			map.put("item1_bigtv", contactInfo.getC_name());

			// ���б�������ӵ��б�������
			list.add(map);
			// --ʹ��ϵͳ���������޷�ʵ�����������
			// //ʵ��������
			adapter = new MyAdapter(this, list, R.layout.mylistview,
					new String[] { "item1_imageivew", "item1_bigtv" },
					new int[] { R.id.iv, R.id.bigtv }, contactInfo);
			listView.setAdapter(adapter);

			layout.addView(listView, 320, 50);
		}

		// ��ʾ�б���ͼ
		sv = new ScrollView(this);
		sv.setLayoutParams(LP_FF);
		sv.addView(layout);

		setContentView(R.layout.activity_main);

		InitImageView();
		InitTextView();
		InitViewPager();

	}

	// ȡ���ֻ���ϵ����Ϣ
	public void getPhoneContacts() {
		ContentResolver resolver = this.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// get phonenumber
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// IF IS NULL CONTINUE
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;

				}
				// get name
				String contactName = phoneCursor.getString(0);
                
				Long contactid=phoneCursor.getLong(3);
				Long photoid = phoneCursor.getLong(2);
				// get the photo
				Bitmap contactPhoto = null;
				if (photoid > 0) {
                     Uri uri=ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                     InputStream input=ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                     contactPhoto=BitmapFactory.decodeStream(input);
                     
				}
				else{
					contactPhoto= BitmapFactory.decodeResource(getResources(), R.drawable.p_pic);
				}

				// System.out.println(contactName+phoneNumber);
				ContactInfo contactInfo = new ContactInfo();
				contactInfo.setC_name(contactName);
				contactInfo.setC_phone(phoneNumber);
				contactInfo.setC_photo(contactPhoto);
				contactList.add(contactInfo);
				
			}
		}
		phoneCursor.close();
	}

	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));

	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(sv);
		listViews.add(mInflater.inflate(R.layout.lay2, null));
		listViews.add(mInflater.inflate(R.layout.lay3, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * ��ʼ������
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 2 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// ���ö�����ʼλ��
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * ҳ���л�����
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
		int two = one * 2;// ҳ��1 -> ҳ��3 ƫ����

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	// The menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 1, 1, "Add");
		// menu.add(0,2,2,"Save");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		if (item.getItemId() == 1) {
			Intent intent = new Intent(Main.this, PeopleInfo.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", "new");
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (item.getItemId() == 2) {

		}
		return super.onOptionsItemSelected(item);
	}

}
