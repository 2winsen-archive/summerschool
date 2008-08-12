#include <ncurses.h>
#include <stdlib.h>
#include <time.h>
#include <stdio.h>
#include <string.h>
#include "network.c"
#include "snake.h"


static int diff=EASY;
static int attempts=5;
static int speed=5;
static bool borders=false;
static int points=0;
static int direction=D_RIGHT;
static int length=3;

struct Snake
{
	int x;
	int y;
} snake[SNAKE_MAX];

struct Leader
{
	char name[25];
	int score;
} leader[11];
 
static int x = START_X;
static int y = START_Y;

static bool resume=false;
static bool nosave=false;
static bool save=false;
static bool bgColored = false;
static bool valid = true;
static bool scoreValid = true;

int mainMenu() 
{
	if(!bgColored)
	{
		bkgdset((chtype)COLOR_PAIR(MENU_BG_COLOR));
		clear();
		bgColored = true;
	}
	
	const int MAX = 10;
	char* menu[MAX]; 
	
	menu[0] = "    New Game\0";
	menu[1] = "    Resume Game\0";
	menu[2] = "    Multiplayer\0"; 
	menu[3] = "    Top Scores\0";
	menu[4] = "    Game Rules\0";
	menu[5] = "    Credits\0";
	menu[6] = "    Difficulty Level\0";
	menu[7] = "    Load Game\0";
	menu[8] = "    Save Game\0";
	menu[9] = "    Exit\0"; 
		
	move(8,34);
	clrtoeol();
	printColorString("MAIN MENU",MENU_HEADING_COLOR);
	 
	int i;
	for(i=0;i<MAX;i++)	/*printing whole menu*/
	{
		move(10+i,30);
		printw("%s",menu[i]);
		printw("\n");
	}

	bool select = false; 
	
	int index = 0;	
	move(10+index,30);
	addch(ACS_DIAMOND);	
	
	while(select != true)	/*controling menu*/
	{
		move(23,79);
		int c = getch(); 
		
		move(10+index,30);
		printw("  ");
			
		if(c == KEY_UP)
		{
			index--;
			if(index<0)
				index=MAX-1; 
		}
		if(c == KEY_DOWN)
		{
			index++;
			if(index>MAX-1)
				index=0;
		}
		if(c == 10)
			select = true;
			
		move(10+index,30); 
		addch(ACS_DIAMOND);				
	}
	
	return index;
}

int menuDifficulty() 
{
	const int MAX = 3;
	char* menu[MAX];
	
	menu[0] = "    EASY\0";
	menu[1] = "    MEDIUM\0"; 
	menu[2] = "    HARD\0";
	
	move(8,34);
	clrtoeol();
	printColorString("DIFFICULTY LEVEL",MENU_HEADING_COLOR);
	
	int i;
	for(i=0;i<MAX;i++)	/*printing whole menu*/
	{
		move(10+i,30);
		printw("%s",menu[i]);
		printw("\n");
	} 
	
	bool select = false;
	
	int index = 0;
	move(10+index,30);
	addch(ACS_DIAMOND);
	
	while(select != true)	/*controling menu*/
	{
		move(23,79);
		int c = getch();
		
		move(10+index,30);
		printw("  ");
			
		if(c == KEY_UP)
		{
			index--;
			if(index<0)
				index=MAX-1;
		}
		if(c == KEY_DOWN)
		{
			index++;
			if(index>MAX-1)
				index=0;
		}
		if(c == 10)
			select = true;
			
		move(10+index,30);
		addch(ACS_DIAMOND);
	}
	
	return index;
}

