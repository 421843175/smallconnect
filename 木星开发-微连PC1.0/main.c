#include <REGX52.H>
#include "MatrixKey.h"
#include "Delay.h"
#include "UART.h"
#include "LCD1602.h"
#include <string.h>
#include "DS18B20.h"
#include <stdio.h>
#include "Timer0.h"
#include "AT24C02.h"
#include <stdlib.h>

#define SIZE 12
#define LINE2SIZE 94

//P00-P07=>L4-H1  D3-D5=>P37-P35


//setting
sbit D3=P3^7;
sbit D4=P3^6;
sbit D5=P3^5;





float T;
char str[6]; 

char cmd[SIZE];
char num[2];
int i=0;


int  keynumber; 
char tstr[]="T:";
char Kstr[]="K:";


int convertToInt(const char *str);
int isrun=1;

//char strline2[LINE2SIZE]="imitate: temperature ";
//char printfnum[2];

//set温度
int temperature;
int humidity;

//char endprint[10];
char send[5];
void SetNumber(int t,int h);
void main(){
	
	LCD_Init();
	UART_Init();


	//测试使用

	//写完 需要等5ms(写入时间)
	//Delayms(5);

	isrun=AT24C02_ReadByte(1);
	

	if(isrun==0)isrun=1;
	
	if(temperature==0)temperature=30;
	if(humidity==0)humidity=10;
	

	
	Timer0Init();
	
	

	while(1){
	
			
		if(isrun==1){
		LCD_ShowString(1,1,"RUN                         ");
			temperature=AT24C02_ReadByte(2);
			humidity=AT24C02_ReadByte(3);
			SetNumber(temperature,humidity);
		D3=0;
		D4=1;
		D5=1;
	}else if(isrun==2){
		LCD_ShowString(1,1,"Setting                     ");
		LCD_ShowString(2,1,"Please set up               ");
		D3=1;
		D4=0;
		D5=1;
	}else if(isrun==3){
		LCD_ShowString(1,1,"STOP                        ");
		LCD_ShowString(2,1,"                            ");
		D3=1;
		D4=1;
		D5=0;
	}
		//按下按键反馈
		keynumber=MatrixKey();
		if(keynumber>=1&&keynumber<=12){
		sprintf(num,"%d",keynumber);
		//通过串口发送
		strcat(Kstr,num);
			UARE_SendString(Kstr);
			//LCD_ShowString(2,1,Kstr);
		
			//初始化
		Kstr[0]='K';
		Kstr[1]=':';
		Kstr[2]='\0';
		}
	}
}




int left=0,thisleft=0;
void Time0_DaoL() interrupt 1{ 
	 
		static unsigned int T0count=0;
	
	int ii=0;
	TL0 = 0x18;		//设置定时初值
	TH0 = 0xFC;		//设置定时初值
	
	
	T0count++;
	
	
	
//定时器  滚动字幕

	/*if(T0count%500==0){
		sprintf(strline2,"imitate:temperature %d centigrade,humidity %d% environment temperature %f centigrade!",temperature,humidity,T);
	if(thisleft>=LINE2SIZE)
	{thisleft=0;}
	else{left=thisleft++;}
	
		for(ii=0;ii<16;ii++){
			if(left>=LINE2SIZE){
				endprint[ii]=' ';
					continue;
			}
				endprint[ii]=strline2[left++];
			
		}
		LCD_ShowString(2,1,endprint);

	}*/
	
	//定时器 温度自动发送
	if(T0count>=3800){
		DS18B20_ConverT();
		Delayms(20);
		T=DS18B20_ReadT();
		//将T转换成字符串
		sprintf(str,"%f",T);
		//通过串口发送
		strcat(tstr,str);
		UARE_SendString(tstr);
		//LCD_ShowString(1,1,tstr);
		
		//for(i=0;i<8;i++){
			//tstr[i]="";
		//}
		
		//初始化
		tstr[0]='T';
		tstr[1]=':';
		tstr[2]='\0';
		T0count=0;
	}
		
}


//定时器 PC指令控制
void UART_Routine() interrupt 4{//触发4号中断
	
	static int i = 0;//静态变量，只初始化一次

	if(RI){
		cmd[i] = SBUF;
		i++;
		if(i == SIZE){
			i=0;
		}
		
				//判断CMD是否包含
		if(strstr(cmd,"c")&&i>=3){
			
			if(cmd[2]=='1'){
				AT24C02_WriteByte(1,1);
				isrun=1;
			}else if(cmd[2]=='2'){
			AT24C02_WriteByte(1,2);
				isrun=2;
			}else if(cmd[2]=='3'){
			AT24C02_WriteByte(1,3);
				isrun=3;
			}
			Delayms(5);
			
			UARE_SendString("c ok");
			i=0;
			
			memset(cmd,'\0',SIZE);//清空数组，方便接收下一条指令
		
		
		}
		else if(strstr(cmd,"t") && i>=4){
			int temp;
			temp =convertToInt(cmd);
			
			
			//if(temperature==0)UARE_SendString("t error");
			if(temp!=0){
				
			AT24C02_WriteByte(2,temp);
			Delayms(5);
			UARE_SendString("t ok");
			}
	
			
			i=0;
			memset(cmd,'\0',SIZE);
			
		}
		else if(strstr(cmd,"h") && i>=4){
			int temp2;
			temp2 =convertToInt(cmd);
			
			
			//if(temperature==0)UARE_SendString("t error");
			if(temp2!=0){
				
			AT24C02_WriteByte(3,temp2);
			Delayms(5);
			UARE_SendString("h ok");
			}
	
			
			i=0;
			memset(cmd,'\0',SIZE);
			
		}
		//else if(strstr(cmd,"E") && i>=1){
			//	sprintf(send,"E-%d",temperature);
		//
			//UARE_SendString(send);	
		//		i=0;
		//	memset(cmd,'\0',SIZE);
		//}
		/*else if(strstr(cmd,"H")){
				sprintf(send,"H-%d",humidity);
						
			UARE_SendString(send);
				i=0;
			memset(cmd,'\0',SIZE);
		}
		else if(strstr(cmd,"C")){
				sprintf(send,"C-%d",isrun);
					
			UARE_SendString(send);	
				i=0;
			memset(cmd,'\0',SIZE);
		}	*/
		RI = 0;//复位接收中断标志
	}
	
} 

void SetNumber(int t,int h){
	LCD_ShowString(2,1,"T:");
	LCD_ShowNum(2,3,t,2);
	LCD_ShowString(2,5,"  ");
		LCD_ShowString(2,7,"H:");
		LCD_ShowNum(2,9,h,2);
	LCD_ShowString(2,11,"               ");
}


int convertToInt(const char *str) {
    // 查找冒号的位置
    const char *colon = strchr(str, ':');
    if (colon != NULL) {
        // 将冒号后面的字符串转换为整数
        return atoi(colon + 1);
    }
    // 如果没有找到冒号，返回0或者错误代码
    return 0;
}