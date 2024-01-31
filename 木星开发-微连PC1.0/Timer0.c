#include <REGX52.H>
#include "Timer0.h"

void Timer0Init(void)		//1毫秒@12.000MHz
{
	TMOD &= 0xF0;		//设置定时器模式
	TMOD |= 0x01;		//设置定时器模式
	TL0 = 0x18;		//设置定时初值
	TH0 = 0xFC;		//设置定时初值
	TF0 = 0;		//清除TF0标志
	TR0 = 1;		//定时器0开始计时
	//配置中断 根据电路图 打开ET0开关
	ET0=1;
	EA=1;
	PT0=0; //这个双路开关拨到下面
}

/*
模板
void Time0_DaoL() interrupt 1{  //定时器0的中断号是1
//每次中断初始化
	static unsigned int T0count=0;
	TL0 = 0x18;		//设置定时初值
	TH0 = 0xFC;		//设置定时初值
	T0count++;


	if(T0count>=1000){
		T0count=0;
		D1=~D1;
	}

}
*/