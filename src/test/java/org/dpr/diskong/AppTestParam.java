package org.dpr.diskong;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import diskong.api.discogs.todo.DiscogClient;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;


/**
 * Unit test for simple App.
 */
@RunWith(JUnitParamsRunner.class)
public class AppTestParam {




	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		DiscogClient app = new DiscogClient();
		//app.getRequestToken();
		//app.getAccessToken();
		app.getUserID();
		
	}
	
	@Test
	@Parameters
	public void isMobilePhoneNumberTest( String phoneNumber,  boolean expectedResult) {
	    //final boolean result = PhoneUtils.isMobilePhoneNumber(phoneNumber);
		
	    assertTrue(true);
	    if (phoneNumber.equals("0123456789")){
	    	fail("iiu")
;	    }
	}
	
	private Object[] parametersForIsMobilePhoneNumberTest() {
	    return new Object[][] {
	            {"1", false},
	            {"0123456789", false},
	            {"0606060606", true}
	    };
	}
}
