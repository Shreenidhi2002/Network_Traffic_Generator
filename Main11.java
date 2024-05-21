import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import com.sun.nio.sctp.SctpStandardSocketOptions;
import java.net.*;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.util.HashSet;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import java.nio.charset.StandardCharsets;



public class Main8 {
   
    public static void main(String[] args) {
	   	String  mode ="" ;
	   	String protocol ="";
	   	String serverIp="";
	   	String clientIp="";
	  	int serverPort=0,recvBuf=0,sendBuf=0,sockBuf=0 ,clientPort=0,clientCount=0 ;
	 	for(int i=0;i<args.length;i++){
		  	switch(args[i]){
		  		case "-mode" :
		  			if(i<args.length-1){
		  				mode = args[i+1];
		  				i++;
		  			}
		  			else{
		  				System.err.println("missing value for mode");
		  				System.exit(1);
		  			}
		  			break;
		  		
		  		case "-protocol":
		  			if(i<args.length-1){
		  				protocol = args[i+1];
		  				i++;
		  			}
		  			else{
		  				System.err.println("missing value for protocol");
		  				System.exit(1);
		  			}
		  			break;
		  		
		  		case "-servip":
		  			if(i<args.length-1){
		  				serverIp = args[i+1];
		  				i++;
		  			}
		  			else{
		  				System.err.println("missing value for serverip");
		  				System.exit(1);
		  			}
		  			break;
		  		
		  		case "-servport":
		  			if(i<args.length-1){
		  				serverPort = Integer.parseInt(args[i+1]);
		  				i++;
		  			}
		  			else{
		  				System.err.println("missing value for serverport");
		  				System.exit(1);
		  			}
		  			break;
		  		
		  		
		  		case "-clientIp":
		  			if(i<args.length-1){
		  				clientIp = args[i+1];
		  				i++;
		  			}
		  			else{
		  				System.err.println("missing value for clientIp");
		  				System.exit(1);
		  			}
		  			break;
		  		
		  		case "-clientCount":
		  			if(i<args.length-1){
		  				clientCount  = Integer.parseInt(args[i+1]);
		  				i++;
		  			}
		  			else{
		  				System.err.println("missing value for clientCount ");
		  				System.exit(1);
		  		}
		  		break;
		  		
		  		case "-clientPort":
		  			if(i<args.length-1){
			  			clientPort= Integer.parseInt(args[i+1]);
			  			i++;
		  			}
		  			else{
				  		System.err.println("missing value for clientPort");
				  		System.exit(1);
		  			}
		  		break;
		  		
		  		case "-sockBuf":
		  			if(i<args.length-1){
			  			sockBuf = Integer.parseInt(args[i+1]);
			  			i++;
		  			}
		  			else{
				  		System.err.println("missing value for sockBuf ");
				  		System.exit(1);
		  			}
		  		
		  		break;
		  		
		  		case "-sendBuf":
		  			if(i<args.length-1){
			  			sendBuf= Integer.parseInt(args[i+1]);
			  			i++;
		  			}
		  			else{
				  		System.err.println("missing value for sendBuf");
				  		System.exit(1);
		  			}
		  		break;
		  		
		  		case "-recvBuf":
		  			if(i<args.length-1){
			  			recvBuf = Integer.parseInt(args[i+1]);
			  			i++;
		  			}
		  			else{
				  		System.err.println("missing value for recvBuf");
				  		System.exit(1);
		  			}
		  		break;
		  		
		  		default:
			  		System.err.println("Unknown option"+args[i]);
			  		System.exit(1);
			  		
		  		
		  	}
  		}
  	
  		switch(mode) {
        		case "server": 
        			switch(protocol) {
        				case "tcp":
        					break;
        				case "udp":
        					break;
        				case "sctp":
						handleSctpServer handlesctpServer = new handleSctpServer(serverIp,serverPort,recvBuf,sendBuf,sockBuf);
						handlesctpServer.handleServer();
						break;
        				default:
        					return;
        			}
        			break;
        		case "client" : 
        			switch(protocol) {
        				case "tcp":
        					break;
        				case "udp":
        					break;
        				case "sctp":
        					ExecutorService executor = Executors.newCachedThreadPool();
						for (int i = 0; i < clientCount; i++) {
						    executor.submit(new handleSctpClient(serverIp, serverPort, clientIp, clientPort + i, recvBuf, sendBuf, sockBuf));
						}
						executor.shutdown();
        					break;
        				default:
        					return;
        			}
        			break;
        		default:
        			return;
       		 }
        
        }
    
}


