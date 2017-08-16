package com.game.helper.sdk.model.comm;

import android.content.Context;
import com.game.helper.sdk.model.base.BaseRequest;

public class GetMainPageInfoBuild extends BaseRequest{

	public GetMainPageInfoBuild(Context context, String url,String userId) {
		super(context, url);
		params=new Params();
		params.userId=userId;
	}
	public Params params;
	
	class Params{
		
		public String userId;
		
	}
}