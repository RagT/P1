/*
Raghu Tirumala
processes.cpp

Program meant to imitate the command
ps -A | grep argv[1] | wc -l
*/

#include <stdio.h>
#include <iostream>
#include <unistd.h>    //for fork, pipe
#include <stdlib.h>    //for exit
#include <sys/wait.h>  //for wait
using namespace std;

int main(int argc, char *argv[])
{
	if(argc != 2)
	{
		perror("Incorrect number of arguments.");
	}
	pid_t pid; //process id	
	const int READ = 0;
	const int WRITE = 1;
	int fd1[2], fd2[2]; //file descriptors for pipes

	if(pipe(fd1) < 0) //pipe 1 created for use between ps and grep
		perror("pipe1 error");
	else if(pipe(fd2) < 0) //pipe 2 created for use between grep and wc
		perror("pipe2 error");
	else
	{
		pid = fork();
		if(pid < 0)
		{
			perror("Fork error child.");
		}
		else if(pid == 0) //Child Process
		{
			pid = fork();
			if(pid < 0)
			{
				perror("Fork error grandchild");
			}
			else if(pid == 0) //Grandchild process
			{
				pid = fork();
				if(pid < 0)
				{
					perror("Fork error great grandchild");
				}
				else if(pid == 0) //Great Grandchild process
				{
					close(fd1[READ]); //close pipe 1 read
					dup2(fd1[WRITE], WRITE); //pipe 1 write to stdout
					execlp("ps", "ps", "-A", NULL);
				}
				else
				{
					close(fd1[WRITE]); //close pipe 1's write
					dup2(fd1[READ], READ); //pipe 1 read to stdin
					dup2(fd2[WRITE], WRITE); //pipe 2 write to stdout
					execlp("grep", "grep", argv[1], NULL);
				}
			}
			else
			{
				//close pipes to allow children to terminate
				close(fd1[READ]);
				close(fd1[WRITE]);
				close(fd2[WRITE]);

				dup2(fd2[READ], READ); //pipe 2 read to stdin
				execlp("wc", "wc", "-l", NULL);
			}
		}
		else //Parent
		{
			close(fd1[READ]);
			close(fd1[WRITE]);
			close(fd2[READ]);
			close(fd2[WRITE]);
			wait(NULL);
		}
	}
	return 0;
}