class handleSctpClient implements Runnable{
		SctpChannel sctpChannel;
		int sockBuf;
		int sendBuf;
		int recvBuf;
		String serverIp,clientIp;
		int serverPort,clientPort;
		ByteBuffer buffer1 , buffer2;
		public handleSctpClient(String serverIp,int serverPort,String clientIp,int clientPort,int recvBuf,int sendBuf,int sockBuf){			
    			this.sockBuf = sockBuf;
    			this.sendBuf = sendBuf;
			    this.recvBuf =recvBuf;
			    this.serverIp = serverIp;
			    this.clientIp = clientIp;
			    this.clientPort = clientPort;
			    this.serverPort = serverPort;
			    buffer1 = ByteBuffer.allocateDirect(recvBuf);
			    buffer2 = ByteBuffer.allocateDirect(sendBuf);
		}
		public void run()  {
		
			    try {
			    boolean continueLoop = true;
			    Scanner sc = new Scanner(System.in);
			    while(continueLoop){
			    System.out.println("Services available:\n\t1.Receive data(enter->1)\n\t2.Send data(enter->2)\n");
			    int userChoice = sc.nextInt();
			    sc.nextLine(); 
			    sctpChannel = SctpChannel.open();
			    sctpChannel.setOption(SctpStandardSocketOptions.SO_SNDBUF, sockBuf);
                	    sctpChannel.setOption(SctpStandardSocketOptions.SO_RCVBUF, sockBuf);
		            System.out.println("client socket created successfully");
		            System.out.println("Client bound to client IP: " + clientIp + " and port: " + clientPort);
			    sctpChannel.connect(new InetSocketAddress(serverIp, serverPort));
			    System.out.println("Client connected to server IP: " + serverIp + " and port: " + serverPort);
			  try {
					String choiceMessage = Integer.toString(userChoice);
					buffer2.clear();
					buffer2.put(choiceMessage.getBytes());
					buffer2.flip();

				       
					InetSocketAddress serverAddress = new InetSocketAddress(serverIp, serverPort);
					MessageInfo messageInfo = MessageInfo.createOutgoing(serverAddress, 0);

					// Send the message with the appropriate message info
					if (messageInfo != null) {
					    sctpChannel.send(buffer2, messageInfo);
					} else {
					    System.out.println("MessageInfo is null. Unable to send data.");
					}
            		 }
            		 catch (Exception e) {
               				 e.printStackTrace();
          		  }
			    if(userChoice ==1){
			    String fileName = "received_data_" + clientPort + "_" + new Date().getTime() + ".txt";
                            FileChannel fileChannel = FileChannel.open(Paths.get(fileName),StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			    
			 
			    while (true) {
				    System.out.println("Waiting for data from the server...");
				    buffer1.clear();
				    MessageInfo messageInfo = sctpChannel.receive(buffer1, null, null);
				    int bytesRead = messageInfo.bytes(); 
				    System.out.println("Bytes read: " + bytesRead);
				    
				    if (bytesRead > 0) {
					buffer1.flip();
					byte[] receivedBytes = new byte[bytesRead];
					buffer1.get(receivedBytes);
					String receivedMessage = new String(receivedBytes);
					//System.out.println("Received message: " + receivedMessage);
					
					
					if (receivedMessage.trim().equals("END_OF_TRANSMISSION")) {
					    System.out.println("Server has finished sending the data");
					    fileChannel.close();
					    break;  
					}
				    
				    buffer1.flip();
					    fileChannel.write(buffer1);
					    buffer1.clear();
					    }
					     else {
					System.out.println("No bytes received from the server");
				    }
				}

}
if(userChoice ==2){
	 System.out.println("Enter the path of the file to send:");
    String filePath = sc.nextLine();
    try (FileChannel fileChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ)) {
        ByteBuffer buffer = ByteBuffer.allocate(sendBuf);
        int bytesRead;
        while ((bytesRead = fileChannel.read(buffer)) != -1) {
                    System.out.println("Bytes read: " + bytesRead);
                    buffer.flip();
                    MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
                    while (buffer.hasRemaining()) {
                            int remainingBytes = buffer.remaining();
                            int bytesToSend = Math.min(remainingBytes, sockBuf);

                            ByteBuffer slice = buffer.slice();
                            slice.limit(bytesToSend);
                            int bytesSent = sctpChannel.send(slice, messageInfo);
                            System.out.println(bytesSent);
                            buffer.position(buffer.position() + bytesSent);

                            while (bytesSent == 0) {
                                Thread.sleep(100);
                                bytesSent = sctpChannel.send(slice, messageInfo);
                            }
                        }
                    

                    buffer.compact();
                }
                MessageInfo endOfTransmissionMessage = MessageInfo.createOutgoing(null, 0);
                ByteBuffer endOfTransmissionSignal = ByteBuffer.wrap("END_OF_TRANSMISSION".getBytes());
                sctpChannel.send(endOfTransmissionSignal, endOfTransmissionMessage);
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

}

	System.out.println("Do you want to close the connection established on port " + clientPort + "?(y/n)");

	String userInput = sc.nextLine().trim();
	if (userInput.equalsIgnoreCase("y")) {
	    sctpChannel.close();
	    continueLoop = false;
	}

				}
				}
				
        			catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
}
}


class handleSctpServer {
	SctpServerChannel sctpServerChannel;
	int sockBuf;
	int sendBuf;
	int recvBuf;
	String serverIp;
	int serverPort;
	ByteBuffer buffer1 , buffer2;
        public handleSctpServer(String serverIp,int serverPort,int recvBuf,int sendBuf,int sockBuf) {
		this.sockBuf = sockBuf;
		this.sendBuf = sendBuf;
		this.recvBuf =recvBuf;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		buffer1 = ByteBuffer.allocateDirect(recvBuf);
		buffer2 = ByteBuffer.allocateDirect(sendBuf);
        }
    
