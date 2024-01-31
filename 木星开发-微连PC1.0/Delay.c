#include <REGX52.H>
void Delayms(unsigned int delayms)
{
	unsigned char i, j;
	while(delayms--){
		i = 2;
		j = 239;
		do
		{
			while (--j);
		} while (--i);
	}
}
