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


void error(char *msg)
{
    perror(msg);
    exit(1);
}

int mpHost()
{
	clear();
	move(0,0);
	echo();
    int sockfd, newsockfd, portno, clilen;
    char buffer[256];
    struct sockaddr_in serv_addr, cli_addr;
    int n;

    if (PORT_NUM == 0) 
    {
        error("ERROR, no port provided\n");
        return 0;
    }
     
	sockfd = socket(AF_INET, SOCK_STREAM, 0);

    
    if (sockfd < 0) 
       error("ERROR opening socket");
    
        
    bzero((char *) &serv_addr, sizeof(serv_addr));
    portno = (char*)PORT_NUM;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);
    if (bind(sockfd, (struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0) 
		error("ERROR on binding");
    
    listen(sockfd,5);
    clilen = sizeof(cli_addr);
    
    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
    if (newsockfd < 0) 
         error("ERROR on accept");     
    bzero(buffer,256);
    n = read(newsockfd,buffer,255);
    if (n < 0) error("ERROR reading from socket");
    
    printw("Here is the message: %s\n",buffer);
    n = write(newsockfd,"I got your message",18);
    if (n < 0) error("ERROR writing to socket");

    getch();
    return 0;
}

int mpJoin()
{
	clear();
	move(0,0);
	echo();
	char* ipAddr = "127.0.0.1";
	int sockfd, portno, n;
	struct sockaddr_in serv_addr;
	struct hostent *server;
	
	char buffer[256];
	
	if (ipAddr == "" && PORT_NUM == 0) 
	{
		printw(stderr,"usage hostname port\n");
		exit(0);
	}
	
	portno = (char*)PORT_NUM;
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd < 0) 
		error("ERROR opening socket");
	server = gethostbyname((char*)ipAddr);
	if (server == NULL) 
	{
		printw(stderr,"ERROR, no such host\n");
		return 0;
	}
	bzero((char *) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	bcopy((char *)server->h_addr, 
	(char *)&serv_addr.sin_addr.s_addr,server->h_length);
	
	serv_addr.sin_port = htons(portno);
	if (connect(sockfd,&serv_addr,sizeof(serv_addr)) < 0) 
		error("ERROR connecting");
		
	printw("Please enter the message: ");
	bzero(buffer,256);
	//scanw("%s",buffer);
	fgets(buffer,255,stdin);
	n = write(sockfd,buffer,strlen(buffer));
	if (n < 0) 
		error("ERROR writing to socket");
	bzero(buffer,256);
	n = read(sockfd,buffer,255);
	if (n < 0) 
		error("ERROR reading from socket");
	printw("%s\n",buffer);
	clear();
	getch();
	return 0;
}
