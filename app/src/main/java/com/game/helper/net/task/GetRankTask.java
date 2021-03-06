package com.game.helper.net.task;

import com.game.helper.net.base.BaseBBXTask;
import com.game.helper.sdk.model.comm.GetRankBuild;
import com.game.helper.sdk.net.comm.GetRankNet;

import android.content.Context;

/**
 * @Description
 * @Path com.game.helper.net.task.GetRankTask.java
 * @Author lbb
 * @Date 2016年9月16日 上午9:56:34
 * @Company 
 */
public class GetRankTask extends BaseBBXTask{
	GetRankBuild build;
	public GetRankTask(Context context,boolean isShow,String userId,Back back) {
		super(context,isShow);
		this.back=back;
		build=new GetRankBuild(context, API_getRank_Url, userId);
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
		return new GetRankNet(context, build.toJson1());
	}
	
}