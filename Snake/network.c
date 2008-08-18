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

int mpPoints=0;
int mpDirection=D_RIGHT;
int mpLength=3;
int mpX = START_X;
int mpY = START_Y-10;
struct MPSnake
{
	int x;
	int y;
} mpSnake[SNAKE_MAX];


int error(char *msg)
{
    printw(msg);
    return -1;
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

	/*reuses port address and no bind err*/
	setsockopt(*hostSock, SOL_SOCKET, SO_REUSEADDR, &serv_addr, sizeof(serv_addr));
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

	renewGlobals();
	speed = 1;
	mpHostNewGame(*clientSock);
	
	/*return number to the parameter*/
    return 1;
}

int mpJoin(int* clientSock)
{
	clear();
	char ip[16];
	echo();
	printw("Enter Host IP Address: ");
	scanw("%s\0",&ip);
	
    /* initialises client side connection to the specified address */            
	noecho();
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
    server = gethostbyname((char*)ip);      
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
    
    /*if success*/
    printw("success!\n");
    
    /*********************
    **Joined played game**
    *********************/
	mpPoints = points;
	mpDirection = direction;
	mpLength = length;
	mpX = START_X;
	mpY = START_Y-10;
	speed = 1;
	 
	mpClientNewGame(*clientSock);
	
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
	int n;
	int signal;
	
	int bufDirection=direction;

	/*allocate memory for dynamic array*/
	int** table = malloc(tHeight * sizeof(int *));
	for(i = 0; i < tHeight; i++)
		table[i] = malloc(tWidth * sizeof(int));
		
	for(i=0;i<tWidth;i++)
		for(j=0;j<tHeight;j++)
			table[i][j]=0;
			
	initSnake(x,y);

	bool exit = false;
	bool eaten = false;
	resume=false;
	
	/*food positions*/
  	int foodX;
	int foodY;
	srand((unsigned)time(0));	
	for(i=0;i<30;i++)
	{
	
		foodX = rand()%(tWidth-2)+1;
		foodY = rand()%(tHeight-2)+1;
		table[foodX][foodY] = 9;
	}

	do
	{			
		printScore(points);
		drawTable(table,tWidth,tHeight);
		halfdelay(speed);
		
		int c = getch();
		if(c == 10)
		{
			signal = 1;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
			exit=true;
		}
		else
		{
			signal = 0;
				n = send(clientSock,&signal, sizeof(int),0);
				if(n<0)
			return -1;
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
		
		if(table[y][x] == 7)
		{
			move(3,1);
			printw("You lost");
			
			signal = 1;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
			exit=true;
		}
		else
		{
			moveSnake(table,x,y,bufDirection,&eaten);
			signal = 0;
				n = send(clientSock,&signal, sizeof(int),0);
				if(n<0)
			return -1;
		}
		
		/*sending own table*/
		n = sendTable(clientSock,table,tWidth,tHeight);
		if(n<0)
			return -1;
			
		recv(clientSock, &signal,sizeof(int),0);		
		if(signal == 1)
			exit=true;
		
		/*Recieving table from client*/
		for(i=0;i<tWidth;i++)
			for(j=0;j<tHeight;j++)
				recv(clientSock, &table[i][j],sizeof(int),0);
		drawTable(table,tWidth,tHeight);
			
		n = matchEnd(table,tWidth,tHeight,clientSock,points);		
		if(n<0)
			return -1;
		if(n==1)
		{			
			signal = 1;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
			exit=true;
		}		
		else if(n==2)
		{
			signal = 0;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
		}
		recv(clientSock, &signal,sizeof(int),0);		
		if(signal == 1)
			exit=true;
		
	}while(!exit);
	
	/*deallocate dynamic array from memory*/
	for(i = 0; i < tHeight; i++)
		free(table[i]);		
	free(table);
	
	while(getch() != 10);	/*pause to view table, before exit*/
	clear();
	return 0;
}

int mpClientNewGame(int clientSock)
{
	bkgdset((chtype)COLOR_PAIR(INFO_COLOR));
	clear();
	move(23,79);
	
	/*table borders*/
	const int tWidth = 20;
	const int tHeight = 20;
		 
	int i=0;
	int j=0;
	int n;
	int signal;
	
	int bufDirection=mpDirection;

	/*allocate memory for dynamic array*/
	int** table = malloc(tHeight * sizeof(int *));
	for(i = 0; i < tHeight; i++)
		table[i] = malloc(tWidth * sizeof(int));
		
	for(i=0;i<tWidth;i++)
		for(j=0;j<tHeight;j++)
			table[i][j]=0;
			
	mpInitSnake(mpX,mpY);

	bool exit = false;
	bool eaten = false;
	resume=false;

	do
	{	
		printScore(mpPoints);
		drawTable(table,tWidth,tHeight);
		halfdelay(speed);
		
		int c = getch();			
		recv(clientSock, &signal,sizeof(int),0);		
		if(signal == 1)
			exit=true;
		
		bufDirection=mpDirection;
		mpDirection = mpChangeDirection(c);
		switch(mpDirection)
		{
			case 1:
			{	
				mpY--;
				mpBorderFunction(table,tWidth,tHeight);
				break;
			}		 
			case 2: 
			{
				mpX++;
				mpBorderFunction(table,tWidth,tHeight);
				break;	
			}
			case 3:
			{
				mpY++;
				mpBorderFunction(table,tWidth,tHeight);
				break;
			}
			case 4:
			{
				mpX--;
				mpBorderFunction(table,tWidth,tHeight);
				break;
			} 
			default:
			{
				printw("Illegal direction!");
				break;
			}
		}
		
		recv(clientSock, &signal,sizeof(int),0);		
		if(signal == 1)
			exit=true;
		
		/*recieving table*/			
		for(i=0;i<tWidth;i++)
			for(j=0;j<tHeight;j++)
				recv(clientSock, &table[i][j],sizeof(int),0);
		drawTable(table,tWidth,tHeight);
		
		
		if(table[mpY][mpX] == 77)
		{
			move(3,1);
			printw("You lost");
			
			signal = 1;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
			exit=true;
		}		
		else
		{
			mpMoveSnake(table,mpX,mpY,bufDirection,&eaten);
			signal = 0;
				n = send(clientSock,&signal, sizeof(int),0);
				if(n<0)
			return -1;
		}	
		
		/*sending table to host*/
		n = sendTable(clientSock,table,tWidth,tHeight);
		if(n<0)
			return -1;
				
		n = matchEnd(table,tWidth,tHeight,clientSock,mpPoints);
		if(n<0)
			return -1;
		recv(clientSock, &signal,sizeof(int),0);		
		if(signal == 1)
			exit=true;
		if(n==1)
		{			
			signal = 1;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
			exit=true;
		}		
		else if(n==2)
		{
			signal = 0;
			n = send(clientSock,&signal, sizeof(int),0);
			if(n<0)
				return -1;
		}	
		
		
	}while(!exit);
	
	/*deallocate dynamic array from memory*/
	for(i = 0; i < tHeight; i++)
		free(table[i]);		
	free(table);
	
	while(getch() != 10);	/*pause to view table, before exit*/
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

void mpMoveSnake(int **table,int mpX,int mpY,int bufDirection,bool *eaten)
{
	int k=0;
	for(k=0;k<mpLength;k++)
	{
		if(k==0)
			table[mpSnake[k].y][mpSnake[k].x] = 0;
		if(k>=1)
		{
			mpSnake[k-1].x = mpSnake[k].x;
			mpSnake[k-1].y = mpSnake[k].y;
		}
		if(k==mpLength-1)
		{
			if(table[mpY][mpX]==9)	/*eating food*/
			{
				mpPoints+=5;
				mpLength++;
				if(mpLength>SNAKE_MAX)
					mpLength = SNAKE_MAX;
				mpSnake[mpLength-1].x = mpX;
				mpSnake[mpLength-1].y = mpY;
				*eaten=true;
			}
			
			if((bufDirection==D_UP && mpDirection==D_DOWN)
				||(bufDirection==D_DOWN && mpDirection==D_UP)
				||(bufDirection==D_RIGHT && mpDirection==D_LEFT)
				||(bufDirection==D_LEFT && mpDirection==D_RIGHT))
			{
				/*revert*/
			}
			else
			{
				mpSnake[k].x = mpX;
				mpSnake[k].y = mpY;
			}
			break;
		}
	}
	for(k=0;k<mpLength;k++)
	{
		table[mpSnake[k].y][mpSnake[k].x] = 77;
	}
}

void mpBorderFunction(int** table,int width,int height)
{
	if(!borders)
	{
		if(mpX>width-2) mpX=1;
		else if(mpX<1) mpX=width-2;
		if(mpY>height-2) mpY=1;
		else if(mpY<1) mpY=height-2;
	}
	else
	{
		if(mpX>width-2)
			newAttempt(table,width,height);
		if(mpX<1)
			newAttempt(table,width,height);
		if(mpY>height-2)
			newAttempt(table,width,height);
		if(mpY<1)
			newAttempt(table,width,height);
	}
}

int mpChangeDirection(int c)
{
	if(c == KEY_UP) return D_UP;
	else if(c == KEY_RIGHT) return D_RIGHT;
	else if(c == KEY_DOWN) return D_DOWN;
	else if(c == KEY_LEFT) return D_LEFT;
	else return mpDirection;
}

void mpInitSnake(int x,int y)
{
	/*initializing snake*/ 
	mpSnake[2].x = mpX;
	mpSnake[2].y = mpY;
	mpSnake[1].x = mpX-1;
	mpSnake[1].y = mpY;	
	mpSnake[0].x = mpX-2;
	mpSnake[0].y = mpY;	
}

int matchEnd(int** table,int width,int height,int clientSock,int points)
{
	int i;
	int j;
	int n;
	/*match end condition*/
	for(i=0;i<width;i++)
	{
		bool isFood = false;
		for(j=0;j<height;j++)
		{
			if(table[i][j]== 9)
			{
				isFood = true;
				break;
			}			
		}
		if(isFood) break;
	}
	if(i*j == width*height)
	{
		int otherPoints;
		n = send(clientSock,&points, sizeof(int),0);
		if(n<0)
			return -1;
		recv(clientSock, &otherPoints,sizeof(int),0);
			
		if(points>otherPoints)
		{
			move(3,1);
			printw("You win: %d - %d",points,otherPoints);
		}
		else if(points<otherPoints)
		{
			move(3,1);
			printw("You lost: %d - %d",points,otherPoints);
		}
		else if(points==otherPoints)
		{
			move(3,1);
			printw("Draw: %d - %d",points,otherPoints);
		}
		return 1;
	}
	else
		return 2;	
}
