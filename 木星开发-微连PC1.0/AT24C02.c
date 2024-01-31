#include <REGX52.H>
#include "I2C.h"
//注意要连EEPROM SCL->P21 SDA->P20
#define AT24C02_ADDRESS 0xA0  //固定地址

/**
  *	@brief AT24C02写入一个字节
  *	@param WordAddress 要写入字节的地址 Data 要写入的数据
  *	@retval
*/
void AT24C02_WriteByte(unsigned char WordAddress,Data){
	I2C_Start();
	I2C_SendByte(AT24C02_ADDRESS);
	I2C_ReceiveAck();
	I2C_SendByte(WordAddress);
	I2C_ReceiveAck();
	I2C_SendByte(Data);
	I2C_ReceiveAck();
	I2C_Stop();
}

/**
  *	@brief AT24C02读入一个字节
  *	@param WordAddress 读入字节的地址
  *	@retval 要写入的数据
*/
char AT24C02_ReadByte(unsigned char WordAddress){
	unsigned char Data;
	I2C_Start();
	I2C_SendByte(AT24C02_ADDRESS);	
	I2C_ReceiveAck();
	I2C_SendByte(WordAddress);
	I2C_ReceiveAck();
	I2C_Start();//接受应答完了后再开
	I2C_SendByte(AT24C02_ADDRESS|0x01);  //请求读
	I2C_ReceiveAck();
	Data=I2C_ReceiveByte();	
	//发送应答
	I2C_SendAck(1);
	I2C_Stop();	
	return Data;

}