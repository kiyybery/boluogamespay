package com.game.helper.sdk.net.comm;

import java.io.InputStream;
import com.game.helper.sdk.model.returns.QueryGameCutPl;
import com.game.helper.sdk.net.base.BaseNetwork;
import com.game.helper.sdk.net.base.GeneralParser;
import android.content.Context;

/**
 * @Description
 * @Path com.game.helper.sdk.net.comm.QueryGameCutPlNet.java
 * @Author lbb
 * @Date 2016年10月12日 上午10:15:23
 * @Company 
 */
public class QueryGameCutPlNet extends BaseNetwork{

	public QueryGameCutPlNet(Context context,  String datas) {
		super(context, false);
		url =BASE_URL;
		InputStream inputStream = getContentWithPOST(datas);
		if (inputStream != null) {
			parser = new GeneralParser(context, inputStream);
		}
	}

	@Override
	public Object getData() {
		if(parser==null){
			return null;
		}
		return parser.getData(QueryGameCutPl.class) ;
	}

}