/*////////////////////
///SNAKE ALGORITHM////
////////////////////*/
int menuNewGame()
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
		
		drawTable(table,tWidth,tHeight);
		if(eaten)
		{
			generateFood(table,tWidth,tHeight);			
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

void moveSnake(int **table,int x,int y,int bufDirection,bool *eaten)
{
	int k=0;
	
	for(k=0;k<length;k++)
	{
		if(k==0)
			table[snake[k].y][snake[k].x] = 0;
		if(k>=1)
		{
			snake[k-1].x = snake[k].x;
			snake[k-1].y = snake[k].y;
		}
		if(k==length-1)
		{
			if(table[y][x]==9)	/*eating food*/
			{
				points+=5;
				length++;
				if(length>SNAKE_MAX)
					length = SNAKE_MAX;
				snake[length-1].x = x;
				snake[length-1].y = y;
				*eaten=true;
			}
			
			if((bufDirection==D_UP && direction==D_DOWN)
				||(bufDirection==D_DOWN && direction==D_UP)
				||(bufDirection==D_RIGHT && direction==D_LEFT)
				||(bufDirection==D_LEFT && direction==D_RIGHT))
			{
				/*revert*/
			}
			else
			{
				snake[k].x = x;
				snake[k].y = y;
			}
			break;
		}
	}
	for(k=0;k<length;k++)
	{
		table[snake[k].y][snake[k].x] = 7;
	}
}

void initSnake(int x,int y)
{
	/*initializing snake*/ 
	snake[2].x = x;
	snake[2].y = y;
	snake[1].x = x-1;
	snake[1].y = y;
	snake[0].x = x-2;
	snake[0].y = y;	
}

int gameOver()
{
	if(attempts==0)
	{
		int c;
		clear();	
		move(10,30);
		printColorString("GAME OVER",MENU_HEADING_COLOR);
		
		if(leader[9].score <= points)
		{
			readLeaderBoard();
			move(12,30);
			printw("Your Score %d: ",points);
			move(14,30);
			printw("Enter Your Name: ");
			echo();
			
			scanw("%s\0",leader[10].name);
			leader[10].score = points;
						
			sortLeaderBoard();
			noecho;
			writeLeaderBoard();
		}
		else
		{
			noecho;
			while(c!=10)			
				c = getch();
		}
		return 1;
	}
	return 0;
}

void newAttempt(int** table,int width,int height)
{
	int c;
	int i;
	int j;
	attempts--;
	int buf=attempts;
	int buf2=points;
	
	for(i=0;i<length;i++)
	{
		snake[i].x=0;
		snake[i].y=0;
	}
	
	for(i=0;i<width;i++)
		for(j=0;j<height;j++)
			table[i][j]=0;
			
	renewGlobals();
	initSnake(x,y);
	attempts=buf;
	points=buf2;
	direction=D_RIGHT;
	move(0,30);
	printColorString("ONE LIFE LESS",MENU_HEADING_COLOR);
	move(23,79);
	while(c!=10)
		c = getch();
	move(0,20);
	clrtoeol();
	clear;
	generateFood(table,width,height);
}

void renewGlobals()
{
	length=3;
	x=START_X;
	y=START_Y;
	points=0;
	direction=D_RIGHT;
	resume=false;
	setDifficulty(diff);
}

int changeDirection(int c)
{
	if(c == KEY_UP) return D_UP;
	else if(c == KEY_RIGHT) return D_RIGHT;
	else if(c == KEY_DOWN) return D_DOWN;
	else if(c == KEY_LEFT) return D_LEFT;
	else return direction;
}

void borderFunction(int** table,int width,int height)
{
	if(!borders)
	{
		if(x>width-2) x=1;
		else if(x<1) x=width-2;
		if(y>height-2) y=1;
		else if(y<1) y=height-2;
	}
	else
	{
		if(x>width-2)
			newAttempt(table,width,height);
		if(x<1)
			newAttempt(table,width,height);
		if(y>height-2)
			newAttempt(table,width,height);
		if(y<1)
			newAttempt(table,width,height);
	}
}

int generateFood(int** table,int width,int height)
{
	bool onSnake = false;
	srand((unsigned)time(0));
	
	/*generate food*/
  	int foodX = rand()%(width-2)+1;
	int foodY = rand()%(height-2)+1;
/*
	if(table[foodY][foodX] == 7)
	{
		while(1==1)
		{
			srand((unsigned)time(0));
		  	foodX = rand()%(width-2)+1;
			foodY = rand()%(height-2)+1;
			int i;
			for(i=0;i<length;i++)
			{
				if(foodX!=snake[i].x && foodY!=snake[i].y)
				{
				}
				else
					break;
			}
			if(i==length)
				break;
		}
	}
*/
	if(table[foodX][foodY] == 7)
	{
		foodX = 3;
		foodY = 2;
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
		  			foodX = rand()%(width-2)+1;
					foodY = rand()%(height-2)+1;
				}
			}
		}
	}
	
	table[foodX][foodY] = 9;
	if(onSnake) return 1;
	else return 0;
}

