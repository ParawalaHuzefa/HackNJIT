package com.hackNjit;

import java.util.HashSet;
import java.util.Set;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class HackNjitRequestStreamHandler extends SpeechletRequestStreamHandler {
	
    private static final Set<String> supportedApplicationIds = new HashSet<String>();

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("Write your ID here");
    }

    /*
     * Start Point of the application. 
     * Application will give response acording to HackNJIT class's methods
     */
    public HackNjitRequestStreamHandler() {
        super(new HackNJIT(), supportedApplicationIds);
    }
}

