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

int mpHost(int* hostSock, int* clientSock)
{
   	/*
    initialises socket connection, sets its 
    number into parameter sock, and returns result
    */
	clear();
    printw("Creating connection... ");
    refresh();
    //int sockfd;
    struct sockaddr_in serv_addr, cli_addr;
    char buffer[256];
    int n;
    
    //create socket
    *hostSock = socket(AF_INET, SOCK_STREAM, 0);
    if (*hostSock < 0)
	{
    	//failed to create
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
    
    bzero(buffer,256);
	n = read(*clientSock,buffer,255);
	if (n < 0) error("ERROR reading from socket");
		printw("Here is the message: %s",buffer);
		
	n = write(*clientSock,"I got your message",18);
	if (n < 0) error("ERROR writing to socket");
    
    //return number to the parameter
    //*sock = sockfd;
    //if success
    printw("success!\n");
    return 1;
}

int mpJoin(int* clientSock, char* ipAddress)
{
    /* initialises client side connection to the specified address */        
    
    clear();
    printw("Connecting to host... ");
    refresh();
    
    //create socket
    //AF_INT - constant for internets protocols, to communicate through internet
    //SOCK_STREAM - smth unknown
    //0 - lets OS automatically choose protocol, 6 would be TCP
    *clientSock = socket(AF_INET, SOCK_STREAM, 0);
	if (*clientSock < 0)
	{
		//failed to create
		error("Failed to create socket");               
		return -1;
    }       
    
    struct hostent *server;
    char buffer[256];
    int n; 
    echo();
    //associate server with IP address given
    server = gethostbyname(ipAddress);      
    if (!server)
	{
		error("ERROR, no such host");
		return -1;
    }
    
    struct sockaddr_in serv_addr, cli_addr;
    //add info about server to the structure
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, (char *)&serv_addr.sin_addr.s_addr, server->h_length);
    serv_addr.sin_port = htons(PORT_NUM);
    if (connect(*clientSock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
		error("ERROR connecting");
		return -1;
    }
    
	n = write(*clientSock,"Hallo World",18);
	if (n < 0)
		error("ERROR writing to socket");
	bzero(buffer,256);
	n = read(*clientSock,buffer,255);
	if (n < 0) 
		error("ERROR reading from socket");
	printw("%s\n",buffer);
	    
	//if success
    printw("success!\n");
    return 1;
}