void printScore(int points)
{
	move(1,1);
	clrtoeol();
	printw("Score: %d",points);
}

void printAttempts(int attempts)
{
	move(3,1);
	clrtoeol();
	printw("Lives: %d",attempts);
}

void drawTable(int** table,int width,int height)
{
	int i;
	int j;
	for(i=0;i<width;i++)
	{
		move(1+i,20);
		for(j=0;j<height;j++)
		{
			/*corners*/
			if(i==0 && j==0)
			{
				if(diff>0)
				{
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
				}
				else
				{
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
				}
			}
			else if(i==0 && j==width-1)
			{
				if(diff>0)
				{
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
				}
				else
				{
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
				}
			}
			else if(i==height-1 && j==0)
			{
				if(diff>0)
				{
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
				}
				else
				{
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
				}
			}
			else if(i==height-1 && j==width-1)
			{
				if(diff>0)
				{
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
				}
				else
				{
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
				}
			}
			/*horizontal lines*/
			else if((i==0 && (j>0 && j<width-1))||(i==height-1 && (j>0 && j<width-1)))
			{
				if(diff>0)
				{
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
				}
				else
				{
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
				}
			}
			/*vertical lines*/
			else if((j==0 && (i>0 && i<height-1))||(j==width-1 && (i>0 && i<height-1)))
			{
				if(diff>0)
				{
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
					addch(' ' | COLOR_PAIR(BORDER_COLOR));
				}
				else
				{
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
					addch(' ' | COLOR_PAIR(MENU_BG_COLOR));
				}
			}
			/*empty squeres*/
			else if(table[i][j]==0 || table[i][j]==8)
			{
				addch(' ' | COLOR_PAIR(TABLE_COLOR));
				addch(' ' | COLOR_PAIR(TABLE_COLOR));
			}
			/*snake*/
			if(table[i][j]==7)
			{
				addch(' ' | COLOR_PAIR(SNAKE_COLOR));
				addch(' ' | COLOR_PAIR(SNAKE_COLOR));
			}
			/*food*/
			if(table[i][j]==9)
			{
				addch(' ' | COLOR_PAIR(FOOD_COLOR));
				addch(' ' | COLOR_PAIR(FOOD_COLOR)); 
			}									
		}
		printw("\n");
	}
	move(23,79);
}

void setDifficulty(int diff)
{
	if(diff==EASY)
	{
		attempts=5;
		speed=5;
		borders=false;
	}
	if(diff==NORMAL)
	{
		attempts=4;
		speed=3;
		borders=true; 
	}
	if(diff==HARD)
	{
		attempts=3;
		speed=1;
		borders=true;
	}
}

