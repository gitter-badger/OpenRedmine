package jp.redmine.redmineclient;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
import android.os.Bundle;
import android.util.Log;

public class IssueViewActivity extends OrmLiteBaseActivity<DatabaseCacheHelper>  {
	public IssueViewActivity(){
		super();
	}
	private SelectDataTask task;
	private RedmineIssueViewForm form;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issueview);

		form = new RedmineIssueViewForm(this);


	}

	@Override
	protected void onStart() {
		super.onStart();
		IssueIntent intent = new IssueIntent(getIntent());
		int connectionid = intent.getConnectionId();
		int issueid = intent.getIssueId();

		RedmineIssueModel model = new RedmineIssueModel(getHelper());
		RedmineIssue issue = new RedmineIssue();
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connectionid, issueid);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		form.setValue(issue);

		task = new SelectDataTask();
		task.execute(issueid);
	}

	private class SelectDataTask extends SelectIssueJournalTask{
		public SelectDataTask() {
			super();
			helper = getHelper();
			IssueIntent intent = new IssueIntent(getIntent());
			int connectionid = intent.getConnectionId();
			ConnectionModel mConnection = new ConnectionModel(getApplicationContext());
			connection = mConnection.getItem(connectionid);
			mConnection.finalize();
		}

	}
}
