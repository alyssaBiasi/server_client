Multithreaded Server and Client - Runs on localhost

Server opens a ServerSocket on the first port (40101) and starts a thread of type ServerThread, to handle client connections. Server reads user input until the exit command is sent.

The server thread starts a ResponseThread thread to handle the multicast messaging. The server thread listens for client connections. It can serve up to 5 clients at a time.

Client establishes a connection to the server thread. The client sends a request to join the multicast group.

	- Joining: The server thread determines if there's a port available in the group. If so, it assigns a port to the client. It then sends the appropriate message to the client and sends the updated group list to the response thread.

If the group is full, the client will close. Otherwise the client will create a ClientThread thread. Client will read user input until the leave group command is sent while the client thread listens to its assigned port to receive the multicast message.

The response thread loops continuously checking if a) there are clients in the group or b) it has been a minute since the last transmission. If it's time to send, the response thread establishes a connection to  the first client in the group. A string is built consisting of the time and date followed by the ports of all the clients in the group, i.e. "time date <port1> <port2> <port3>". This string is sent to the first client and the connection is closed.

The client receives this string and tokenises it. It adjusts the time before printing and passing it on.

It rebuilds the string with the time, date and ports, removing its own port number. The time and date portion is tokenised into its separate values. These are used to create a Calendar object based on the time the message was sent. The difference between when the message was sent and the current time is calculated and added to the the time received. It then prints the new time and date string to the screen. If there are more clients in the list, it will then open a connection to the next client and pass on the amended message. It will then go back to listening to its port.

When Client receives the exit prompt, it connects to the server thread and notifies it, sending	its port number.

	-Leaving: The server thread finds the clients port number in the array and removes it. The updated list is sent to the response thread.

When the server's user prompts to exit the response thread sends a message to the first	client notifying it that it is shutting down. The client then passes it own so that all clients can disconnect.

Server exit command: "Exit."	
Client exit command: "Leave."	