void menuSave()
{
	FILE *output_file;
	if((output_file = fopen("save.file", "w")) == NULL)
		fprintf(stderr, "Cannot open %s\n", "save.file"); 	
	
	int chkSum=0;
	int len=0;

	/*printw("%d",diff);*/
	diff = encrypt((int)diff);
	fwrite(&diff,sizeof(int),1,output_file);	
	chkSum+=diff;
	
	attempts = encrypt((int)attempts);
	fwrite(&attempts,sizeof(int),1,output_file);	
	chkSum+=attempts;
	
	speed = encrypt((int)speed);
	fwrite(&speed,sizeof(int),1,output_file);	
	chkSum+=speed;
	
	/*printw("%d",borders);*/
	borders = encrypt((int)borders);
	fwrite(&borders,sizeof(int),1,output_file);	
	chkSum+=borders;
	
	points = encrypt((int)points);
	fwrite(&points,sizeof(int),1,output_file);	
	chkSum+=points;
	
	direction = encrypt((int)direction);
	fwrite(&direction,sizeof(int),1,output_file);	
	chkSum+=direction;
	
	len = length;
	length = encrypt((int)length);	
	fwrite(&length,sizeof(int),1,output_file);
	chkSum+=length;
	
	x = encrypt((int)x);	
	fwrite(&x,sizeof(int),1,output_file);
	chkSum+=x;
	
	y = encrypt((int)y);	
	fwrite(&y,sizeof(int),1,output_file);
	chkSum+=y;

	int i;	
	for(i=0;i<len;i++)
	{
		snake[i].x = encrypt((int)snake[i].x);
		fwrite(&snake[i].x,sizeof(int),1,output_file);
		chkSum+=snake[i].x;
		
		snake[i].y = encrypt((int)snake[i].y);
		fwrite(&snake[i].y,sizeof(int),1,output_file);
		chkSum+=snake[i].y;
	}
	
	chkSum = encrypt((int)chkSum);
	fwrite(&chkSum,sizeof(int),1,output_file);
	
	fclose(output_file);
	
	move(0,0);
	clrtoeol();
	printColorString("Saved Sucessfully",MENU_BG_COLOR);
}

void menuLoad()
{
	FILE *output_file;
	if((output_file = fopen("save.file", "r")) == NULL)
		fprintf(stderr, "Cannot open %s\n", "save.file");
	else
	{
		int chkSum=0;
		int tstSum=0;
		int len=0;
		
		fread(&diff,sizeof(int),1,output_file);
		chkSum+=diff;
		diff = encrypt((int)diff);
		/*printw("%d",diff);*/
					
		fread(&attempts,sizeof(int),1,output_file);
		chkSum+=attempts;
		attempts = encrypt((int)attempts);		
				
		fread(&speed,sizeof(int),1,output_file);
		chkSum+=speed;
		speed = encrypt((int)speed);
			
		fread(&borders,sizeof(int),1,output_file);
		chkSum+=borders;
		borders = encrypt((int)borders);
	 	/*printw("%d",borders);*/
		
		fread(&points,sizeof(int),1,output_file);
		chkSum+=points;
		points = encrypt((int)points);
				
		fread(&direction,sizeof(int),1,output_file);
		chkSum+=direction;
		direction = encrypt((int)direction);
				
		fread(&length,sizeof(int),1,output_file);
		chkSum+=length;
		length = encrypt((int)length);
		len = length;
				
		fread(&x,sizeof(int),1,output_file);
		chkSum+=x;
		x = encrypt((int)x);
		
		fread(&y,sizeof(int),1,output_file);
		chkSum+=y;
		y = encrypt((int)y);
		
		int i;	
		for(i=0;i<len;i++)
		{
			fread(&snake[i].x,sizeof(int),1,output_file);
			chkSum+=snake[i].x;
			snake[i].x = encrypt((int)snake[i].x);
			
			fread(&snake[i].y,sizeof(int),1,output_file);
			chkSum+=snake[i].y;
			snake[i].y = encrypt((int)snake[i].y);			
		}
		
		fread(&tstSum,sizeof(int),1,output_file);
		tstSum = encrypt((int)tstSum);
		
		if(chkSum != tstSum) valid=false;		
	}
	fclose(output_file);
}

int encrypt(int num)
{
	num = num ^ 39977;
	return num;
}

void colorSupport()
{
	if (!has_colors())
	{
		endwin();
		printf("Colors are not supported");
		getch();
		exit(1);
	}
	start_color();
		
	init_pair(MENU_BG_COLOR, COLOR_BLUE, COLOR_WHITE);
	init_pair(MENU_HEADING_COLOR, COLOR_RED, COLOR_WHITE);
	init_pair(SNAKE_COLOR, COLOR_RED, COLOR_YELLOW);
	init_pair(SNAKE_HEAD_COLOR, COLOR_GREEN, COLOR_BLACK);
	init_pair(BORDER_COLOR,  COLOR_GREEN, COLOR_GREEN);
	init_pair(GAME_COLOR, COLOR_YELLOW, COLOR_WHITE);
	init_pair(FOOD_COLOR, COLOR_RED, COLOR_RED);
	init_pair(INFO_COLOR, COLOR_BLUE, COLOR_WHITE);
	init_pair(TABLE_COLOR, COLOR_BLUE, COLOR_BLACK);
}

