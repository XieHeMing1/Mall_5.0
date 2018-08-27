package com.qy.zgz.mall.lcb_game

import android.os.Bundle
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button

import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseActivity
import com.qy.zgz.mall.base.BaseRxActivity
import com.qy.zgz.mall.lcb_game.RiseNumberTextView
import com.zhy.autolayout.utils.AutoUtils
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject





@ContentView(R.layout.activity_num_dance)
class NumDanceActivity : BaseActivity() {

    @ViewInject(R.id.rntv_num_dance)
    lateinit var rntv:RiseNumberTextView

//    @ViewInject(R.id.snow_view)
//    lateinit var snow_view: SnowView

//    @ViewInject(R.id.tv_ready)
//    lateinit var tv_ready: TextView

    @ViewInject(R.id.wrv_prize)
    lateinit var wrv_prize: RecyclerView


    @ViewInject(R.id.btn_start)
    lateinit var btn_start: Button



    //上次选中的
    private var lastSelect=0
    //总奖品数量
    private var sumPrizeNum=40

   var game_prize_adapter:GamePrizeAdapter?=null;

    override fun onClick(v: View) {
        when(v!!.id){
            R.id.btn_start->{
                if(rntv.isRunning) {
                    rntv.stop()
                    btn_start.text="开始"
                }else{
                    rntv.start()
                    btn_start.text="停止"
                }
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
//        snow_view.startSnowAnim(SnowUtils.SNOW_LEVEL_HEAVY)
        wrv_prize.layoutManager=GridLayoutManager(this,4)
        wrv_prize.addItemDecoration(GridSpaceItemDecoration(AutoUtils.getPercentHeightSize(20),AutoUtils.getPercentWidthSize(5),AutoUtils.getPercentHeightSize(20),AutoUtils.getPercentWidthSize(5)))
        game_prize_adapter=GamePrizeAdapter(this,ArrayList<String>(),false,sumPrizeNum)
        wrv_prize.adapter=game_prize_adapter
        rntv.withNumber(1100)
        rntv.setOnEnd {
            btn_start.text="开始"
//            game_prize_adapter!!.isShowSelect=false
//            game_prize_adapter!!.notifyDataSetChanged()
        }
        rntv.setOnTimeTick {
            game_prize_adapter!!.apply {
                if (rntv.isRunning){
                    if (this.getLastSelect()+1>=sumPrizeNum){
                        this.setLastSelect(0)
                    }else{
                        this.setLastSelect(this.getLastSelect()+1)
                    }
                    this.isShowSelect=true
                    this.notifyDataSetChanged()
                }
            }

        }
    }

    override fun ObjectMessage(msg: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
