package com.arc_studio.brick_lib_api.core;

import java.math.BigInteger;
import java.util.Arrays;
/**
 * 一部分游戏中使用的大数字实现
 *
 * @author fho4565*/
public final class GameNumber {
    public static final String[] UNIT_UPPER_CASE = {"","K","M","G","T","P","E","Z","Y"};
    public static final String[] UNIT = {"","k","m","g","t","p","e","z","y"};
    private final int[] number = {0,0,0,0,0,0,0,0,0};
    public GameNumber(){}
    public GameNumber(int number){
        this.number[0] = number;
        validate();
    }
    public int getEmpty(){
        return number[0];
    }
    public void setEmpty(short value){
        number[0] = value;
        validate();
    }
    public int getK(){
        return number[1];
    }
    public void setK(short value){
        number[1] = value;
        validate();
    }
    public int getM(){
        return number[2];
    }
    public void setM(short value){
        number[2] = value;
        validate();
    }
    public int getG(){
        return number[3];
    }
    public void setG(short value){
        number[3] = value;
        validate();
    }
    public int getT(){
        return number[4];
    }
    public void setT(short value){
        number[4] = value;
        validate();
    }
    public int getP(){
        return number[5];
    }
    public void setP(short value){
        number[5] = value;
        validate();
    }
    public int getE(){
        return number[6];
    }
    public void setE(short value){
        number[6] = value;
        validate();
    }
    public int getZ(){
        return number[7];
    }
    public void setZ(short value){
        number[7] = value;
        validate();
    }
    public int getY(){
        return number[8];
    }
    public void setY(short value){
        number[8] = value;
        validate();
    }
    public void set(int number){
        Arrays.fill(this.number, 0);
        this.number[0] = number;
        validate();
    }
    public void add(GameNumber gameNumber){
        GameNumber gameNumber1 = fromBigInteger(this.toBigInteger().add(gameNumber.toBigInteger()));
        System.arraycopy(number, 0, gameNumber1.number, 0, gameNumber1.number.length);
        validate();
    }
    public void subtract(GameNumber gameNumber){
        GameNumber gameNumber1 = fromBigInteger(this.toBigInteger().subtract(gameNumber.toBigInteger()));
        System.arraycopy(number, 0, gameNumber1.number, 0, gameNumber1.number.length);
        validate();
        validate();
    }
    public GameNumber multiply(GameNumber gameNumber){
        BigInteger bigInteger = toBigInteger();
        bigInteger = bigInteger.multiply(gameNumber.toBigInteger());
        return fromBigInteger(bigInteger);
    }
    private void validate(){
        BigInteger bigInteger = toBigInteger();
        for (int i = 0; i < number.length; i++) {
            number[i] = bigInteger.mod(new BigInteger("1000")).intValue();
            bigInteger = bigInteger.divide(new BigInteger("1000"));
        }
    }
    private GameNumber fromBigInteger(BigInteger bigInteger){
        GameNumber gameNumber = new GameNumber();
        for (int i = 0; i < number.length; i++) {
            gameNumber.number[i] = bigInteger.mod(new BigInteger("1000")).intValue();
            bigInteger = bigInteger.divide(new BigInteger("1000"));
        }
        return gameNumber;
    }
    private BigInteger toBigInteger(){
        BigInteger bigInteger = new BigInteger("0");
        for (int i = 0; i < number.length; i++) {
            bigInteger =  bigInteger.add(new BigInteger(String.valueOf(number[i])).multiply(new BigInteger("1000").pow(i)));
        }
        return bigInteger;
    }
    public String getString(boolean upperCase) {
        String[] units = upperCase ? UNIT_UPPER_CASE : UNIT;
        StringBuilder builder = new StringBuilder();
        for (int i = number.length - 1; i >= 0; i--) {
            if (number[i] != 0 || i == 0) {
                builder.append(number[i]).append(units[i]);
            }
        }
        return builder.toString();
    }
    public static GameNumber load(String string){
        return new GameNumber().fromBigInteger(new BigInteger(string));
    }
    public static String save(GameNumber gameNumber){
        return gameNumber.toBigInteger().toString();
    }
}