void printColorString(char* c,int color)
{
	int i;
	for(i=0;c[i]!='\0';i++)
		addch(c[i] | COLOR_PAIR(color));
}

void readLeaderBoard()
{
	FILE *output_file;
	int chkSum=0;
	int tstSum=0;
	if((output_file = fopen("score.file", "r")) == NULL)
	{
		output_file = fopen("score.file", "w");
		writeLeaderBoard();	
	}
	else
	{
		int i;	
		for(i=0;i<10;i++)
		{
			int j;
			for(j=0;j<25;j++)
			{
				fread(&leader[i].name[j],sizeof(int),1,output_file);
				chkSum+=leader[i].name[j];
				leader[i].name[j] = encrypt((int)leader[i].name[j]);
			}
			
			fread(&leader[i].score,sizeof(int),1,output_file);
			chkSum+=leader[i].score;
			leader[i].score = encrypt((int)leader[i].score);
		}
	
		fread(&tstSum,sizeof(int),1,output_file);
		tstSum = encrypt((int)tstSum);
		
		move(0,0);
		printw("Read %d",tstSum);
		printw("\n");
		printw("Counted %d",chkSum);
		
		if(chkSum != tstSum) scoreValid=false;
	}
	fclose(output_file);
}

void writeLeaderBoard()
{
	FILE *output_file;
	if((output_file = fopen("score.file", "w")) == NULL)
		fprintf(stderr, "Cannot open %s\n", "score.file");
	int i;
	int chkSum=0;	
	
	for(i=0;i<10;i++)
	{
	
		int j;
		for(j=0;j<25;j++)
		{
			leader[i].name[j] = encrypt((int)leader[i].name[j]);
			fwrite(&leader[i].name[j],sizeof(int),1,output_file);
			chkSum+=leader[i].name[j];
		}
		
		leader[i].score = encrypt((int)leader[i].score);
		chkSum+=leader[i].score;
		fwrite(&leader[i].score,sizeof(int),1,output_file);
	}
	
	move(3,0);
	printw("CheckSum %d",chkSum);
	
	chkSum = encrypt((int)chkSum);
	fwrite(&chkSum,sizeof(int),1,output_file);	
	
	fclose(output_file);
}

void sortLeaderBoard()
{
	int i;
	int j;
	char nm[25];
	int sc;
	
	for (i=0; i<11-1; i++) 
	{
		for (j=0; j<11-1-i; j++)
			if (leader[j+1].score > leader[j].score) 
			{ 
				strcpy(nm,leader[j].name);
				sc = leader[j].score;
				strcpy(leader[j].name,leader[j+1].name);
				leader[j].score = leader[j+1].score;
				strcpy(leader[j+1].name,nm);
				leader[j+1].score = sc;
			}
	}
}

void printleaderBoard()
{
	char c;
	move(8,34);
	clrtoeol();
	printColorString("TOP SCORES",MENU_HEADING_COLOR);
	printw("\n");
	int i;
	
	for(i=0;i<10;i++)
	{
		move(10+i,34);
		printw("%s",leader[i].name);
		printw("     ");
		printw("%d",leader[i].score);
		printw("\n");
	}
	
	move(23,79);
	while(c!=10)		
		c = getch();
	clear();
}

void menuCredits()
{
	char c;
	move(8,34);
	clrtoeol();
	printColorString("CREDITS",MENU_HEADING_COLOR);
	printw("\n");
	
	move(10,34);
	printw("Game Author: Vitalijs Shakels;");
	move(11,34);
	printw("Year: 2008;");
	move(12,34);
	printw("Enjoy");
	move(23,79);
	
	while(c!=10)		
		c = getch();
	clear();
}