	public void handleServer()  {
    		try {
			sctpServerChannel = SctpServerChannel.open();
			System.out.println("server socket creation successfull");
			sctpServerChannel.bind(new InetSocketAddress(serverIp,serverPort),7000);
			System.out.println("bind successful");
			while(true) {
				SctpChannel sctpChannel = sctpServerChannel.accept();
				sctpChannel.setOption(SctpStandardSocketOptions.SO_SNDBUF, sockBuf);
                		sctpChannel.setOption(SctpStandardSocketOptions.SO_RCVBUF, sockBuf);
				Thread clientThread = new Thread(new clientHandler(sctpChannel,buffer1,buffer2,sockBuf,recvBuf,sendBuf));
				clientThread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}



class clientHandler implements Runnable {
    SctpChannel sctpChannel = null;
    ByteBuffer buffer1;
    ByteBuffer buffer2;
    int sockBuf, recvBuf,sendBuf;

    clientHandler(SctpChannel sctpChannel, ByteBuffer buffer1, ByteBuffer buffer2, int sockBuf, int recvBuf,int sendBuf) {
        this.sctpChannel = sctpChannel;
        this.buffer1 = buffer1;
        this.buffer2 = buffer2;
        this.sockBuf = sockBuf;
        this.recvBuf = recvBuf;
        this.sendBuf = sendBuf;
    }

    public void run() {
        buffer2.clear();
        try {
            sctpChannel.receive(buffer2, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer2.flip();
        String choiceMessage = StandardCharsets.UTF_8.decode(buffer2).toString();

        if (choiceMessage.equals("1")) {
            handleOption1();
        } else if (choiceMessage.equals("2")) {
            handleOption2();
        } else {
            System.out.println("Invalid choice received from client: " + choiceMessage);
        }
    }

    private void handleOption1() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(sendBuf);
            try (FileChannel fileChannel = FileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
                int bytesRead;
                while ((bytesRead = fileChannel.read(buffer)) != -1) {
                    System.out.println("Bytes read: " + bytesRead);
                    buffer.flip();
                    Set<SocketAddress> remoteAddresses = sctpChannel.getRemoteAddresses();
                    for (SocketAddress address : remoteAddresses) {
                        MessageInfo messageInfo = MessageInfo.createOutgoing(address, 0);
                        while (buffer.hasRemaining()) {
                            int remainingBytes = buffer.remaining();
                            int bytesToSend = Math.min(remainingBytes, sockBuf);

                            ByteBuffer slice = buffer.slice();
                            slice.limit(bytesToSend);
                            int bytesSent = sctpChannel.send(slice, messageInfo);
                            System.out.println(bytesSent);
                            buffer.position(buffer.position() + bytesSent);

                            while (bytesSent == 0) {
                                Thread.sleep(100);
                                bytesSent = sctpChannel.send(slice, messageInfo);
                            }
                        }
                    }

                    buffer.compact();
                }
                MessageInfo endOfTransmissionMessage = MessageInfo.createOutgoing(null, 0);
                ByteBuffer endOfTransmissionSignal = ByteBuffer.wrap("END_OF_TRANSMISSION".getBytes());
                sctpChannel.send(endOfTransmissionSignal, endOfTransmissionMessage);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

 
private void handleOption2() {
    try {
        String fileName = "received_data_" + sctpChannel.getRemoteAddresses().iterator().next().toString().replaceAll("[^\\d]", "") + "_" + System.currentTimeMillis() + ".txt";

        try (FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            while (true) {
                System.out.println("Waiting for data from the client...");
                buffer1.clear();
                MessageInfo messageInfo = sctpChannel.receive(buffer1, null, null);
                int bytesRead = messageInfo.bytes();
                System.out.println("Bytes read: " + bytesRead);

                if (bytesRead > 0) {
                    buffer1.flip();
                    byte[] receivedBytes = new byte[bytesRead];
                    buffer1.get(receivedBytes);
                    String receivedMessage = new String(receivedBytes);
                    //System.out.println("Received message: " + receivedMessage);

                    if (receivedMessage.trim().equals("END_OF_TRANSMISSION")) {
                        System.out.println("Client has finished sending the data");
                        break;
                    }

                    buffer1.flip();
                    fileChannel.write(buffer1);
                    buffer1.clear();
                } else {
                    System.out.println("No bytes received from the client");
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    }


