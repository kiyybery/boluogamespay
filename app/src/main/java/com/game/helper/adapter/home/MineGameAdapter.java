package com.game.helper.adapter.home;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.game.helper.BaseActivity;
import com.game.helper.BaseApplication;
import com.game.helper.R;
import com.game.helper.activity.WebActivity;
import com.game.helper.activity.home.GameDetailActivity;
import com.game.helper.db.manager.DBManager;
import com.game.helper.download.bean.AppContent;
import com.game.helper.installPackage.MonitorSysReceiver;
import com.game.helper.leopardkit.DownLoadModel;
import com.game.helper.net.base.BaseBBXTask.Back;
import com.game.helper.net.task.DownloadFinishTask;
import com.game.helper.sdk.model.returns.LoginData;
import com.game.helper.util.FileUtil;
import com.game.helper.util.ToastUtil;
import com.game.helper.view.widget.XCRoundImageViewByXfermode;
import com.yuan.leopardkit.LeopardHttp;
import com.yuan.leopardkit.db.HttpDbUtil;
import com.yuan.leopardkit.download.DownLoadManager;
import com.yuan.leopardkit.download.model.DownloadInfo;
import com.yuan.leopardkit.interfaces.IProgress;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * @Description
 * @Path com.game.helper.adapter.home.MineGameAdapter.java
 * @Author lbb
 * @Date 2016年8月23日 上午9:12:21
 * @Company
 */
public class MineGameAdapter  extends BaseAdapter {
	/*public  interface MOnItemClickListener2{
		void onItemClick(int position);
	}
	MOnItemClickListener2 l;
	public void setOnItemClickListener(MOnItemClickListener2 l){
		this.l=l;
	}*/
	 private List<View> viewList= new ArrayList<View>();//View对象集合
	List<DownLoadModel> mList = new ArrayList<DownLoadModel>();
	//private List<AppContent> mList=new ArrayList<AppContent>();
	private Context mContext;
	protected LayoutInflater mInflater;
	int listviewId;
	int tag;
	public MineGameAdapter(Context mContext,List<AppContent> mList,int tag,int listviewId) {//tag=2时不能直接操作下载游戏
		super();
		mInflater = LayoutInflater.from(mContext);
		this.mContext=mContext;
		this.tag=tag;
		this.listviewId=listviewId;

		if (null == mList || mList.size() == 0) {
			//ToastUtil.showToast(mContext,"数据错误");
			return;
		} else {
			init(mList);
		}
		//init(mList);
	}

	public void init(List<AppContent> mLists){
		viewList.clear();
		mList.clear();
		DownLoadModel model=null;
		for(AppContent mAppContent:mLists){
			DownloadInfo info = new DownloadInfo();
			info.setUrl(mAppContent.downloadPath);
			info.setProgress(0L);
			info.setFileName("IRecordApp_" + mAppContent.gameId+ ".apk");

			model = new DownLoadModel();
			model.setInfo(info);
			model.setmAppContent(mAppContent);
			mList.add(model);
		}
		List<DownloadInfo> downloadInfoList =   HttpDbUtil.initHttpDB(mContext).queryFileInfo(0);

		for(DownLoadModel mD:mList){
			if(mD!=null&&mD.getInfo()!=null
					&&!TextUtils.isEmpty(mD.getInfo().getFileName())){
				for (DownloadInfo info:downloadInfoList){
					if(info!=null){
						if(mD.getInfo().getFileName().equals(info.getFileName())){
							mD.setInfo(info);
							break;
						}
					}
				}
			}
		}
	}


	public List<DownLoadModel> getmList() {
		return mList;
	}


	public void setmList(final List<AppContent> mList) {
//			Executors.newSingleThreadExecutor().execute(new Runnable() {
//				@Override
//				public void run() {
					init(mList);
					((Activity)mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							notifyDataSetChanged();
						}
					});
