package com.hackNjit;

import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Permissions;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.interfaces.system.SystemInterface;
import com.amazon.speech.speechlet.interfaces.system.SystemState;
import com.amazon.speech.ui.AskForPermissionsConsentCard;
import com.amazon.speech.ui.PlainTextOutputSpeech;

public class HackNJIT implements SpeechletV2 {

	private static final String ADDRESS_CARD_TITLE = "Sample Device Address Skill";
	/**
	 * The permissions that this skill relies on for retrieving addresses. If the consent token isn't
	 * available or invalid, we will request the user to grant us the following permission
	 * via a permission card.
	 *
	 * Another Possible value if you only want permissions for the country and postal code is:
	 * read::alexa:device:all:address:country_and_postal_code
	 * Be sure to check your permissions settings for your skill on https://developer.amazon.com/
	 */
	private static final String ALL_ADDRESS_PERMISSION = "read::alexa:device:all:address";
	


	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> speechletRequestEnvelope) {
		// TODO Auto-generated method stub
		IntentRequest intentRequest = speechletRequestEnvelope.getRequest();
		Session session = speechletRequestEnvelope.getSession();
		Intent intent = intentRequest.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		/*
		 *Users intent switch case
		 */
		switch (intentName) {
		/*
		 * when user shows intent that he is not feeling well
		 */
		case "FeelingN":

			return SkillResponse.newAskResponse("Why what happend ?", "Sorry! I didn't get you. How may I help you?");

		/*
		 * when user tells why he is not feeling well
		 */
		case "NotWell":
			session.setAttribute("bodypart", true);
			Permissions permissions = session.getUser().getPermissions();
			if (permissions == null) {
				return getPermissionsResponse();
			}
			String consentToken = permissions.getConsentToken();
			SystemState systemState = getSystemState(speechletRequestEnvelope.getContext());
			String deviceId = systemState.getDevice().getDeviceId();
			locationFetcher locationfettcher = new locationFetcher();
			String temp[] = locationfettcher.predictions(deviceId,consentToken);



			float[] location = new float[2];
			
			if(locationfettcher.getLatLong(temp,location).equals("Success")) {
				HospitalFetcher hospitalFetcher = new HospitalFetcher();
				ArrayList<String> Hospital = hospitalFetcher.predictions(1,location);
				if(Hospital != null) {
					session.setAttribute("ListofHospitals",Hospital);
					return SkillResponse.newAskResponse("There are "+ Hospital.size()+" hospitals near you which covers you in your insurance, would you like me to book an appointment?", "Sorry! I didn't get you. How may I help you?");
				}else {
					return SkillResponse.newAskResponse("No hospitals found near you which covers you in your insurance", "Sorry! I didn't get you. How may I help you?");
				}
			}else {
				return SkillResponse.newAskResponse(" Error in data ", "");
			}





		/*
		 * when user shows interest in booking an appointment with the doctor
		 */
		case"BookAppointment":
			boolean bodypart1 = (boolean) session.getAttribute("bodypart");
			ArrayList<String> Hospitals = (ArrayList<String>) session.getAttribute("ListofHospitals");
			if(bodypart1) {
				if(Hospitals.size()==0) {
					return SkillResponse.newAskResponse("No data in hospitals", "Sorry! I didn't get you. How may I help you?");
				}
				StringBuilder listOfHospitals = new StringBuilder("Here is the list of hospitals nearby, ");
				for(int index = 0; index < Hospitals.size(); index++) {

					if(index == Hospitals.size()-1) {
						listOfHospitals.append(" "+Hospitals.get(index)+" ");
					}else {
						listOfHospitals.append(" "+Hospitals.get(index)+" and ");
					}
				}
				return SkillResponse.newAskResponse(listOfHospitals.toString()+", which one would you like me to book an appoinment?", "Sorry! I didn't get you. How may I help you?");

			}else {
				return SkillResponse.newAskResponse("for what you would like me to book an appoinment?", "Sorry! I didn't get you. How may I help you?");

			}


		/*
		 * User selecting the slot from all the available slot at the hospital
		 */
		case"AppointmentBooked":
			HashMap<String,Integer> list = new HashMap<String,Integer>();
			list.put("First",1);
			list.put("second",2);
			list.put("Third",3);
			list.put("Fourth",4);
			list.put("Fifth",5);
			list.put("Sixth",6);
			list.put("Seventh",7);
			list.put("Eigth",8);
			list.put("Ninth",9);
			list.put("Tenth",10);
			String digitOne = intent.getSlot("digitOne").getValue();
			ArrayList<String> Hospitalsname = (ArrayList<String>) session.getAttribute("ListofHospitals");
			session.setAttribute("appoinmentBooked", true);
			return SkillResponse.newAskResponse("Your appoinment at "+ Hospitalsname.get(list.get(digitOne)-1)+" is booked, would you like me to set a reminder for the appoinment", "Sorry! I didn't get you. How may I help you?");

		/*
		 * when user shows interest in making a reminder for the doctors appointment
		 */
		case"BookReminder":
			boolean flag = (boolean) session.getAttribute("appoinmentBooked");
			if(flag) {
				session.setAttribute("appoinmentBooked",false);
				session.setAttribute("bodypart", false);
				session.setAttribute("ListofHospitals", null);
				return SkillResponse.newAskResponse("Your reminder is set, can i help you with anythng else", "Sorry! I didn't get you. How may I help you?");
			}
			else {
				return SkillResponse.newAskResponse("Sorry I did not get you, would you like me to set a reminder for the appoinment ", "Sorry! I didn't get you. How may I help you?");	
			}
		/*
		 * when user has lost his insurance card
		 */
		case"Lostcard":
			Random rand = new Random();
			int num = rand.nextInt(9000000) + 1000000;
			return SkillResponse.newTellResponse("I have generated the request .Request number is "+ num);

		/*
		 * Flow termination
		 */
		case"End":
			session.setAttribute("appoinmentBooked",false);
			session.setAttribute("bodypart", false);
			session.setAttribute("ListofHospitals", null);
			return SkillResponse.newTellResponse("cool , you know how to call me");
		default:
			return SkillResponse.newTellResponse("Sorry I did not get you");

		}

	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> arg0) {
		// TODO Auto-generated method stub
		return SkillResponse.newAskResponse("Welcome to the Hack Njit 2k17 , how can I help you?", "Sorry! I didn't get you. How may I help you?");
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> arg0) {
		// TODO Auto-generated method stub

	}

	private SpeechletResponse getPermissionsResponse() {
		String speechText = "You have not given this skill permissions to access your address. " +
				"Please give this skill permissions to access your address.";
		
		AskForPermissionsConsentCard card = new AskForPermissionsConsentCard();
		card.setTitle(ADDRESS_CARD_TITLE);
		Set<String> permissions = new HashSet<>();
		permissions.add(ALL_ADDRESS_PERMISSION);
		card.setPermissions(permissions);
		PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
		return SpeechletResponse.newTellResponse(speech, card);
	}
	/**
	 * Helper method for retrieving an OutputSpeech object when given a string of TTS.
	 * @param speechText the text that should be spoken out to the user.
	 * @return an instance of SpeechOutput.
	 */
	private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		return speech;
	}

	/*
	* Method to return system date.
	*/
	private SystemState getSystemState(Context context) {
		return context.getState(SystemInterface.class, SystemState.class);
	}

}
