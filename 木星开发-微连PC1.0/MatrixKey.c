#include<REGX52.H>
#include "Delay.h"

//P00-P07->L4-H1
//将P1用K1代替
#define K1 P0
//注意sbit仅仅用于 单个引脚
sbit K1_0=P0^0;
sbit K1_1=P0^1;
sbit K1_2=P0^2;
sbit K1_3=P0^3;
sbit K1_4=P0^4;
sbit K1_5=P0^5;
sbit K1_6=P0^6;
sbit K1_7=P0^7;

//按列扫描  图在51单片机入门_灰海宽松 K33
unsigned char MatrixKey(){

	unsigned char KeyNumber=0;
	
	
	//扫描第一列
	//初始化一下
	K1=0xFF;
	//设13端口位 低电平（输入一个低电平 什么位置输出了也是低电平就是谁）
	K1_3=0;
	if(K1_7==0){Delayms(20);while(K1_7==0);Delayms(20);
		//S1被按下
		KeyNumber=1;
	}
	if(K1_6==0){Delayms(20);while(K1_6==0);Delayms(20);KeyNumber=5;}
	if(K1_5==0){Delayms(20);while(K1_5==0);Delayms(20);KeyNumber=9;}
	if(K1_4==0){Delayms(20);while(K1_4==0);Delayms(20);KeyNumber=13;}
	
	
	//扫描第二列
		K1=0xFF;
		K1_2=0;
	if(K1_7==0){Delayms(20);while(K1_7==0);Delayms(20);KeyNumber=2;}
	if(K1_6==0){Delayms(20);while(K1_6==0);Delayms(20);KeyNumber=6;}
	if(K1_5==0){Delayms(20);while(K1_5==0);Delayms(20);KeyNumber=10;}
	if(K1_4==0){Delayms(20);while(K1_4==0);Delayms(20);KeyNumber=14;}
	
		K1=0xFF;
			K1_1=0;
	if(K1_7==0){Delayms(20);while(K1_7==0);Delayms(20);KeyNumber=3;}
	if(K1_6==0){Delayms(20);while(K1_6==0);Delayms(20);KeyNumber=7;}
	if(K1_5==0){Delayms(20);while(K1_5==0);Delayms(20);KeyNumber=11;}
	if(K1_4==0){Delayms(20);while(K1_4==0);Delayms(20);KeyNumber=15;}
	
		K1=0xFF;
				K1_0=0;
	if(K1_7==0){Delayms(20);while(K1_7==0);Delayms(20);KeyNumber=4;}
	if(K1_6==0){Delayms(20);while(K1_6==0);Delayms(20);KeyNumber=8;}
	if(K1_5==0){Delayms(20);while(K1_5==0);Delayms(20);KeyNumber=12;}
	if(K1_4==0){Delayms(20);while(K1_4==0);Delayms(20);KeyNumber=16;}
	
	return KeyNumber;


}