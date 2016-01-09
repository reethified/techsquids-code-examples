package com.ts.common.util;

import java.util.Arrays;

public class Test {
public static void main(String[] args) {
	String [][] board = new String[][]{{"a","b","c"},{"c","d","e"}};
	for (int i=0; i < board.length;i++){ 
        for(int j=0; j < board[i].length ; j++){
           board[i][j] = "-";
        } 
    }
	for (int i = 0; i < board.length; i++) {
		System.out.println(Arrays.toString(board[i]));
	}
	
}
}
