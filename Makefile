CC = gcc
CFLAGS = -g -Wall
#adjust root accordingly
IPATH = -I/home/jerry/Dropbox/hw/222/bitmap/apue.3e/include
LPATH = -L/home/jerry/Dropbox/hw/222/bitmap/apue.3e/lib
L = -lapue
#If you want to update a file make clean first then make

targets= bmp
all: $(targets)

bmp:
	$(CC) $(CFLAGS) $(IPATH) $(LPATH) $@.c $(L) -o $@ -lm

clean:
	rm -rf *.o $(targets)
