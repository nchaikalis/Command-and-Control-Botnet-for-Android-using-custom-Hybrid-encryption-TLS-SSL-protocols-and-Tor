# Command-and-Control-Botnet-for-Android-using-custom-Hybrid-encryption-TLS-SSL-protocols-and-Tor

Command and Control Botnet for Android using custom Hybrid encryption, TLS/SSL protocols and Tor - University Assignment  
  
* Distributed System with a GUI written in Java.  
* Python script using Scapy to DDoS the victims.  
* Using TLS/SSL, IPsec and Tor Network.  

The purpose of this assignment is to DDoS a DTLS (SSL/TLS for UDP) Server.
For this purpose we created a server, and we created a custom DTLS protocol.

The Botmaster establish a communication with the Command and Control Server (C&C) via TOR dark web.
When the connection is established the C&C creating a pair of Private and Public key and is sending the Public Key to the Botmaster. The Botmaster creates a Secret Key, and he sends the Secret Key to the C&C encrypted by the C&C’s Public Key.  At this point both Botmaster and C&C are communicating with TOR, and they are encrypting their messages using the same Secret Key. So when a message enters or leaves TOR is still encrypted.

At the same time the Bots are connected to the C&C.
The channel between the Bots (Android Bots and Desktop Bots) and C&C is encrypted using SSL/TLS with self sign certificates created by OpenSSL.
The C&C is forwarding the orders from the Botmaster to the Bots. So the Bots are attacking to the DTLS server using the DTLS channel.
