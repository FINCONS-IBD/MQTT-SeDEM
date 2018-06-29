import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;

import net.minidev.json.JSONObject;

public class ECDHIllegalStateNew {

	private static final String key_type="EC";
	private static final String curve_parameters = "secp256r1";
	private static boolean error = false;

	public static void main(String[] args) {
		setupSymmetricKey();
	}
	
	
	
	public static void setupSymmetricKey(){
		
		// Generate the key pair
		KeyPairGenerator gen;
		
		PublicKey public_key = null;
		PrivateKey priv_key = null;

		try {
			gen = KeyPairGenerator.getInstance("EC");
			gen.initialize(Curve.P_256.toECParameterSpec()); // Set curve params
			KeyPair keyPair = gen.generateKeyPair();

			// Convert to JWK format
			JWK jwk = new ECKey.Builder(Curve.P_256, (ECPublicKey)keyPair.getPublic()).build();

			KeyAgreement ka = KeyAgreement.getInstance("ECDH");
			priv_key = keyPair.getPrivate();
			System.out.println("private_key: "  + Base64.getUrlEncoder().withoutPadding().encodeToString(priv_key.getEncoded()));
			ka.init(priv_key);
			
			JSONObject jwk_obj = jwk.toJSONObject();
			
			String x_param =jwk_obj.getAsString("x");
			String y_param =jwk_obj.getAsString("y");
			
			public_key = getForeignPublicKey(x_param, y_param);

			System.out.println("public_key: " + Base64.getUrlEncoder().withoutPadding().encodeToString(public_key.getEncoded()));
			ka.doPhase(public_key, true);
			
			ka.generateSecret();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
