package axlecho.memo;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.axlecho.memo.R;

public class ShowActivity extends SherlockActivity {
	private ImageView imageView;
	private TextView noteView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TODO 适应横竖屏幕
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_show);
		String pic_path = (String) getIntent().getStringExtra("pic_path");
		imageView = (ImageView) findViewById(R.id.view_image);
		imageView.setImageBitmap(BitmapFactory.decodeFile(pic_path));

		String note = (String) getIntent().getStringExtra("note");
		noteView = (TextView) findViewById(R.id.view_note);
		noteView.setText(note);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.show, menu);
		return true;
	}
}
