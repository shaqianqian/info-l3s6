#include <stdio.h>
#include <sys/types.h>  
#include <sys/socket.h>  
#include <netinet/in.h>  
#include <arpa/inet.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>  
  
int main(int argc, char *argv[])  
{  
    int server_sockfd;  
    int len;  
    struct sockaddr_in  emetteur;   //la structure de l'adresse de serveur  
    struct sockaddr_in remote_addr; //la structure de l'adresse de cliente 
    unsigned int sin_size;  
    char buf[BUFSIZ];  //buffer de data  
    memset(&emetteur,0,sizeof(emetteur)); //zero le data 
    emetteur.sin_family=AF_INET; //design to ip communication  
    emetteur.sin_addr.s_addr=INADDR_ANY;//ip is allowed to connection with all the address in the host  
    emetteur.sin_port=htons(8000); //lumero de post   
      
    //IPv4// 
    if((server_sockfd=socket(PF_INET,SOCK_DGRAM,0))<0)  
    {    
        perror("error socket");  
        return 1;  
    }  

    sin_size=sizeof(struct sockaddr_in);
    // recevoir les datas de cliente et emetteur a cliente
    if(recvfrom(server_sockfd,buf,BUFSIZ,0,(struct sockaddr *)&remote_addr,&sin_size)<0)  
    {  
        perror("recvfrom");  
        return 1;  
    }  
    printf("contents: %s\n",buf);  
    close(server_sockfd);  
    return 0;  
} 
