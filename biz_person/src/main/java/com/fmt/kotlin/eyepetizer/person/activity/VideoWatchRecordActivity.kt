package com.fmt.kotlin.eyepetizer.person.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.fmt.kotlin.eyepetizer.common.ext.dp2px
import com.fmt.kotlin.eyepetizer.common.ext.immersionStatusBar
import com.fmt.kotlin.eyepetizer.common.ext.setToolBar
import com.fmt.kotlin.eyepetizer.person.R
import com.fmt.kotlin.eyepetizer.person.adapter.DiffUtilCallback
import com.fmt.kotlin.eyepetizer.person.adapter.VideoWatchRecordAdapter
import com.fmt.kotlin.eyepetizer.provider.constant.BaseConstant
import com.fmt.kotlin.eyepetizer.provider.model.event.WatchVideoEvent
import com.fmt.kotlin.eyepetizer.provider.service.warp.VideoWatchWarp
import com.fmt.livedatabus.LiveDataBus
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.person_activity_video_watch_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoWatchRecordActivity : AppCompatActivity() {

    private val mAdapter by lazy {
        VideoWatchRecordAdapter(
            this,
            VideoWatchWarp.getVideoWatchList()
        )
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VideoWatchRecordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionStatusBar(true, android.R.color.white, true, 0.2f)
        setContentView(R.layout.person_activity_video_watch_record)
        setToolBar(mToolbar)
        initSwipeMenu()
        initEvent()
    }

    private fun initSwipeMenu() {
        val mSwipeMenuCreator =
            SwipeMenuCreator { _, rightMenu, _ ->
                val deleteItem: SwipeMenuItem =
                    SwipeMenuItem(this).setBackground(R.drawable.person_selector_red)
                        .setImage(R.drawable.person_ic_action_delete)
                        .setWidth(dp2px(70f))
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                rightMenu.addMenuItem(deleteItem)
            }

        val mItemMenuClickListener =
            OnItemMenuClickListener { menuBridge, position ->
                menuBridge.closeMenu()

                if (menuBridge.direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                    VideoWatchWarp.removeVideoWatchRecord(mAdapter.mDataList[position])
                    mAdapter.remove(position)
                }
            }

        mSwipeRecyclerView.apply {
            setSwipeMenuCreator(mSwipeMenuCreator)
            setOnItemMenuClickListener(mItemMenuClickListener)
            isSwipeItemMenuEnabled = true
            layoutManager = LinearLayoutManager(this@VideoWatchRecordActivity)
            adapter = mAdapter
        }
    }

    private fun initEvent() {
        LiveDataBus.with<WatchVideoEvent>(BaseConstant.WATCH_VIDEO_EVENT).observe(this, {
            updateWatchRecord()
        })
    }

    private fun updateWatchRecord() {
        val newDataList = VideoWatchWarp.getVideoWatchList()
        lifecycleScope.launch(Dispatchers.IO) {
            //对比数据源比较耗时，所以放在子线程中进行处理
            val diffResult = DiffUtil.calculateDiff(
                DiffUtilCallback(
                    mAdapter.mDataList,
                    newDataList
                )
            )
            withContext(Dispatchers.Main) {
                diffResult.dispatchUpdatesTo(mAdapter)
                mAdapter.newData(newDataList)
            }
        }
    }
}