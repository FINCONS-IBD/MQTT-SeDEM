import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.KeyAgreement;
import java.security.spec.ECPublicKeySpec;

public class EccJava {

    public static void main(String[] args) throws Exception {        
        ECParameterSpec P256 = new ECParameterSpec(
                new EllipticCurve(
                    // field the finite field that this elliptic curve is over.
                    new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")),
                    // a the first coefficient of this elliptic curve.
                    new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
                    // b the second coefficient of this elliptic curve.
                    new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")
                ),
                //g the generator which is also known as the base point.
                new ECPoint(
                    // gx
                    new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
                    // gy
                    new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")
                ),
                // Order n
                new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"),
                1);     
        ECParameterSpec ecParameterSpec = P256;
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPoint point = new ECPoint(new BigInteger("42018758423175437168159151230828022456501940238043507722365166549704764006670"), new BigInteger("60175430641159831899278191731045016723882032138466519172283364761886481667223"));
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, ecParameterSpec);
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
        getAgreedKey(2417,keyFactory, publicKey, ecParameterSpec);
    } 
    private static byte[] getAgreedKey(int counter, KeyFactory keyFactory, PublicKey publicKey,  ECParameterSpec ecParameterSpec) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(""+counter), ecParameterSpec);
        PrivateKey recoveredPrivateKey = keyFactory.generatePrivate(privateKeySpec);
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(recoveredPrivateKey);
        keyAgreement.doPhase(publicKey, true);
 
        return keyAgreement.generateSecret();
    }
}