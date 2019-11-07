# Go-Back-N-protocol

'make' will compile all of the needed files.

The Emulator can be run for Example on host1: ./nEmulator-linux386 9991 host2 9994 9993 host3 9992 1 0 0
The receiver can be run using the 'receiver.sh' script. Ex. on host2: ./receiver.sh host1 9993 9994 output.txt
The sender can be run using the 'sender.sh' script. Ex. on host3: ./sender.sh host1 9991 9992 small.txt 

seqnum.log records all of the sequence number the sender has sent.
ack.log records all of the acks the sender recieves.
time.log records the time the program took to send the file in miliseconds.

A report on the time experiment is in the file "Time Experiment Report" and all of the raw data and more graphs are in "experiment.xlsx"
