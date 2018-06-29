import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;

public class TryNimbusLib {
	public static void main(String[] args) {
	
		// Generate the key pair
		KeyPairGenerator gen;
		try {
			gen = KeyPairGenerator.getInstance("EC");
			gen.initialize(Curve.P_256.toECParameterSpec()); // Set curve params
			KeyPair keyPair = gen.generateKeyPair();

			// Convert to JWK format
			JWK jwk = new ECKey.Builder(Curve.P_256, (ECPublicKey)keyPair.getPublic()).build();
			
			// Output
			System.out.println(jwk);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
