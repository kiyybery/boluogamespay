package com.game.helper.net.task;

import android.content.Context;
import com.game.helper.net.base.BaseBBXTask;
import com.game.helper.sdk.model.comm.QueryMyGiftBuild;
import com.game.helper.sdk.net.comm.QueryMyGiftNet;

public class QueryMyGiftTask extends BaseBBXTask{
	QueryMyGiftBuild build;
	public QueryMyGiftTask(Context context,String userId,Back back) {
		super(context);
		this.back=back;
		build=new QueryMyGiftBuild(context, API_queryMyGift_Url, userId);
	}
	@Override
	public void onSuccess(Object object, String msg) {
		// TODO Auto-generated method stub
		if(back!=null){
			back.success(object,msg);
		}
	}

	@Override
	public void onFailed(String status, String msg, Object result) {
		// TODO Auto-generated method stub
		super.onFailed(status, msg, result);
       if(back!=null){
    	   back.fail(status, msg, result);
		}
	}

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		return new QueryMyGiftNet(context, build.toJson1());
	}
	
}
