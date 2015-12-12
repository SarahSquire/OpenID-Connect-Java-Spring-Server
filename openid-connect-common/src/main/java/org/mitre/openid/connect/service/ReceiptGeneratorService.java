/**
 * 
 */
package org.mitre.openid.connect.service;

import org.mitre.openid.connect.model.UserInfo;

/**
 * @author sarahdavies
 *
 */
public interface ReceiptGeneratorService {

	void generateConsentReceipt(UserInfo crUserInfo);

}
