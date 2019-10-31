#### CNT5106C – Fall 2019 | Project 1 - FTP Client & Server 
#### Prafful Mehrotra UFID: 1099-5311

# Project Directory Structure
```
.
├── Client1
    ├── Client1.java
    └── client1.txt
├── Client2
    ├── Client2.java
    └── client2.txt
└── FTPServer
    ├── MultiServerThread.java
    ├── Server.java
    ├── Server1.txt
    └── Server2.txt
└── README.md
└── Readme.pdf
```

# About the project

This project is a simple ftpclient which can be used to send and upload files to and from a server.

# Implementation & Usage
- Once a server is booted, it waits for clients to send in connection requests.
- For every new client, a new thread is started by the server (MultiServerThread) which is responsible for interaction with corresponding client.
- Following are the possible commands from the client:


| Command        | var1           | var2  |description
| :-------------:|:-------------:|:-----:|:----:|
| ftpclient|IP|PORT|connect to a server|
| dir| | |list files present on the server|
| get|filename| |downlaod a file from the server|
|upload|filename| | send a file from the client directory to server|
|ls| | |list files present in the client directory|