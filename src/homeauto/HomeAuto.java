/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homeauto;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anon
 */
public class HomeAuto {
    
     static final  GpioController GPIO_CONT     = GpioFactory.getInstance();  
     static final  GpioPinDigitalOutput RELAY_1 = GPIO_CONT.provisionDigitalOutputPin(RaspiPin.GPIO_15,"relay 1",PinState.HIGH);
     static final  GpioPinDigitalOutput RELAY_2 = GPIO_CONT.provisionDigitalOutputPin(RaspiPin.GPIO_16,"relay 2",PinState.HIGH);

    public static void main(String[] args) throws InterruptedException, IOException {

        ServerSocket serverSocket = new ServerSocket(9090);
         
        System.out.println("Server Started ...");

         while(true){
             new Listen(serverSocket.accept()).start();
         }
 
    }
 private static void relay1(){
     
    RELAY_1.setShutdownOptions(true,PinState.LOW);
    
    if(RELAY_1.getState() == PinState.LOW){
        RELAY_1.setState(PinState.HIGH);
      }else{ 
        RELAY_1.setState(PinState.LOW);
     }

 }
  private static void relay2(){
      
      RELAY_2.setShutdownOptions(true,PinState.LOW); 
       
    if(RELAY_2.getState() == PinState.LOW){  
        RELAY_2.setState(PinState.HIGH);
    }else{
        RELAY_2.setState(PinState.LOW);
    }
    
 }
 private static class Listen extends Thread {
     
      private final Socket socket;

    Listen(Socket socket){
         this.socket=socket;
     }
     @Override
     public void run(){
         
         BufferedReader in = null;
         PrintWriter out   = null; 
         
          try {
               
              in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              out = new PrintWriter(socket.getOutputStream(), true);
      
              while(true){
                  
                  String temp = in.readLine();
                  System.out.println(temp);
                  
                  if(temp.equals("45-FT")){
                    relay1(); 
                    System.out.println("OPEN Relay 1");
                    out.println("Relay 1 Opened");
                  }else if(temp.equals("45-F")){
                     relay2();
                     System.out.println("OPEN Relay 2");
                     out.println("Relay 2 Opened"); 
                  }
              }
              
          } catch (IOException ex) {
              Logger.getLogger(HomeAuto.class.getName()).log(Level.SEVERE, null, ex);
          } finally {
              try {
                  in.close();
                  out.close();
                  socket.close();
              } catch (IOException ex) {
                  Logger.getLogger(HomeAuto.class.getName()).log(Level.SEVERE, null, ex);
              }
          }
     }
     
 }
}
