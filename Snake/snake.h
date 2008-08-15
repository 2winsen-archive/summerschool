/* COMPILE
gcc snake.c -o snake -lncurses -ansi 
*/
#ifndef SNAKE_H_
#define SNAKE_H_

	#define SNAKE_MAX 400
	#define D_UP 1 
	#define D_RIGHT 2
	#define D_DOWN 3
	#define D_LEFT 4
	#define START_X 3
	#define START_Y 15
	#define EASY 0
	#define NORMAL 1
	#define HARD 2
	
	#define MENU_BG_COLOR 1 
	#define MENU_HEADING_COLOR 2 
	#define SNAKE_COLOR 3
	#define SNAKE_HEAD_COLOR 4
	#define BORDER_COLOR 5
	#define GAME_COLOR 6
	#define FOOD_COLOR 7
	#define INFO_COLOR 8
	#define TABLE_COLOR 9
	
	#define PORT_NUM 9769
	
	
	int mainMenu();
	int menuDifficulty();
	int menuNewGame();
	void moveSnake(int **table,int x,int y,int bufDirection,bool *eaten);
	void initSnake(int x,int y);
	int gameOver();
	void newAttempt(int** table,int width,int height);
	void renewGlobals();
	int changeDirection(int c);
	void borderFunction(int** table,int width,int height);
	int generateFood(int** table,int width,int height);
	void printScore(int points);
	void printAttempts(int attempts);
	void drawTable(int** table,int width,int height);
	void setDifficulty(int diff);
	void menuSave();
	void menuLoad();
	int encrypt(int num);
	void colorSupport();
	void printColorString(char* c,int color);
	void readLeaderBoard();
	void writeLeaderBoard();
	void sortLeaderBoard();
	void printleaderBoard();
	void menuCredits();
	void menuRules();
	int mpHostJoinMenu();
	int mpHost();
	int mpJoin();
	int error(char *msg);
	int mpHostNewGame(int clientSock);
	int mpClientNewGame(int clientSock);
	int sendTable(int clientSock,int** table,int width,int height);
	int sendLenDir(int clientSock);
	void mpMoveSnake(int **table,int mpX,int mpY,int bufDirection,bool *eaten);
	void mpBorderFunction(int** table,int width,int height);
	int mpChangeDirection(int c);
	void mpInitSnake(int x,int y);
	int matchEnd(int** table,int width,int height,int clientSock,int points);
	

#endif /*SNAKE_H_*/
