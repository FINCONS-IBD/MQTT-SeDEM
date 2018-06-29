import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;

public class ThrowIllegalStateExc {

	public static void main(String[] args) {
	
		/*bad keys 1
			X: 88283837461245610623190256264561355286668986756121440861644755603098846747952
			Y: 115581995790541190221359939186291273542047137588435488224358468719242216741185
		*/
		String publicKeyEncoded = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEwy7hmSzninx3B744HxqociqMnMxiNRzZSTMNshGh3TAAiRdg8zUvmf787L3c85ip36GzJqKJsg603tvX3ZAZQQ";
		String privateKeyEncoded = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDwh63t9S4EX1TOAIWtpFWQv8mnaJfb-qIQcCbSNLjU8g";
		
		/*bad keys 2
		 	X: 92043945630126860893723512320463349811293349246291846783602009262125433991829
			Y: 115766198249863333935708674308088117073770685973268124330282964976415348571672
		 */
		//String publicKeyEncoded = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEy38G7KXmtLnnk2IrxQAVRKmFLuSRV2w1hwsNl3LjspUA8Vih5qMBLvbLyYh2Fag-SA-RrAbOZOMlADh7X-M-GA";
		//String privateKeyEncoded = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCB7tgPfZts-2-wMChlKr154GWdRQeZgerd_YV2rvrE3Wg";
		
		//good keys1
		//String publicKeyEncoded = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErwkHWevGGPBWv9S8c4KOaaSwv6Gj7HnjGPRy9KvXRgnSuFVuHeY8ZyU7OqolkkRTyNpdKKo8APyS0vBbKWk_8g";
		//String privateKeyEncoded = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDxgXifCE4dg4lobUwIrCVYn10twp8_ZeHdAi4flX0kHw";
		
		//good keys2
		//String publicKeyEncoded = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEAlrNvsy36-AcWHcuh25c40fPORHimzwlnkaid0j5Voh-FeU2ToJhp3IPYxhr7G4aEFxlxLztY8NOuYg_u-kBxQ";
		//String privateKeyEncoded = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCC-f3_9p9fxdpB4SNh1gM7oFEdGD3O5fFmUytORDFXZzw";
				
		KeyAgreement ka;
		try {
			ka = KeyAgreement.getInstance("ECDH");
			byte[] privateKeyBytes = Base64.getUrlDecoder().decode(privateKeyEncoded);
			
			KeyFactory kf = KeyFactory.getInstance("EC"); 
			
			PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

			ka.init(privateKey);
			
			byte [] publicKeyBytes = Base64.getUrlDecoder().decode(publicKeyEncoded);

		    EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

			PublicKey publicKey = kf.generatePublic(publicKeySpec);
			ka.doPhase(publicKey, true);
			ka.generateSecret();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}
