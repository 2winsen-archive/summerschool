#include <ncurses.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include "snake.h"

/*network libs*/
#include <stdio.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>	
#include <netdb.h>

extern int diff;
extern int attempts;
extern int speed;
extern bool borders;
extern int points;
extern int direction;
extern int length;

extern int x;
extern int y;
extern bool resume;

void error(char *msg)
{
    perror(msg);
    exit(1);
}

int mpHost(int* hostSock, int* clientSock)
{
   	/*
    initialises socket connection, sets its 
    number into parameter sock, and returns result
    */
	clear();
    printw("Creating connection... ");
    refresh();
    struct sockaddr_in serv_addr, cli_addr;
    
    /*create socket*/
    *hostSock = socket(AF_INET, SOCK_STREAM, 0);
    if (*hostSock < 0)
	{
    	error("Failed to create socket");               
        return -1;
    }

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(PORT_NUM);

    if (bind(*hostSock, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
    	error("ERROR on binding");
        return -1;
    }
    
    listen(*hostSock, 5);
    
    int cliLen = sizeof(cli_addr);
    *clientSock = accept(*hostSock, (struct sockaddr *) &cli_addr, &cliLen);
    if (*clientSock < 0)
	{
    	error("ERROR on accept");
    	return -1;
    }
    
    
    /*********************
    **Hosted played game**
    *********************/	
	mpHostNewGame(*clientSock);
	
	
	/*return number to the parameter*/
    /*if success*/
    printw("success!\n");
    return 1;
}

int mpJoin(int* clientSock, char* ipAddress)
{
    /* initialises client side connection to the specified address */        
    
    clear();
    printw("Connecting to host... ");
    refresh();
    
    /*create socket*/
    /*AF_INT - constant for internets protocols, to communicate through internet*/
    /*SOCK_STREAM - smth unknown*/
    /*0 - lets OS automatically choose protocol, 6 would be TCP*/
    *clientSock = socket(AF_INET, SOCK_STREAM, 0);
	if (*clientSock < 0)
	{
		/*failed to create*/
		error("Failed to create socket");               
		return -1;
    }       
    
    struct hostent *server;
    char buffer[256];
    int n; 
    echo();
    /*associate server with IP address given*/
    server = gethostbyname(ipAddress);      
    if (!server)
	{
		error("ERROR, no such host");
		return -1;
    }
    
    struct sockaddr_in serv_addr, cli_addr;
    /*add info about server to the structure*/
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, (char *)&serv_addr.sin_addr.s_addr, server->h_length);
    serv_addr.sin_port = htons(PORT_NUM);
    if (connect(*clientSock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
		error("ERROR connecting");
		return -1;
    }
    
    /*********************
    **Joined played game**
    *********************/
    	
	mpClientNewGame(*clientSock);
	
	
	//if success
    printw("success!\n");
    return 1;
}

int mpHostNewGame(int clientSock)
{
	bkgdset((chtype)COLOR_PAIR(INFO_COLOR));
	clear();
	
	/*table borders*/
	const int tWidth = 20;
	const int tHeight = 20; 
		 
	int i=0;
	int j=0;
	
	int bufDirection=direction;

	/*allocate memory for dynamic array*/
	int** table = malloc(tHeight * sizeof(int *));
	for(i = 0; i < tHeight; i++)
		table[i] = malloc(tWidth * sizeof(int));
		
	for(i=0;i<tWidth;i++)
		for(j=0;j<tHeight;j++)
			table[i][j]=0;
			
	if(!resume) 
		initSnake(x,y);

	bool exit = false;
	bool eaten = false;		
	generateFood(table,tWidth,tHeight);
	
	resume=false;
	do
	{	
		if(gameOver()==1)	/*see if not game over*/
			break;
	
		printScore(points);
		printAttempts(attempts);
		
		int n=0;
		n = sendTable(clientSock,table,tWidth,tHeight);
		if(n<0)
			return -1;
		drawTable(table,tWidth,tHeight);
		if(eaten)
		{
			int res;
			res = generateFood(table,tWidth,tHeight);
			if(res == 1)
			{
				int foodX = 3;
				int foodY = 2;
				if(table[foodX][foodY] == 7)
				{
					foodX = 10;
					foodY = 12;
					if(table[foodX][foodY] == 7)
					{
						foodX = 15;
						foodY = 3;
						if(table[foodX][foodY] == 7)
						{
							foodX = 2;
							foodY = 17;			
						}
						if(table[foodX][foodY] == 7)
						{
							srand((unsigned)time(0));
				  			foodX = rand()%(tWidth-2)+1;
							foodY = rand()%(tHeight-2)+1;
						}
					}
				}
				table[foodX][foodY] = 9;
			}
			eaten=false;
		}
		halfdelay(speed);
		
		int c = getch();
		if(c == 10) 
		{
			exit=true;
			resume = true;
		}	
		
		bufDirection=direction;
		direction = changeDirection(c);
		switch(direction)
		{
			case 1:
			{	
				y--;
				borderFunction(table,tWidth,tHeight);
				break;
			}		 
			case 2: 
			{
				x++;
				borderFunction(table,tWidth,tHeight);
				break;	
			}
			case 3:
			{
				y++;
				borderFunction(table,tWidth,tHeight);
				break;
			}
			case 4:
			{
				x--;
				borderFunction(table,tWidth,tHeight);
				break;
			} 
			default:
			{
				printw("Illegal direction!");
				break;
			}
		}
		
		if(table[y][x] == 7)	/*new attempt*/
			newAttempt(table,tWidth,tHeight);
		else
			moveSnake(table,x,y,bufDirection,&eaten);	
	}while(!exit);
	
	/*deallocate dynamic array from memory*/
	for(i = 0; i < tHeight; i++)
		free(table[i]);		
	free(table);

	clear();
	return 0;
}

int mpClientNewGame(int clientSock)
{	
	/*table borders*/
	const int tWidth = 20;
	const int tHeight = 20;
	int len; 
		 
	int i=0;
	int j=0;

	/*allocate memory for dynamic array*/
	int** table = malloc(tHeight * sizeof(int *));
	for(i = 0; i < tHeight; i++)
		table[i] = malloc(tWidth * sizeof(int));
		
	for(i=0;i<tWidth;i++)
		for(j=0;j<tHeight;j++)
			table[i][j]=0;

	bool exit = false;	
	do
	{
		for(i=0;i<tWidth;i++)
			for(j=0;j<tHeight;j++)
			{
				recv(clientSock, &table[i][j],sizeof(int),0);
				halfdelay(speed);
			}
		drawTable(table,tWidth,tHeight);				
		
		int c = getch();
		if(c == 10) 
		{
			exit=true;
		}	
	}while(!exit);
	
	/*deallocate dynamic array from memory*/
	for(i = 0; i < tHeight; i++)
		free(table[i]);		
	free(table);

	clear();
	return 0;
}

int sendTable(int clientSock,int** table,int width,int height)
{
	int i;
	int j;
	int n;
	for(i=0;i<width;i++)
	{
		for(j=0;j<height;j++)
		{
			n = send(clientSock,&table[i][j], sizeof(int),0);
			if(n<0)
				return -1;
		}
	}
	return 1;
}

int sendLenDir(int clientSock)
{
	int n;
	n = send(clientSock,&direction, sizeof(int),0);
	if(n<0)
		return -1;
	n = send(clientSock,&length, sizeof(int),0);
	if(n<0)
		return -1;
	return 1;
}
/*
int sendSnake(int clientSock)
{
	int n;
	int i;
	for(i=0;i<length;i++)
	{
		n = send(clientSock,&snake[i].x, sizeof(int),0);
		if(n<0)
			return -1;
		n = send(clientSock,&snake[i].y, sizeof(int),0);
		if(n<0)
			return -1;
	}
}
*/

int sendFood(int clientSock)
{
}
