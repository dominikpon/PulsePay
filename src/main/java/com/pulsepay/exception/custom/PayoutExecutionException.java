package com.pulsepay.exception.custom;

public class PayoutExecutionException extends RuntimeException{
    public PayoutExecutionException(String message){super(message);}

    public PayoutExecutionException(String message,Throwable cause){super(message,cause);}
}
