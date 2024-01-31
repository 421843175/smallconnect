#include <REGX52.H>
#include "Delay.h"

//初始化串口
/**
  *	@brief 串口初始化 4800bps@12.000MHz
  *	@param
  *	@retval
*/
void UART_Init(){  
	SCON=0x50; //0100 0000  确定SM工作方式 0x50才能接收
	PCON |= 0x80;
	//串口需要使用定时器1 8位自动重装
	TMOD &= 0x0F;		//清除定时器1模式位
	TMOD |= 0x20;		//设定定时器1为8位自动重装方式
	TL1 = 0xF3;		//设定定时初值
	TH1 = 0xF3;		//设定定时器重装值
	ET1 = 0;		//禁止定时器1中断
	TR1 = 1;		//启动定时器1
	EA=1;	//启动中断
	ES=1; //启动串口中断
}

/**
  *	@brief 串口发送一个字节数据
  *	@param 要发送的字节数据
  *	@retval
*/
void UARE_SendByte(unsigned char byte){
	//只要写到SBUF 因为波特率和定时器都配置完了 所以会自动发
	SBUF=byte;
	while(TI==0);  //TI是0的时候一直循环 当TI不是0 需要手动复位
	TI=0;
}

void UARE_SendString(unsigned char * s){
	while(*s){
		SBUF=*s;
		while(TI==0);
		TI=0;
		s++;
	}
}

/*发送数据模板
void UART_Routine() interrupt 4{//触发4号中断
	//发送中断
	if(RI==1){
		P2=~SBUF;
		//复位
		RI=0;
	}
} 
*/