//				}
//			});
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public DownLoadModel getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_listview_home_mine_game, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_open.setTag(position);

		final DownLoadModel mDownLoadModel=(DownLoadModel) getItem(position);
		DownloadInfo info =mDownLoadModel.getInfo();
		holder.setData(mDownLoadModel.getmAppContent(),info,position);
		if(MineGameAdapter.this.tag!=2){
			holder.mLinearClick.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle bundle=new Bundle();
					bundle.putInt("currIndex", 0);
					bundle.putString("gameId", getItem(position).getmAppContent().gameId);
					bundle.putParcelable("appcontent", getItem(position).getmAppContent());
					((BaseActivity)mContext).startActivity(GameDetailActivity.class,bundle);
				}
			});
		}
		/* 标识View对象 */
        //将list_view的ID作为Tag的Key值
//        convertView.setTag(listviewId, position);//此处将位置信息作为标识传递
//        viewList.add(convertView);
		return convertView;
	}

	class ViewHolder {
		public @BindView(R.id.mLinearClick) LinearLayout mLinearClick;

		public @BindView(R.id.iv_item) XCRoundImageViewByXfermode iv_item;
		public @BindView(R.id.mLinear_dz) LinearLayout mLinear_dz;
		public @BindView(R.id.tv_dz) TextView tv_dz;

		public @BindView(R.id.tv_item) TextView tv_item;
		public @BindView(R.id.tv_first) TextView tv_first;
		public @BindView(R.id.tv_gift) TextView tv_gift;

		public @BindView(R.id.item_mLinearMsg) LinearLayout item_mLinearMsg;
		public @BindView(R.id.tv_type) TextView tv_type;
		public @BindView(R.id.tv_datasize) TextView tv_datasize;

		public @BindView(R.id.tv_msg) TextView tv_msg;
		public @BindView(R.id.tv_fromdownload) TextView tv_fromdownload;
		public @BindView(R.id.pb_update_progress) ProgressBar pb_update_progress;
		public @BindView(R.id.tv_open)
		Button tv_open;

		public @BindView(R.id.mLinear_progress) LinearLayout mLinear_progress;
		public @BindView(R.id.down_txt_pb) TextView prgressTv;
		public @BindView(R.id.down_progress) TextView progressShow;

		public @BindView(R.id.tv_first3) TextView tv_first3;
		public AppContent mAppContent;
		public DownloadInfo info;
		private ViewHolder mViewHolder;
		private DownLoadModel mDownloadModel = new DownLoadModel();
		private int mPostion = -1;

		Handler handler=new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
					case 0x001:
						if (DownLoadManager.getManager().isDowLoading(info)){//有下载中的任务,并且点击的不是下载总的
							ToastUtil.showToast(mContext,"已有游戏在下载中 请稍后...");
							return false;
						}else{
							doDowlod();
						}
						break;

				}

				return false;
			}
		});

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
		@SuppressWarnings("unchecked")
		public void setData(final AppContent mAppContent, final DownloadInfo info,final Integer  postion){
			this.mAppContent=mAppContent;
			this.info=info;
			mPostion = postion;
			if(mAppContent!=null){
				iv_item.setType(XCRoundImageViewByXfermode.TYPE_ROUND);
				iv_item.setRoundBorderRadius(23);
				mLinear_dz.setVisibility(View.GONE);
				if(!TextUtils.isEmpty(mAppContent.sczk)&&Double.parseDouble(mAppContent.sczk)>0){
					mLinear_dz.setVisibility(View.VISIBLE);
					tv_dz.setText(""+mAppContent.sczk+"折");
				}else if(!TextUtils.isEmpty(mAppContent.xczk)&&Double.parseDouble(mAppContent.xczk)>0){
					mLinear_dz.setVisibility(View.VISIBLE);
					tv_dz.setText(""+mAppContent.xczk+"折");
				}

				ViewTarget viewTarget = new ViewTarget<XCRoundImageViewByXfermode, GlideDrawable>( iv_item ) {
					@Override
					public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
						this.view.setImageDrawable( resource.getCurrent() );
					}
				};
				Glide
				.with( BaseApplication.mInstance.context.getApplicationContext() ) // safer!
				.load( ""+mAppContent.fileAskPath+mAppContent.logo )
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.into( viewTarget );

				tv_item.setText(mAppContent.gameName);
				tv_type.setText(mAppContent.kindName);
				if(!TextUtils.isEmpty(mAppContent.fileSize)){
					tv_datasize.setText(""+mAppContent.fileSize+"M");
				}else{
					tv_datasize.setText("0.0"+"M");
				}
				tv_msg.setText(mAppContent.intro);
				tv_fromdownload.setText(mAppContent.platName);
				if((!TextUtils.isEmpty(mAppContent.typeId)&&mAppContent.typeId.equals("3"))
						||(!TextUtils.isEmpty(mAppContent.typeName)&&mAppContent.typeName.equals("H5游戏"))){
					//tv_open.setText("打开");
					tv_open.setText("");
					tv_open.setBackgroundResource(R.drawable.home_open);
					tv_open.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Bundle bundle=new Bundle();
							bundle.putString("Url",mAppContent.downloadPath);
							bundle.putString("TITLE", mAppContent.gameName);
							((BaseActivity)mContext).startActivity(WebActivity.class,bundle);
						}
					});
					return;
				}
			}
			setByStatus(info.getState());

			if(info.getFileLength()!=0){
				int cur_progress = (int) ((float) info.getProgress() / info.getFileLength() * 100);
				pb_update_progress.setMax((int) info.getFileLength());
				pb_update_progress.setProgress((int) info.getProgress());
				progressShow.setText(cur_progress + "%");
				if (info.getState() == DownLoadManager.STATE_FINISH) {
					if(cur_progress == 100){
						tv_open.setText("安装");
						tv_open.setBackgroundResource(R.drawable.apk_state);
					}else{
						//tv_open.setText("下载");
						tv_open.setText("");
						tv_open.setBackgroundResource(R.drawable.home_install);
						info.setState(DownLoadManager.STATE_WAITING);
						setByStatus(DownLoadManager.STATE_WAITING);
						if(info.getDownLoadTask()==null){
                        	DownloadInfo mDownloadInfo=DownLoadManager.getManager().getDownloadInfo(info);
                        	if(mDownloadInfo!=null){
                        		info.setDownLoadTask(mDownloadInfo.getDownLoadTask());
                        		info.getDownLoadTask().setState(DownLoadManager.STATE_WAITING);
                        	}
                        }else{
                        	info.getDownLoadTask().setState(DownLoadManager.STATE_WAITING);
                        }
						//info.getDownLoadTask().pause();
					}
				}
				/*prgressTv.setText(Double.toString(getRealNum(info.getProgress()/1024/1024)) + "MB/"
							+ Double.toString(getRealNum( info.getFileLength()/1024/1024)) + "MB"+"");*/
				double curP = 0;
				double curTotal = 0;
				long progress=info.getProgress();
				long total=info.getFileLength();
				if (progress / 1024L < 1024L) {
					curP = (double) progress / 1024;
					curTotal = (double) total / 1024L / 1024L;
					prgressTv.setText(Double.toString(getRealNum(curP)) + "KB/" + Double.toString(getRealNum(curTotal)) + "MB"
							);
				} else {
					curP = (double) progress / 1024L / 1024L;
					curTotal = (double) total / 1024L / 1024L;
					prgressTv.setText(Double.toString(getRealNum(curP)) + "MB/" + Double.toString(getRealNum(curTotal)) + "MB"
							);
				}
			}else{
				pb_update_progress.setMax(0);
				pb_update_progress.setProgress(100);
				progressShow.setText( 0 + "%");
			}
			if (info.getState() == DownLoadManager.STATE_WAITING) {
				//tv_open.setText("下载");
				tv_open.setText("");
				tv_open.setBackgroundResource(R.drawable.home_install);
				if(MonitorSysReceiver.checkApkExist(mContext,mAppContent.packageName , mAppContent.gameId)){
					//tv_open.setText("打开");
					tv_open.setText("");
					tv_open.setBackgroundResource(R.drawable.home_open);
					info.setState(DownLoadManager.AlreadyInstalled);
					setByStatus(DownLoadManager.AlreadyInstalled);
				}
			}else
			if (info.getState() == DownLoadManager.STATE_PAUSE) {
				tv_open.setText("继续");
				tv_open.setBackgroundResource(R.drawable.apk_state);
			}else
			if (info.getState() == DownLoadManager.STATE_DOWNLOADING) {
				tv_open.setText("暂停");
				tv_open.setBackgroundResource(R.drawable.apk_state);
			}else
			if (info.getState() == DownLoadManager.STATE_ERROR) {
				tv_open.setText("等待");
				tv_open.setBackgroundResource(R.drawable.apk_state);
			}else
			if (info.getState() == DownLoadManager.AlreadyInstalled) {
				//tv_open.setText("打开");
				tv_open.setText("");
				tv_open.setBackgroundResource(R.drawable.home_open);
			}


           if(MineGameAdapter.this.tag!=2){
//			   tv_open.setOnTouchListener(new View.OnTouchListener() {
//				   @Override
//				   public boolean onTouch(View v, MotionEvent event) {
//					   switch (event.getAction()) {
//						   case MotionEvent.ACTION_DOWN://按下按钮
//							   //Log.e(TGA, "Item_ACTION_DOWN");
//							   Message msg=new Message();
//							   msg.what=0x001;
//							   handler.sendMessage(msg);
//							   mViewHolder = ViewHolder.this;
//							   break;
//					   }
//					   return false;
//				   }
//			   });

        	  tv_open.setOnClickListener(new OnClickListener() {

        		   public void onClick(View v){
					   //Log.e(TGA, "Item_ACTION_DOWN");
					   Message msg=new Message();
					   msg.what=0x001;
					   handler.sendMessage(msg);
				   }
        	   });

           }

		}

		private void doDowlod() {
			BaseApplication.mInstance.isRecommendBoutiqueAdapter=4;
			DBManager.getInstance(mContext).insertDownloadFile(mAppContent);
			if( info.getDownLoadTask()==null &&(DownLoadManager.getManager().isHaved(info)==false||DownLoadManager.getManager().getDownloadInfo(info)==null)){
				final DownLoadModel mDown=new DownLoadModel();
				mDown.setmAppContent(mAppContent);
				//add task
				LeopardHttp.DWONLOAD(info, new IProgress() {
					@SuppressLint("NewApi")
					@Override
					public void onProgress(final long progress, final long total, final boolean done) {

						if(null != info.getDownLoadTask()){
							DownloadInfo infos = info.getDownLoadTask().getDownloadInfo();
							mDown.setInfo(infos);
							Log.e("lbb", "------getState---4---"+infos.getState());

//							setData(mAppContent,info,mPostion);
							EventBus.getDefault().post(mDown);

							if (done&&((int) ((float) progress / total * 100))==100) {
								LoginData user=DBManager.getInstance(mContext).getUserMessage();
								new DownloadFinishTask(mContext, user.userId, mAppContent.gameId, new Back() {

									@Override
									public void success(Object object, String msg) {
										// TODO Auto-generated method stub

									}

									@Override
									public void fail(String status, String msg, Object object) {
										// TODO Auto-generated method stub

									}
								}).start();
								if(MonitorSysReceiver.checkApkExist(mContext,mAppContent.packageName , mAppContent.gameId)){
											/*PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
											if(packageManager!=null){
												Intent intent=new Intent();
												intent =packageManager.getLaunchIntentForPackage(mAppContent.packageName);
												if(intent!=null){
													mContext.startActivity(intent);
												}
											}*/
								}else{

									// 下载完成后弹出安装窗
									File file = new File(DownLoadManager.getManager().deFaultDir+ infos.getFileName());
//									Intent intentInstall = new Intent();
//									intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//									intentInstall.setAction(Intent.ACTION_VIEW);
//									intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//									mContext.startActivity(intentInstall);
									FileUtil.apkInstall(file,mContext);
								}
    								   /*viewHolder.tv_open.setText("安装");
    								   infos.getDownLoadTask().setState(DownLoadManager.STATE_FINISH);
    								   viewHolder.prgressTv.setText(viewHolder.prgressTv.getText() + "");*/
							}
							View view = null;
        						   /* 匹配视图对象 */
        						   /*for (int i = 0; i < viewList.size(); i++) {
        							   if (viewList.get(i).getTag(listviewId) == postion) {
        								   //检查所有视图ID，如果ID匹配则取出该对象
        								   view = viewList.get(i);
        								   break;
        							   }
        						   }*/
							if (view != null) {}
						}

					}
				},mContext);
			}
			if(info.getDownLoadTask()==null){
				DownloadInfo mDownloadInfo=DownLoadManager.getManager().getDownloadInfo(info);
				if(mDownloadInfo!=null){
					info.setDownLoadTask(mDownloadInfo.getDownLoadTask());
				}
			}

			DownloadInfo infos = info.getDownLoadTask().getDownloadInfo();
//			DownloadInfo info = data.get(Integer.valueOf(v.getTag().toString())).getInfo();
			if (infos.getState() == DownLoadManager.STATE_FINISH) {
				if(((int) ((float) infos.getProgress() / infos.getFileLength() * 100))==100){
					//tv_open.setText("打开");
					tv_open.setText("");
					tv_open.setBackgroundResource(R.drawable.home_open);
					info.setState(DownLoadManager.AlreadyInstalled);
					infos.setState(DownLoadManager.AlreadyInstalled);
					setByStatus(DownLoadManager.AlreadyInstalled);
					if(infos.getDownLoadTask()==null){
						DownloadInfo mDownloadInfo=DownLoadManager.getManager().getDownloadInfo(infos);
						if(mDownloadInfo!=null){
							infos.setDownLoadTask(mDownloadInfo.getDownLoadTask());
							infos.getDownLoadTask().setState(DownLoadManager.AlreadyInstalled);
						}else{
							HttpDbUtil.initHttpDB(mContext).update(infos);
						}

					}else{
						infos.getDownLoadTask().setState(DownLoadManager.AlreadyInstalled);
					}
					//infos.getDownLoadTask().reStart();
					if(MonitorSysReceiver.checkApkExist(mContext,mAppContent.packageName , mAppContent.gameId)){
						PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
						if(packageManager!=null){
							Intent intent=new Intent();
							intent =packageManager.getLaunchIntentForPackage(mAppContent.packageName);
							if(intent!=null){
								mContext.startActivity(intent);
							}
						}
					}else{

						// 下载完成后弹出安装窗
						File file = new File(DownLoadManager.getManager().deFaultDir+ infos.getFileName());
//						Intent intentInstall = new Intent();
//						intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intentInstall.setAction(Intent.ACTION_VIEW);
//						intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//						mContext.startActivity(intentInstall);
						FileUtil.apkInstall(file,mContext);
					}
					DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
					DownLoadModel mDown=new DownLoadModel();
					mDown.setmAppContent(mAppContent);
					mDown.setInfo(infoss);
					EventBus.getDefault().post(mDown);
					return;
				}else{
					//去重新下载
					tv_open.setText("暂停");
					tv_open.setBackgroundResource(R.drawable.apk_state);
					setByStatus(DownLoadManager.STATE_DOWNLOADING);
					infos.getDownLoadTask().reStart();

					DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
					DownLoadModel mDown=new DownLoadModel();
					mDown.setmAppContent(mAppContent);
					mDown.setInfo(infoss);
					EventBus.getDefault().post(mDown);
					return;
				}
			}
			else if (infos.getState() == DownLoadManager.STATE_WAITING) {
				tv_open.setText("暂停");
				tv_open.setBackgroundResource(R.drawable.apk_state);
				setByStatus(DownLoadManager.STATE_DOWNLOADING);
				info.getDownLoadTask().reStart();

				DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
				DownLoadModel mDown=new DownLoadModel();
				mDown.setmAppContent(mAppContent);
				mDown.setInfo(infoss);
				EventBus.getDefault().post(mDown);
				return;
			}
			else if (infos.getState() == DownLoadManager.STATE_PAUSE) {
				tv_open.setText("暂停");
				tv_open.setBackgroundResource(R.drawable.apk_state);
				setByStatus(DownLoadManager.STATE_DOWNLOADING);
				info.getDownLoadTask().resume();

				DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
				DownLoadModel mDown=new DownLoadModel();
				mDown.setmAppContent(mAppContent);
				mDown.setInfo(infoss);
				EventBus.getDefault().post(mDown);
				return;
			}
			else if (infos.getState() == DownLoadManager.STATE_DOWNLOADING) {
				tv_open.setText("继续");
				tv_open.setBackgroundResource(R.drawable.apk_state);
				setByStatus(DownLoadManager.STATE_PAUSE);
				info.getDownLoadTask().pause();

				DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
				DownLoadModel mDown=new DownLoadModel();
				mDown.setmAppContent(mAppContent);
				mDown.setInfo(infoss);
				EventBus.getDefault().post(mDown);
				return;
			}
			else if (infos.getState() == DownLoadManager.STATE_ERROR) {
				tv_open.setText("暂停");
				tv_open.setBackgroundResource(R.drawable.apk_state);
				setByStatus(DownLoadManager.STATE_DOWNLOADING);
				info.getDownLoadTask().reStart();

				DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
				DownLoadModel mDown=new DownLoadModel();
				mDown.setmAppContent(mAppContent);
				mDown.setInfo(infoss);
				EventBus.getDefault().post(mDown);
				return;
			}
			else if (infos.getState() == DownLoadManager.AlreadyInstalled) {
				//tv_open.setText("打开");
				tv_open.setText("");
				tv_open.setBackgroundResource(R.drawable.home_open);
				setByStatus(DownLoadManager.AlreadyInstalled);


				if(MonitorSysReceiver.checkApkExist(mContext,mAppContent.packageName , mAppContent.gameId)){
					PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
					if(packageManager!=null){
						Intent intent=new Intent();
						intent =packageManager.getLaunchIntentForPackage(mAppContent.packageName);
						if(intent!=null){
							mContext.startActivity(intent);
						}
					}
				}else{

					// 下载完成后弹出安装窗
					File file = new File(DownLoadManager.getManager().deFaultDir+ infos.getFileName());
//					Intent intentInstall = new Intent();
//					intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intentInstall.setAction(Intent.ACTION_VIEW);
//					intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//					mContext.startActivity(intentInstall);
					FileUtil.apkInstall(file,mContext);

					DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
					DownLoadModel mDown=new DownLoadModel();
					mDown.setmAppContent(mAppContent);
					mDown.setInfo(infoss);
					EventBus.getDefault().post(mDown);
					return;
				}
				DownloadInfo infoss = infos.getDownLoadTask().getDownloadInfo();
				DownLoadModel mDown=new DownLoadModel();
				mDown.setmAppContent(mAppContent);
				mDown.setInfo(infoss);
				EventBus.getDefault().post(mDown);
				return;
			}
		}

		private double getRealNum(double num) {
			BigDecimal bg = new BigDecimal(num);
			return bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		/**
		 * 根据状态设置图标
		 * @param
		 * @param status
		 */
		private void setByStatus( int status) {

			switch (status) {
			case DownLoadManager.STATE_DOWNLOADING:
				item_mLinearMsg.setVisibility(View.GONE);
				mLinear_progress.setVisibility(View.VISIBLE);
				break;
			case DownLoadManager.STATE_ERROR:
				item_mLinearMsg.setVisibility(View.VISIBLE);
				mLinear_progress.setVisibility(View.GONE);
				break;
			case DownLoadManager.STATE_WAITING:
				item_mLinearMsg.setVisibility(View.VISIBLE);
				mLinear_progress.setVisibility(View.GONE);
				break;
			case DownLoadManager.STATE_PAUSE:
				item_mLinearMsg.setVisibility(View.VISIBLE);
				mLinear_progress.setVisibility(View.GONE);
				break;
			case DownLoadManager.STATE_FINISH:
				item_mLinearMsg.setVisibility(View.GONE);
				mLinear_progress.setVisibility(View.VISIBLE);
				break;
			case DownLoadManager.AlreadyInstalled:
				item_mLinearMsg.setVisibility(View.VISIBLE);
				mLinear_progress.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	}
}
