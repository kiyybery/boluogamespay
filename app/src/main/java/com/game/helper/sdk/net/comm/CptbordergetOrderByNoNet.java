package com.game.helper.sdk.net.comm;

import java.io.InputStream;
import com.game.helper.sdk.model.returns.CptbordergetOrderByNo;
import com.game.helper.sdk.net.base.BaseNetwork;
import com.game.helper.sdk.net.base.GeneralParser;
import android.content.Context;

/**
 * @Description
 * @Path com.game.helper.sdk.net.comm.CptbordergetOrderByNoNet.java
 * @Author lbb
 * @Date 2016年11月30日 下午2:42:32
 * @Company 
 */
public class CptbordergetOrderByNoNet extends BaseNetwork{

	public CptbordergetOrderByNoNet(Context context,  String datas) {
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
		return parser.getData(CptbordergetOrderByNo.class) ;
	}

}
