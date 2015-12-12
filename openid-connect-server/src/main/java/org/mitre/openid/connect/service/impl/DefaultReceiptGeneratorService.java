/**
 * 
 */
package org.mitre.openid.connect.service.impl;

import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.ReceiptGeneratorService;
import org.springframework.stereotype.Service;

/**
 * @author sarahdavies
 *
 */

@Service("defaultReceiptGeneratorService")

public class DefaultReceiptGeneratorService implements ReceiptGeneratorService {
	private String jurisdiction;
	/* (non-Javadoc)
	 * @see org.mitre.openid.connect.service.ReceiptGeneratorService#generateConsentReceipt(org.mitre.openid.connect.model.UserInfo)
	 */
	@Override
	public void generateConsentReceipt(UserInfo crUserInfo) {
		// TODO Auto-generated method stub per transaction stuff goes here

	}
	public String getJurisdiction() {
		return jurisdiction;
	}
	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

}
