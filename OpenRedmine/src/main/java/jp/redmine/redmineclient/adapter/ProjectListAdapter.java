package jp.redmine.redmineclient.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineProjectContract;

public class ProjectListAdapter extends CursorAdapter {
	private static final String TAG = ProjectListAdapter.class.getSimpleName();

	/**
	 * The listener that receives notifications when an item is clicked.
	 */
	OnFavoriteClickListener mOnFavoriteClickListener;

	/**
	 * Interface definition for a callback to be invoked when an item in this
	 * AdapterView has been clicked.
	 */
	public interface OnFavoriteClickListener {
		/**
		 * Callback method to be invoked when an item in this AdapterView has
		 * been clicked.
		 * <p>
		 * Implementers can call getItemAtPosition(position) if they need
		 * to access the data associated with the selected item.
		 *
		 * @param position The position of the view in the adapter.
		 * @param id The position of the cursor in the adapter.
		 * @param b Rating bar status.
		 */
		void onItemClick(int position, int id, boolean b);
	}

	public void setOnFavoriteClickListener(OnFavoriteClickListener mOnItemClickListener) {
		this.mOnFavoriteClickListener = mOnItemClickListener;
	}

	public ProjectListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	class ViewHolder {
		public TextView textSubject;
		public CheckBox ratingBar;
		public void setup(View view){
			textSubject = (TextView)view.findViewById(R.id.textSubject);
			ratingBar = (CheckBox)view.findViewById(R.id.checkStar);
			ratingBar.setFocusable(false);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = View.inflate(parent.getContext(), R.layout.listitem_project, null);
		ViewHolder holder = new ViewHolder();
		holder.setup(view);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, Context context, final Cursor cursor) {
		ViewHolder holder = (ViewHolder)view.getTag();
		int column_id = cursor.getColumnIndex(RedmineProjectContract._ID);
		int column_subject = cursor.getColumnIndex(RedmineProjectContract.NAME);
		int column_ratingbar = cursor.getColumnIndex(RedmineProjectContract.FAVORITE);
		final int position = cursor.getPosition();
		final int id = cursor.getInt(column_id);
		holder.textSubject.setText(cursor.getString(column_subject));
		holder.ratingBar.setOnCheckedChangeListener(null);
		holder.ratingBar.setChecked(cursor.getInt(column_ratingbar) > 0);
		holder.ratingBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mOnFavoriteClickListener == null)
					return;
				mOnFavoriteClickListener.onItemClick(position, id, isChecked);
			}
		});
	}

	public static int getProjectId(Cursor cursor){
		int column_id = cursor.getColumnIndex(RedmineProjectContract.PROJECT_ID);
		return cursor.getInt(column_id);
	}

	public static void updateFavorite(ContentResolver resolver,int id, boolean b){
		Uri uri = RedmineProjectContract.CONTENT_URI.buildUpon()
						.appendPath(String.valueOf(id))
						.build();
		ContentValues value = new ContentValues();
		value.put(RedmineProjectContract.FAVORITE, b ? 1 : 0);
		resolver.update(uri, value, null, null);

	}

	public static CursorLoader getCursorLoader(Context context, int connection_id){
		return new CursorLoader(context,
				RedmineProjectContract.CONTENT_URI, null
				, RedmineProjectContract.CONNECTION_ID + "=?"
				, new String[]{
					String.valueOf(connection_id)
				}, null
		);
	}
	public static Cursor getSearchQuery(ContentResolver resolver,int connection_id, CharSequence constraint){
		if(TextUtils.isEmpty(constraint)){
			return resolver.query(RedmineProjectContract.CONTENT_URI
					, null
					, RedmineProjectContract.CONNECTION_ID + "=?"
					, new String[]{
							String.valueOf(connection_id)
					}, null
			);
		} else {
			return resolver.query(RedmineProjectContract.CONTENT_URI
					, null
					, RedmineProjectContract.CONNECTION_ID + "=? AND " + RedmineProjectContract.NAME + " like ?"
					, new String[]{
							String.valueOf(connection_id)
							, "%" + constraint + "%"
					}, null
			);
		}
	}


}
