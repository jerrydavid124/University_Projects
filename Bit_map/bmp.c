/**
 * @file bmp.c
 *
 * @author Jerry Tafoya
 *
 * @date April 8th 2019
 *
 * Assignment: BitMap Project
 *
 * @brief can read 8bpp and 24bpp bmp files
 * 
 *
 * Note: If you want to compile then change the pathnames in the makefile
 * SideNote: Also make sure to unpack apue.src.3e.tar.gz, go into that directory
 * and run "make" so that bmp.c can work for error handling
 *
 * @details reads information from a bmp file and can print out information
 * about its header, color table and pixel data.
 * when outputting it can change the r g and b values of an image as well as
 * having the option to reverse the image colors.
 *
 * @bugs You HAVE to put the original file at the end of the arguments list
 * Ex: ./bmp.c -o temp.bmp pepper.bmp
 * I could have definitely done this in about 400 lines if I re-did the Assignment
 * Knowing what I do now about bit maps.
 *
 * @todo: None
 */


#include "apue.h"
#include "read_all.c"
#include <stdio.h>
#include <ftw.h>
#include <stdint.h>
#include <pwd.h>
#include <stdlib.h>
#include <time.h>
#include <ctype.h>
#include <unistd.h>
#include <errno.h>
#include <assert.h>
#include <sys/types.h>
#include <sys/syscall.h>
#include <sys/uio.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dirent.h>
#include <string.h>
#include <sys/sysmacros.h>
#include <math.h>

int colortable = 0;
int pixeldata1 = 0;
int pixeldata2 = 0;

//Comments for these functions are found under main

int printheaderinfo(unsigned char *header);
unsigned int getinfo(unsigned char *header, int front, int end);
int printcolortable(unsigned char * buffer, int num_bytes);
int print_pixel_data(int fd, int imgwidth, int imgheight, int bit_pix, int offset);
int print_pixel_data2(int fd, int imgwidth, int imgheight, int bit_pix, int offset);
int outputdata(int fd, int fdout, int imgwidth, int imgheight, int bit_pix, int offset, int col_table, int rflag, int gflag, int bflag, int rval, int gval, int bval);
int Routputdata(int fd, int fdout, int imgwidth, int imgheight, int bit_pix, int offset, int col_table, int rflag, int gflag, int bflag, int rval, int gval, int bval);


