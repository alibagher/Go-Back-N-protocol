JCC = javac
default: sender.class receiver.class packet.class globals.class

sender.class: sender.java
	$(JCC) sender.java

receiver.class: receiver.java
	$(JCC) receiver.java 

packet.class: packet.java
	$(JCC) packet.java

globals.class: globals.java
	$(JCC) globals.java

clean:
	rm *.class
