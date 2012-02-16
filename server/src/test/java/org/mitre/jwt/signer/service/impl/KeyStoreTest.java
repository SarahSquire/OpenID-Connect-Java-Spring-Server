package org.mitre.jwt.signer.service.impl;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:test-context.xml" })
public class KeyStoreTest {

	@Autowired
	@Qualifier("testKeystore")
	KeyStore keystore;

	static {
		// Needed to create the certificate
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Creates a certificate.
	 * 
	 * @param commonName
	 * @param daysNotValidBefore
	 * @param daysNotValidAfter
	 * @return
	 */
	private static X509V3CertificateGenerator createCertificate(
			String commonName, int daysNotValidBefore, int daysNotValidAfter) {
		// BC sez X509V3CertificateGenerator is deprecated and the docs say to
		// use another, but it seemingly isn't included jar...
		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();

		v3CertGen
				.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		v3CertGen.setIssuerDN(new X509Principal("CN=" + commonName
				+ ", OU=None, O=None L=None, C=None"));
		v3CertGen.setNotBefore(new Date(System.currentTimeMillis()
				- (1000L * 60 * 60 * 24 * daysNotValidBefore)));
		v3CertGen.setNotAfter(new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24 * daysNotValidAfter)));
		v3CertGen.setSubjectDN(new X509Principal("CN=" + commonName
				+ ", OU=None, O=None L=None, C=None"));
		return v3CertGen;
	}

	/**
	 * Create an RSA KeyPair and insert into specified KeyStore
	 * 
	 * @param location
	 * @param domainName
	 * @param alias
	 * @param keystorePassword
	 * @param aliasPassword
	 * @param daysNotValidBefore
	 * @param daysNotValidAfter
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static java.security.KeyStore generateRsaKeyPair(KeyStore keystore,
			String domainName, String alias, String aliasPassword, int daysNotValidBefore, int daysNotValidAfter)
			throws GeneralSecurityException, IOException {

		java.security.KeyStore ks = keystore.getKeystore();

		KeyPairGenerator rsaKeyPairGenerator = null;

		rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");

		rsaKeyPairGenerator.initialize(2048);
		KeyPair rsaKeyPair = rsaKeyPairGenerator.generateKeyPair();

		// BC sez X509V3CertificateGenerator is deprecated and the docs say to
		// use another, but it seemingly isn't included jar...
		X509V3CertificateGenerator v3CertGen = createCertificate(domainName,
				daysNotValidBefore, daysNotValidAfter);

		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();

		v3CertGen.setPublicKey(rsaKeyPair.getPublic());
		v3CertGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		// BC docs say to use another, but it seemingly isn't included...
		X509Certificate certificate = v3CertGen
				.generateX509Certificate(rsaPrivateKey);

		// if exist, overwrite
		ks.setKeyEntry(alias, rsaPrivateKey, aliasPassword.toCharArray(),
				new java.security.cert.Certificate[] { certificate });

		keystore.setKeystore(ks);
		
		return ks;
	}
	

	@Test
	public void storeKeyPair() throws GeneralSecurityException, IOException {

		java.security.KeyStore ks  = null;
			
		try {
			ks = KeyStoreTest.generateRsaKeyPair(keystore, "OpenID Connect Server", "storeKeyPair", "changeit", 30, 365);

		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertThat(ks, not(nullValue()));
	}
	
	@Test
	public void readKey() throws GeneralSecurityException {
		
		Key key = keystore.getKeystore().getKey("storeKeyPair",
				KeyStore.PASSWORD.toCharArray());
		
		assertThat(key, not(nullValue()));
	}
}
