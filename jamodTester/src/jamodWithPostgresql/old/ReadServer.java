/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql.old;

//import Logserver.Logger;
//import SMSClient.SMSClient;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
//import database.ClampQuery;

/**
 *
 * @author marko
 */
public class ReadServer {
//    private final static Logger log = new Logger(ReadServer.class.getName());
    private static GpioController gpio;
    private boolean active1 = false;
    
        /* Clamp 1 */
    private final GpioPinDigitalInput clamp1;
    
    private boolean startStop;

    public void setStartStop(boolean startStop) {
        this.startStop = startStop;
    }

    public boolean isStartStop() {
        return startStop;
    }

    public ReadServer() { 
        gpio = GpioFactory.getInstance();
        clamp1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
        
        clamp1.setShutdownOptions(true);

        startStop=true;
//        log.startup("init gpio pins successful");
        
//        log.startup("start init listeners");
        initListeners();
        
        runServer();
    }
    
    
    private void initListeners() {
    
        /* Clamp 1 */ 
        GpioPinListenerDigital clamp1Listener = new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                try {
                    if(event.getState() == PinState.HIGH && !active1) {
                        active1=true; 
//                        log.alarm("Clamp 1 noticed an alarm");
//                        ClampQuery info = new ClampQuery(1);
//                        SMSClient.sendAlarmRequest(info.getType(), info.getText(), info.getGroups(), info.getCoordinates());
//                        log.alarm("send alarm successful");
//                        Thread.sleep(500);
                        
                        active1=false;                    
                    }
                }
                catch (Exception e) { 
//                    log.error("failed to send alarm");
//                    log.fatal(e.toString());
                    active1=false;    
                }
            }
        };
        clamp1.addListener(clamp1Listener);
//        log.startup("set Listener for clamp 1");
        
        
        
//        log.startup("finished init listeners and wait for any alarm");
        
        try 
        {
//            log.startup("system is running");
            while(startStop)
            {
                Thread.sleep(1000);
            }
        } catch (Exception e) 
        {
//            log.fatal(e.toString());
        }
//        log.fatal("system is going down");
    }

    private void runServer() {

    }
    
}
