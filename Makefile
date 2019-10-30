JCC = javac
default: sender.class receiver.class packet.class

sender.class: sender.java
	$(JCC) sender.java

receiver.class: receiver.java
	$(JCC) receiver.java 

packet.class: packet.java
	$(JCC) packet.java

clean:
	rm *.class