void menuRules()
{
	FILE *output_file;
	if((output_file = fopen("rules.txt", "r")) == NULL)
		output_file = fopen("rules.txt", "w");
	else
	{
		move(8,34);
		clrtoeol();
		printColorString("GAME RULES",MENU_HEADING_COLOR);
		printw("\n");
		
		int i;
		int j=0;
		move(10+j,0);
		/* read back the file */
		for(;;)
		{
			i = fgetc(output_file);
			if(i == EOF) break;
			printw("%c",i);
		}
	}
	fclose(output_file);	
	move(23,79);
	getch();
	clear();
}

int mpHostJoinMenu()
{
	const int MAX = 2;
	char* menu[MAX];
	
	menu[0] = "    Host Game\0";
	menu[1] = "    Join Existing Game\0"; 
	
	move(8,34);
	clrtoeol();
	printColorString("MULTIPLAYER",MENU_HEADING_COLOR);
	
	int i;
	for(i=0;i<MAX;i++)	/*printing whole menu*/
	{
		move(10+i,30);
		printw("%s",menu[i]);
		printw("\n");
	} 
	
	bool select = false;
	
	int index = 0;
	move(10+index,30);
	addch(ACS_DIAMOND);
	
	while(select != true)	/*controling menu*/
	{
		move(23,79);
		int c = getch();
		
		move(10+index,30);
		printw("  ");
			
		if(c == KEY_UP)
		{
			index--;
			if(index<0)
				index=MAX-1;
		}
		if(c == KEY_DOWN)
		{
			index++;
			if(index>MAX-1)
				index=0;
		}
		if(c == 10)
			select = true;
			
		move(10+index,30);
		addch(ACS_DIAMOND);
	}
	
	return index;
}

int main(int argc,char *argv[])
{
	initscr();
	keypad(stdscr, true);	/*allows input from keyboard*/
	noecho();	/*not writing pressed chars*/
	colorSupport();
	bool exit = false;
	
	while(!exit)
	{
		switch(mainMenu())
		{
			case 0: /*new game*/
			{
				readLeaderBoard();
				clear();
				renewGlobals();
				menuNewGame();
				break;
			}
			case 1:	/*resume game*/
			{
				if(resume)
				{
					clear();
					menuNewGame();
				}
				move(0,0);
				clrtoeol();
				printColorString("No Games To Resume",MENU_BG_COLOR); 
				break;
			}
			case 2:	/*multiplayer*/
			{
				clear();
				move(0,0);
				clrtoeol();
				int result = mpHostJoinMenu();
				if(result == 0) 
				{
					clear();
					mpHost();
				}
				if(result == 1) 
				{
					clear();
					mpJoin();
				}
				break;
			}
			case 3:	/*top scores*/
			{
				readLeaderBoard();
				if(scoreValid)
				{
					clear();
					printleaderBoard();
				}
				else
				{
					clear();
					move(0,0);
					clrtoeol();
					printColorString("LOAD DATA ARE CORRUPTED, REMOVE SCORE.FILE",MENU_HEADING_COLOR);
					move(23,79);
				}
				break;
			}
			case 4:	/*game rules*/
			{
				clear();
				menuRules();
				break;
			}
			case 5:	/*credits*/
			{
				clear();
				menuCredits();
				break;
			}
			case 6:	/*difficulty*/
			{
				clear();
				diff = menuDifficulty();
				setDifficulty(diff);
				
				clear();
				renewGlobals();
				menuNewGame();				
				
				break;
			}
			case 7:	/*load*/ 
			{
				clear();
				menuLoad();
				if(valid)
				{
					clear();
					resume=true;					
					menuNewGame();
				}
				else
				{
					clear();
					move(0,0);
					clrtoeol();
					printColorString("LOAD DATA ARE CORRUPTED, REMOVE SAVE.FILE",MENU_HEADING_COLOR);
					move(23,79);
				}
				break;
			}
			case 8:	/*save*/
			{
				if(resume)
				{
					clear();
					menuSave();
				}
				else
				{
					clear();
					move(0,0);
					clrtoeol();
					printColorString("No Active Games",MENU_BG_COLOR); 
				}
				break;
			}
			case 9:	/*exit*/
			{
				exit=true;
				break;
			}
			default:
			{
				printw("Illegal Choise");
				endwin();
			}
		}
	}
	 
	endwin();
	return 0;
}




