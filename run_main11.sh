javac Main8.java

if [ "$1" == "server" ] ; then
	java Main8 -mode server -protocol sctp -servip 127.0.0.1 -servport 8080 -sockBuf 103000 -sendBuf 120000 -recvBuf 120000
	
elif [ "$1" == "client" ]; then
	java Main8 -mode client -protocol sctp -servip 127.0.0.1 -servport 8080 -sockBuf 103000 -sendBuf 50000 -recvBuf 50000 -clientPort 8081 -clientCount 1 -clientIp 127.0.0.1
	
fi