int main(int argc, char *argv[]){
        if(argc == 1){
                printf("Usage: ./bmp.c -h\n");
                exit(EXIT_SUCCESS);
        }
        opterr = 0;
        int oflag = 0; //-o FILE
        int iflag = 0; //-i
        int dflag = 0; //-d
        int Rflag = 0; //-R *needs output*
        int rflag = 0; //-r *needs output*
        int gflag = 0; //-g *needs output*
        int bflag = 0; //-b *needs output*
        int rval = 0; // r value
        int gval = 0; // g value
        int bval = 0; // b value
        size_t pagesize = sysconf(_SC_PAGESIZE); //gets pagesize for reading file
        char *filenameout = malloc(sizeof(char) * 4096); //gather output name
        int c;
        int argvspot = 1;

        while ((c = getopt(argc, argv, "ho:idRr:g:b:")) != -1){
                argvspot++;
                switch (c) {
                        case 'h':
                        printf("Welcome to the bitmap project!\n");
                        printf("This program will take a file and read");
                        printf(" back information about the file\n");
                        printf("Usage: ./bmp.c [-o FILE] [-i] [-d] [-R] [-r N] [-g N] [-b N] FILE\n");
                        printf("FILE, the input file, a bmp image (PLACED LAST)\n");
                        printf("-o FILE: optional output file, needed if -r, -g, -b or -R is used\n");
                        printf("-i: display the bitmap header and DIB information\n");
                        printf("-d: a dump of all headers, color table (if avaliable) and pixel\n");
                        printf("values of each cell, one line per pixel\n");
                        printf("***Note*** in the final version the following has not been implemented\n");
                        printf("-R: reverse the image of a 8bpp (grey) image only (need output file)\n");
                        printf("-r N: change value of all red pixels on 24bpp, between 0 and 255\n");
                        printf("-g N: change value of all green pixels on 24bpp, between 0 and 255\n");
                        printf("-b N: change value of all blue pixels on 24bpp, between 0 and 255\n");
                        exit(EXIT_SUCCESS);
                        break;
                        case 'o':
                        memcpy(filenameout, optarg, strlen(optarg));
                        argvspot++;
                        oflag = 1;
                        break;
                        case 'i':
                        iflag = 1;
                        break;
                        case 'd':
                        dflag = 1;
                        break;
                        case 'R':
                        Rflag = 1;
                        break;
                        case 'r':
                        rflag = 1;
                        rval = atoi(optarg);
                        argvspot++;
                        break;
                        case 'g':
                        gflag = 1;
                        gval = atoi(optarg);
                        argvspot++;
                        break;
                        case 'b':
                        bflag = 1;
                        bval = atoi(optarg);
                        argvspot++;
                        break;
                        case '?':
                                if (optopt == 'o'){
                                        fprintf(stderr, "Option -%c needs an output file.\n", optopt);
                                        free(filenameout);
                                        exit(EXIT_SUCCESS);
                                } else if (optopt == 'r'){
                                        fprintf(stderr, "Option -%c needs a value between 0 and 255.\n", optopt);
                                        free(filenameout);
                                        exit(EXIT_SUCCESS);
                                } else if (optopt == 'g'){
                                        fprintf(stderr, "Option -%c needs a value between 0 and 255.\n", optopt);
                                        free(filenameout);
                                        exit(EXIT_SUCCESS);
                                } else if (optopt == 'b'){
                                        fprintf(stderr, "Option -%c needs a value between 0 and 255.\n", optopt);
                                        free(filenameout);
                                        exit(EXIT_SUCCESS);
                                } else {
                                        fprintf(stderr, "Unknown Option or Character\n");
                                        free(filenameout);
                                        exit(EXIT_SUCCESS);
                                }
                        default:
                        free(filenameout);
                        exit(EXIT_SUCCESS);
                }
        }

        /**
        CHECKING IF EVERYTHING FROM STDIN IS CORRECT
        */

        if(Rflag == 1 && oflag == 0){
                printf("-R needs an output file to write to\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        } else if(rflag == 1 && oflag == 0){
                printf("-r needs an output file to change\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        } else if (gflag == 1 && oflag == 0){
                printf("-g needs an output file to change\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        } else if (bflag == 1 && oflag == 0){
                printf("-b needs an output file to change\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        }
        if(rval >= 256 || rval <= -1){
                printf("Error: r value is too large or too small\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        }
        if(gval >= 256 || gval <= -1){
                printf("Error: g value is too large or too small\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        }
        if(bval >= 256 || bval <= -1){
                printf("Error: b value is too large or too small\n");
                free(filenameout);
                exit(EXIT_SUCCESS);
        }

        char *filenamein = malloc(sizeof(char) * pagesize); //gather inout name
        memcpy(filenamein, argv[argvspot], strlen(argv[argvspot])); //file input


        unsigned char *buffer = malloc(sizeof(char) * pagesize);
        int fd = open(filenamein, O_RDONLY);
        unsigned char *header = malloc(sizeof(char) * pagesize);

        int col_table = 0;
        int imgwidth = 0;
        int imgheight = 0;
        int bit_pix = 0;
        int offset = 0;

        /**
        Getting  header information here
        */

        if((read_all(fd, buffer, 53)) == -1){
                free(filenamein);
                free(filenameout);
                free(buffer);
                free(header);
                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                exit(EXIT_FAILURE);
        }
        imgwidth = getinfo(buffer,18,21);
        imgheight = getinfo(buffer,22,25);
        bit_pix = getinfo(buffer,28,29);
        offset = getinfo(buffer,10,13);
        if(iflag == 1 || dflag == 1){
                        col_table = printheaderinfo(buffer);
        }
        if(col_table == -1){
                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                free(buffer);
                free(header);
                free(filenamein);
                free(filenameout);
                close(fd);
                exit(EXIT_SUCCESS);
        }

        /**
        Print Color table info and pixel data info
        */

        if(dflag == 1){
                if(col_table == 0){
                        printf("No Color Table\n");
                } else if(col_table != 0) {
                        if((read_all(fd, buffer, col_table * 4)) == -1){
                                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                                free(buffer);
                                free(header);
                                free(filenamein);
                                free(filenameout);
                                close(fd);
                                exit(EXIT_SUCCESS);
                        } else {
                                printf("Color Table\nintex\tred\tgreen\tblue\talpha\n");
                                printf("---------------------------------------------\n");
                                printcolortable(buffer, col_table * 4); // no error check needed
                        }
                }
                if(col_table != 0){
                        printf("Pixel Data\n");
                        printf("(row, col) index\n");
                        printf("------------------\n");
                        if((print_pixel_data(fd, imgwidth, imgheight, bit_pix, offset)) == -1){
                                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                                free(buffer);
                                free(header);
                                free(filenamein);
                                free(filenameout);
                                close(fd);
                                exit(EXIT_SUCCESS);
                        }
                } else {
                        printf("Pixel Data\n");
                        printf("(row, col) red\tgreen\tblue\n");
                        printf("-----------------------------\n");
                        if((print_pixel_data2(fd, imgwidth, imgheight, bit_pix, offset)) == -1){
                                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                                free(buffer);
                                free(header);
                                free(filenamein);
                                free(filenameout);
                                close(fd);
                                exit(EXIT_SUCCESS);
                        }
                }
        }

        /**
        write data from my input file to the output file, color change is done
        here as well
        */

        int fdout;
        lseek(fd, 0, SEEK_SET);
        if(oflag == 1){
                fdout = open(filenameout, O_WRONLY | O_CREAT , 0777);
                if(Rflag == 1){
                        if((Routputdata(fd, fdout, imgwidth, imgheight, bit_pix, offset, col_table, rflag, gflag, bflag, rval, gval, bval)) == -1){
                                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                                free(buffer);
                                free(header);
                                free(filenamein);
                                free(filenameout);
                                close(fd);
                                close(fdout);
                                exit(EXIT_SUCCESS);
                        }
                } else {
                        if((outputdata(fd, fdout, imgwidth, imgheight, bit_pix, offset, col_table, rflag, gflag, bflag, rval, gval, bval)) == -1){
                                err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                                free(buffer);
                                free(header);
                                free(filenamein);
                                free(filenameout);
                                close(fd);
                                close(fdout);
                                exit(EXIT_SUCCESS);
                        }
                }
        }

        free(buffer);
        free(header);
        free(filenamein);
        free(filenameout);
        close(fd);
        close(fdout);
        exit(EXIT_SUCCESS);
}

/**
* Prints the heaader information from a buffer in main, counting upto num_bytes
* @param header: buffer passed from main containing the header information
* @return: returns the size of the images color table
*/
int printheaderinfo(unsigned char *header){

        //A list of where the data lies within the header
        int col_table = 0;
        //*starting at 0//
        char h_type[2]; //0 - 1
        //int size_bitmap = 0; // 2 - 5
        //6 - 9 is reserved
        //int offset = 0; //10 - 13
        //int dib = 0; //14 - 17
        //int width = 0; // 18 - 21
        //int height = 0; //22 - 25
        //int planes = 0; //26 - 27
        //int bits_per_pix = 0; //28 - 29
        //int compress = 0; // 30 - 33
        //int image_size = 0; //34 - 37
        //int horiz_res = 0; // 38 - 41
        //int verti_res = 0; //42 - 45
        //int num_col = 0; // 46 - 49
        //int imp_col = 0; //50 - 53




        int headsize = getinfo(header,14,17);
        if(headsize != 40){
                printf("The BitMapInfoHeader is not of size 40, exiting...");
                return - 1;
        }

        h_type[0] = header[0];
        h_type[1] = header[1];
        printf("header: %s\n", h_type);
        printf("size of bitmap (bytes) : %u\n", getinfo(header,2,5));
        printf("offset (start of image data): %u\n", getinfo(header,10,13));
        printf("\n");
        printf("size of dib: %u\n", headsize);
        printf("bitmap width in pixels: %u\n", getinfo(header,18,21));
        printf("bitmap height in pixels: %u\n", getinfo(header,22,25));
        printf("number of color planes: %u\n", getinfo(header,26,27));
        printf("number of bits per pixel: %u\n", getinfo(header,28,29));
        printf("compressions method: %u\n", getinfo(header,30,33));
        printf("image size: %u\n", getinfo(header,34,37));
        printf("horizontal resolution (pixel per meter): %u\n", getinfo(header,38,41));
        printf("vertical resolution (pixel per meter): %u\n", getinfo(header,42,45));
        printf("number of colors: %u\n", getinfo(header,46,49));
        if((getinfo(header,46,49)) != 0){
                col_table = getinfo(header,46,49);
        }
        printf("number of important colors: %u\n", getinfo(header,50,53));
        return col_table;

}
/**
* Gets the info from header, can read 4 or 2 bytes in length only and reads
little endian information.
* @param header: String that holds the information
* @param front: start of where to read bytes
* @param end: end of where to read bytes
* @return returns the byte value as an unsigned int
*/
unsigned int getinfo(unsigned char *header, int front, int end){
        unsigned int ans = 0;
        int diff = (end - front);
        char *revhead = malloc(sizeof(char) * 4);
        char *hexexp = malloc(sizeof(char) * 4);
        char *fixwidth = malloc(sizeof(char) * 4);
        char *fixagain = malloc(sizeof(char) * 4);
        if(diff == 3){
                revhead[0] = header[end];
                revhead[1] = header[end - 1];
                revhead[2] = header[front + 1];
                revhead[3] = header[front];
                sprintf(fixwidth, "%02X", revhead[3]);
                if(strlen(fixwidth) != 2){
                        strcpy(fixagain, &fixwidth[6]);
                        sprintf(hexexp, "%02X%02X%02X%s", revhead[0], revhead[1], revhead[2], fixagain);
                } else {
                        sprintf(hexexp, "%02X%02X%02X%02X", revhead[0], revhead[1], revhead[2], revhead[3]);
                }

        } else if (diff == 1){
                revhead[0] = header[end];
                revhead[1] = header[front];
                sprintf(hexexp, "%02X%02X", revhead[0], revhead[1]);
        }
        ans = (unsigned int)strtol(hexexp, NULL, 16);
        free(revhead);
        free(fixwidth);
        free(fixagain);
        free(hexexp);
        return ans;

}
/**
* This prints out the colortable if the return value from printheaderinfo is
greater than 0
* @param buffer: this string points to the beginning of the color values
* @param num_bytes: how far down the function reads for color information
*/
int printcolortable(unsigned char * buffer, int num_bytes){
        int i = 0;
        unsigned int rval, gval, bval, aval;
        char string[64];
        int c = 0;
        while (i < num_bytes) {
                sprintf(string, "%02X", buffer[i]);
                aval = (unsigned int)strtol(string, NULL, 16);
                sprintf(string, "%02X", buffer[i+1]);
                bval = (unsigned int)strtol(string, NULL, 16);
                sprintf(string, "%02X", buffer[i+2]);
                gval = (unsigned int)strtol(string, NULL, 16);
                sprintf(string, "%02X", buffer[i+3]);
                rval = (unsigned int)strtol(string, NULL, 16);
                i = i + 4;
                printf("%d\t%d\t%d\t%d\t%d\n", c, rval, gval, bval, aval);
                c++;
        }
        return 0;
}

/**
* Prints the pixel data fomr the file descriptor in main, assuming that it points
right after the color table and header information from main. this is the black
and white version of the function
* @param fd: the filedescriptor of the input file in main
* @param imgwidth: the width of the images
* @param imgheight: the height of the images
* @param bit_pix: the bits per pixel used to determine how many bytes we have
* @param offset: Where in buffer the infomation begins
*/
int print_pixel_data(int fd, int imgwidth, int imgheight, int bit_pix, int offset){
        float rowsize = ((bit_pix * imgwidth)/32);
        rowsize = ceilf(rowsize);
        rowsize = rowsize*4;
        int pixelarraysize = rowsize * imgheight;

        unsigned char *pixdata = malloc(sizeof(char) * pixelarraysize);

        struct iovec iov[1];
        iov[0].iov_base = pixdata;
        iov[0].iov_len = sysconf(_SC_PAGESIZE);


        lseek(fd, offset, SEEK_SET);
        int bytes_read = 0;

        //place all info from infile to pixdata
        for(int i = pixelarraysize; i > 0; i = i - bytes_read){
                bytes_read = readv(fd, iov, 1);
                if(bytes_read == -1){
                        free(pixdata);
                        err_sys("error in %s:%s:%d", __FILE__, "main", __LINE__);
                        return -1;
                        exit(EXIT_FAILURE);
                }
                iov->iov_base = iov->iov_base + bytes_read;
        }
        int col = 0;
        int row = 0;
        int c;
        int pos;
        char *byte = malloc(sizeof(char) * 16);
        int byte_val;
        int padding = imgwidth * bit_pix;
        padding = padding % 32;
        if(padding != 0){
                padding = 32 - padding;
        }
        padding = padding / 8;

        //read info from pixdata
        for(int j = 1; j <= imgheight; j++){
                col = 0;
                for(c = 0; c < (rowsize - padding); c++){
                        pos = c + (rowsize * (imgheight - j));
                        sprintf(byte, "%02X", pixdata[pos]);
                        byte_val = (int)strtol(byte, NULL, 16);
                        printf("(%d,%d) %d\n", row, col, byte_val);
                        col += 1;
                }
                row+= 1;
        }
        free(byte);
        free(pixdata);
        return 0;

}
/**
* Prints the pixel data from the file descriptor in main, assuming that it points
right after the color table and header information from main. This is the colored
version of the function
* @param fd: the filedescriptor of the input file in main
* @param imgwidth: the width of the images
* @param imgheight: the height of the images
* @param bit_pix: the bits per pixel used to determine how many bytes we have
* @param offset: Where in buffer the infomation begins
*/
int print_pixel_data2(int fd, int imgwidth, int imgheight, int bit_pix, int offset){
        float rowsize = ((bit_pix * imgwidth)/32);
        rowsize = ceilf(rowsize);
        rowsize = rowsize*4;
        int pixelarraysize = rowsize * imgheight;

        unsigned char *pixdata = malloc(sizeof(char) * pixelarraysize);

        struct iovec iov[1];
        iov[0].iov_base = pixdata;
        iov[0].iov_len = sysconf(_SC_PAGESIZE);


        lseek(fd, offset, SEEK_SET);
        int bytes_read = 0;

        //place data for infole into pixdata
        for(int i = pixelarraysize; i > 0; i = i - bytes_read){
                bytes_read = readv(fd, iov, 1);
                if(bytes_read == -1){
                        free(pixdata);
                        return -1;
                }
                iov->iov_base = iov->iov_base + bytes_read;
        }
        int col = 0;
        int row = 0;
        int c;
        int pos;
        char *byte = malloc(sizeof(char) * 16);
        int padding = imgwidth * bit_pix;
        padding = padding % 32;
        if(padding != 0){
                padding = 32 - padding;
        }
        padding = padding / 8;

        int red;
        int green;
        int blue;

        //print the color information from pixdata
        for(int j = 1; j <= imgheight; j++){
                col = 0;
                for(c = 0; c < (rowsize - padding); c += 3){
                        pos = c + (rowsize * (imgheight - j));
                        sprintf(byte, "%02X", pixdata[pos+2]);
                        red = (int)strtol(byte, NULL, 16);
                        sprintf(byte, "%02X", pixdata[pos+1]);
                        green = (int)strtol(byte, NULL, 16);
                        sprintf(byte, "%02X", pixdata[pos]);
                        blue = (int)strtol(byte, NULL, 16);
                        printf("(%d,%d) %d\t%d\t%d\n", row, col, red, green, blue);
                        col += 1;
                }
                row+= 1;
        }
        free(byte);
        free(pixdata);
        return 0;

}
/**
* Places all of the data from the input file to the output file
* @param fd: the filedescriptor of the input file in main
* @param imgwidth: the width of the images
* @param imgheight: the height of the images
* @param bit_pix: the bits per pixel used to determine how many bytes we have
* @param offset: Where in buffer the infomation begins
* @param col_table: the size of the color table
* @param rflag: if -r is set
* @param gflag: if -g is set
* @param bflag: if -b is set
* @param rval: value of red
* @param gval: value of green
* @param bval: value of blue
*/
int outputdata(int fd, int fdout, int imgwidth, int imgheight, int bit_pix, int offset, int col_table, int rflag, int gflag, int bflag, int rval, int gval, int bval){

        float rowsize = ((bit_pix * imgwidth)/32);
        rowsize = ceilf(rowsize);
        rowsize = rowsize*4;
        int pixelarraysize = rowsize * imgheight;
        int allocated = pixelarraysize + 53 + (col_table * 4);
        unsigned char *pixdata = malloc(sizeof(char) * allocated);

        struct iovec iov[1];
        iov[0].iov_base = pixdata;
        iov[0].iov_len = sysconf(_SC_PAGESIZE);


        lseek(fd, 0, SEEK_SET);
        int bytes_read = 0;
        int bytes_written = 0;

        //placing all of my data into iov_base
        for(int i = allocated; i > 0; i = i - bytes_read){
                bytes_read = readv(fd, iov, 1);
                if(bytes_read == -1){
                        free(pixdata);
                        return -1;
                }
                iov->iov_base = iov->iov_base + bytes_read;
        }

        char *byte = malloc(sizeof(char) * 16);
        int padding = imgwidth * bit_pix;
        padding = padding % 32;
        if(padding != 0){
                padding = 32 - padding;
        }
        padding = padding / 8;




        //checking for all of the colors
        int pos;
        if(col_table == 0){
                if(rflag == 1 && gflag == 1 && bflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = rval;
                                        pixdata[pos + 1 + offset] = gval;
                                        pixdata[pos + offset] = bval;
                                }
                        }
                } else if (gflag == 1 && rflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = rval;
                                        pixdata[pos + 1 + offset] = gval;
                                }
                        }
                } else if (bflag == 1 && gflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 1 + offset] = gval;
                                        pixdata[pos + offset] = bval;
                                }
                        }
                } else if (rflag == 1 && bflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = rval;
                                        pixdata[pos + 1 + offset] = gval;
                                }
                        }
                } else if (gflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 1 + offset] = gval;
                                }
                        }
                } else if (bflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + offset] = bval;
                                }
                        }
                } else if (rflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = rval;
                                }
                        }
                }
        }


        //placing data from in file to out file
        lseek(fdout, 0, SEEK_SET);
        lseek(fd, 0, SEEK_SET);
        iov->iov_base = pixdata;
        for(int i = allocated; i > 0; i = i - bytes_written){
                bytes_written = writev(fdout, iov, 1);
                if(bytes_written == -1){
                        free(pixdata);
                        return -1;
                }
                iov->iov_base = iov->iov_base + bytes_written;
        }

        free(byte);
        free(pixdata);
        return 0;
}
/**
* Places all of the data from the input file to the output file, but inverts the colors
* @param fd: the filedescriptor of the input file in main
* @param imgwidth: the width of the images
* @param imgheight: the height of the images
* @param bit_pix: the bits per pixel used to determine how many bytes we have
* @param offset: Where in buffer the infomation begins
* @param col_table: the size of the color table
* @param rflag: if -r is set
* @param gflag: if -g is set
* @param bflag: if -b is set
* @param rval: value of red
* @param gval: value of green
* @param bval: value of blue
*/
int Routputdata(int fd, int fdout, int imgwidth, int imgheight, int bit_pix, int offset, int col_table, int rflag, int gflag, int bflag, int rval, int gval, int bval){
        float rowsize = ((bit_pix * imgwidth)/32);
        rowsize = ceilf(rowsize);
        rowsize = rowsize*4;
        int pixelarraysize = rowsize * imgheight;
        int allocated = pixelarraysize + 53 + (col_table * 4);
        unsigned char *pixdata = malloc(sizeof(char) * allocated);

        struct iovec iov[1];
        iov[0].iov_base = pixdata;
        iov[0].iov_len = sysconf(_SC_PAGESIZE);


        lseek(fd, 0, SEEK_SET);
        int bytes_read = 0;
        int bytes_written = 0;

        //placing all data into pixdata
        for(int i = allocated; i > 0; i = i - bytes_read){
                bytes_read = readv(fd, iov, 1);
                if(bytes_read == -1){
                        free(pixdata);
                        return -1;
                }
                iov->iov_base = iov->iov_base + bytes_read;
        }

        char *byte = malloc(sizeof(char) * 16);
        int padding = imgwidth * bit_pix;
        padding = padding % 32;
        if(padding != 0){
                padding = 32 - padding;
        }
        padding = padding / 8;




        //chekcing for all of the colors
        int pos;
        if(col_table == 0){
                if(rflag == 1 && gflag == 1 && bflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = rval;
                                        pixdata[pos + 1 + offset] = gval;
                                        pixdata[pos + offset] = bval;
                                }
                        }
                } else if (gflag == 1 && rflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = rval;
                                        pixdata[pos + 1 + offset] = gval;
                                        pixdata[pos + offset] = 255 - pixdata[pos + offset];
                                }
                        }
                } else if (bflag == 1 && gflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = 255 - pixdata[pos + offset + 2];
                                        pixdata[pos + 1 + offset] = 255 - gval;
                                        pixdata[pos + offset] = 255 - bval;
                                }
                        }
                } else if (rflag == 1 && bflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = 255 - rval;
                                        pixdata[pos + 1 + offset] = 255 - gval;
                                        pixdata[pos + offset] = 255 - pixdata[pos + offset];
                                }
                        }
                } else if (gflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = 255 - pixdata[pos + offset + 2];
                                        pixdata[pos + 1 + offset] = 255 - gval;
                                        pixdata[pos + offset] = 255 - pixdata[pos + offset];
                                }
                        }
                } else if (bflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = 255 - pixdata[pos + offset + 2];
                                        pixdata[pos + 1 + offset] = 255 - pixdata[pos + offset + 1];
                                        pixdata[pos + offset] = 255 - bval;
                                }
                        }
                } else if (rflag == 1){
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = 255 - rval;
                                        pixdata[pos + 1 + offset] = 255 - pixdata[pos + offset + 1];
                                        pixdata[pos + offset] = 255 - pixdata[pos + offset];
                                }
                        }
                } else {
                        for(int j = 1; j <= imgheight; j++){
                                for(int c = 0; c < (rowsize - padding); c += 3){
                                        pos = c + (rowsize * (imgheight - j));
                                        pixdata[pos + 2 + offset] = 255 - pixdata[pos + offset + 2];
                                        pixdata[pos + 1 + offset] = 255 - pixdata[pos + offset + 1];
                                        pixdata[pos + offset] = 255 - pixdata[pos + offset];
                                }
                        }
                }
        }


        //place data from input file to output file
        lseek(fdout, 0, SEEK_SET);
        lseek(fd, 0, SEEK_SET);
        iov->iov_base = pixdata;
        for(int i = allocated; i > 0; i = i - bytes_written){
                bytes_written = writev(fdout, iov, 1);
                if(bytes_written == -1){
                        free(pixdata);
                        return -1;
                }
                iov->iov_base = iov->iov_base + bytes_written;
        }

        free(byte);
        free(pixdata);
        return 0;
}
