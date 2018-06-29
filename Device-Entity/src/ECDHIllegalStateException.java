import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;

public class ECDHIllegalStateException {


	private static final String key_type="EC";
	private static final String curve_parameters = "secp256r1";

	private static boolean error = false;
	
	public static void main(String[] args) {
		int counter = 1;
		while(!error){
			try {
				Thread.sleep(500);
				setupSymmetricKey();
				counter++;
				System.out.println("--- "+counter+" ---");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("number of cycles: " + counter);
		
	}
	
	public static void setupSymmetricKey(){
		KeyPair kp;
		PublicKey public_key = null;
		PrivateKey priv_key = null;
		try{
			ECGenParameterSpec gps = new ECGenParameterSpec(curve_parameters);
		    KeyPairGenerator kpg = KeyPairGenerator.getInstance(key_type);
		    kpg.initialize(gps);
		    kp = kpg.generateKeyPair();
		    
			KeyAgreement ka = KeyAgreement.getInstance("ECDH");
			priv_key = kp.getPrivate();
			System.out.println("private_key: "  + Base64.getUrlEncoder().withoutPadding().encodeToString(priv_key.getEncoded()));
			
			ka.init(priv_key);
		
			String x_param ="";
			String y_param ="";
			
			ECPublicKey pub_key_x  = (ECPublicKey)kp.getPublic();
			BigInteger x= pub_key_x.getW().getAffineX();
			System.out.println("X: " + x);
			byte[] array_x = x.toByteArray();

			x_param = Base64.getUrlEncoder().withoutPadding().encodeToString(array_x);
					
			ECPublicKey pub_key_y  = (ECPublicKey)kp.getPublic();
			BigInteger y= pub_key_y.getW().getAffineY();
			System.out.println("Y: " + y);
			byte[] array_y = y.toByteArray();

			y_param = Base64.getUrlEncoder().withoutPadding().encodeToString(array_y);
			
			public_key = getForeignPublicKey(x_param, y_param);
			System.out.println("public_key: "  + Base64.getUrlEncoder().withoutPadding().encodeToString(public_key.getEncoded()));
			ka.doPhase(public_key, true);
			
			ka.generateSecret();
		}catch(Exception e){
			e.printStackTrace();
			error = true;
		}	
	}
	
	private static PublicKey getForeignPublicKey(String x_param, String y_param) {
		PublicKey public_key = null;
		try{
			byte[] x_byte = Base64.getUrlDecoder().decode(x_param);
			byte[] y_byte = Base64.getUrlDecoder().decode(y_param);
			ECPoint pubPoint = new ECPoint(new BigInteger(x_byte),new BigInteger(y_byte));
			AlgorithmParameters parameters = AlgorithmParameters.getInstance(key_type);
			parameters.init(new ECGenParameterSpec(curve_parameters));
			ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
			ECPublicKeySpec pubSpec = new ECPublicKeySpec(pubPoint, ecParameters);
			KeyFactory kf = KeyFactory.getInstance(key_type);
			public_key = kf.generatePublic(pubSpec);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return public_key;

	}

}
