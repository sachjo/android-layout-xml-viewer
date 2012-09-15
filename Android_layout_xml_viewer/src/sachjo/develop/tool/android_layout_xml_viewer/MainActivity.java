package sachjo.develop.tool.android_layout_xml_viewer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	String[] layoutNameArray;
	Integer[] layoutIdArray;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			EditText editText = (EditText) findViewById(R.id.editText1);
			String xmlText = editText.getText().toString();

			if (xmlText.isEmpty()) {
				// ダイアログの表示
				AlertDialog.Builder dlg;
				dlg = new AlertDialog.Builder(this);
				dlg.setTitle("String Empty");
				dlg.setMessage("Select from list or input text.");
				dlg.show();
			} else {
				InputStream bais = null;
				try {
					bais = new ByteArrayInputStream(xmlText.getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				XmlPullParserFactory factory;
				try {
					factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					// XmlPullParser parser = factory.newPullParser();
					// xpp.setInput( new StringReader (concat) );

					XmlPullParser parser = Xml.newPullParser();
					try {
						parser.setInput(bais, "utf-8");
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						LayoutInflater orgLayInf = this.getLayoutInflater();
						LayoutInflater2 layInf = new LayoutInflater2(orgLayInf,this,metrics);
						View view = layInf.inflate(parser, null);
						setContentView(view);
//						setContentView(this.getLayoutInflater().inflate(parser, null));
					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						// ダイアログの表示
						AlertDialog.Builder dlg;
						dlg = new AlertDialog.Builder(this);
						dlg.setTitle("inflate error");
						dlg.setMessage(e.getMessage());
						dlg.show();
					}
				} catch (XmlPullParserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;
		case R.id.button2:
			Resources res = getResources();
			XmlResourceParser resParser = res.getLayout(layoutIdArray[0]);
			setContentView(this.getLayoutInflater().inflate(resParser, null));
			Button mGoBtn = (Button) findViewById(R.id.button1);
			mGoBtn.setOnClickListener(this);
			Button mGoBtn2 = (Button) findViewById(R.id.button2);
			mGoBtn2.setOnClickListener(this);
			Button mGoBtn3 = (Button) findViewById(R.id.button3);
			mGoBtn3.setOnClickListener(this);
			break;
		case R.id.button3:
			onCreate(null);
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_list);

		Button mGoBtn = (Button) findViewById(R.id.button1);
		mGoBtn.setOnClickListener(this);
		Button mGoBtn2 = (Button) findViewById(R.id.button2);
		mGoBtn2.setOnClickListener(this);
		Button mGoBtn3 = (Button) findViewById(R.id.button3);
		mGoBtn3.setOnClickListener(this);
		// String[] layoutList = new String[];
		java.lang.reflect.Field[] fields = R.layout.class.getFields();
		List<String> layoutNameList = new ArrayList<String>();
		List<Integer> layoutIdList = new ArrayList<Integer>();
		R.layout layoutInstance = new R.layout();
		for (Field field : fields) {
			layoutNameList.add(field.getName());
			try {
				layoutIdList.add(field.getInt(layoutInstance));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		layoutNameArray = layoutNameList.toArray(new String[0]);
		layoutIdArray = layoutIdList.toArray(new Integer[0]);
		// String[] layoutNameArray = {"test1","test2"};
		ListView listView = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list, layoutNameArray);
		// Log.d("test",layoutNameArray[0]);
		listView.setAdapter(adapter);
		// リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Log.d("test", "clicked at " + position);
		Resources res = getResources();

		// EditText editText = (EditText) findViewById(R.id.editText1);

		// TypedValue outData = new TypedValue();
		// res.getValue(layoutIdArray[position],outData,true);
		// editText.setText(outData.toString());

		// editText.setText(res.getText(layoutIdArray[position]));

		// InputStream in = res.openRawResource(layoutIdArray[position]);
		// BufferedReader reader;
		// try {
		// reader = new BufferedReader(new InputStreamReader(in,
		// "UTF-8"/* 文字コード指定 */));
		// StringBuffer buf = new StringBuffer();
		// String str = null;
		// while ((str = reader.readLine()) != null) {
		// buf.append(str);
		// }
		// editText.setText(str);
		// } catch (UnsupportedEncodingException e) {
		// // TODO 自動生成された catch ブロック
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO 自動生成された catch ブロック
		// e.printStackTrace();
		// }
		// try {
		// in.close();
		// } catch (IOException e) {
		// // TODO 自動生成された catch ブロック
		// e.printStackTrace();
		// }

		XmlResourceParser resParser = res.getLayout(layoutIdArray[position]);
		int eventType;
		String TAG = "resParseTest";
		String outStr = "";
		int nestNum = -1;
		boolean isInTag = false;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("http://schemas.android.com/apk/res/android", "android");
		map.put("http://schemas.android.com/tools", "tools");
		try {
			eventType = resParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
					Log.e(TAG, "Start document");
				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					Log.e(TAG, "End document");
				} else if (eventType == XmlPullParser.START_TAG) {
					Log.e(TAG, "Start tag " + resParser.getName());
					if (isInTag) {
						for (int i = 0; i < nestNum; i++) {
							outStr += "    ";
						}
						outStr += " >\n";
						nestNum++;
						for (int i = 0; i < nestNum; i++) {
							outStr += "    ";
						}
					} else {
						nestNum++;
						for (int i = 0; i < nestNum; i++) {
							outStr += "    ";
						}
						isInTag = true;
					}
					String name = resParser.getName();
					if (name.equals("RelativeLayout") || name.equals("Button")
							|| name.equals("ListView")
							|| name.equals("EditText")) {
						name = "android.widget." + name;
					}
					outStr += "<" + name;
					if (nestNum == 0) {
						for (String s : map.keySet()) {
							outStr += " xmlns:" + map.get(s) + "=\"" + s
									+ "\"\n";
						}
					}
					// for (int i =
					// resParser.getNamespaceCount(resParser.getDepth()) -1; i
					// >= 0; i--) {
					// map.put(resParser.getNamespaceUri(i),
					// resParser.getNamespacePrefix(i));
					// }
					int num = resParser.getAttributeCount();
					String msg = "";
					for (int j = 0; j < num; j++) {
						// if(map.containsKey(resParser.getAttributeNamespace(j)))
						// {
						// msg = "\"" +
						// map.get(resParser.getAttributeNamespace(j));
						// } else {
						// msg = "\"" + resParser.getAttributeNamespace(j);
						//
						// }
						// msg += "\":"
						msg += "" + resParser.getAttributeName(j) + ","
								+ resParser.getAttributeType(j) + ","
								+ resParser.getAttributeValue(j) + "\n";
						Log.e(TAG, "Tag Attribute " + msg);
						if (map.containsKey(resParser.getAttributeNamespace(j))) {
							outStr += " "
									+ map.get(resParser
											.getAttributeNamespace(j));
						} else {
							outStr += " " + resParser.getAttributeNamespace(j);
						}
						// outStr += " {" + resParser.getAttributeNamespace(j) +
						// "}";
						outStr += ":" + resParser.getAttributeName(j) + "=\""
								+ resParser.getAttributeValue(j) + "\"\n";
						for (int i = 0; i < nestNum; i++) {
							outStr += "    ";
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					Log.e(TAG, "End tag " + resParser.getName());
					for (int i = 0; i < nestNum; i++) {
						outStr += "    ";
					}
					nestNum--;
					if (isInTag) {
						outStr += "/>\n";
						isInTag = false;
					} else {
						//outStr += "</" + resParser.getName() + ">\n";

						String name = resParser.getName();
						if (name.equals("RelativeLayout") || name.equals("Button")
								|| name.equals("ListView")
								|| name.equals("EditText")) {
							name = "android.widget." + name;
						}
						outStr += "</" + name + ">\n";
					}
				} else if (eventType == XmlPullParser.TEXT) {
					Log.e(TAG, "Text " + resParser.getText());
				}
				eventType = resParser.next();
			}
			EditText editText = (EditText) findViewById(R.id.editText1);
			editText.setText(outStr);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
