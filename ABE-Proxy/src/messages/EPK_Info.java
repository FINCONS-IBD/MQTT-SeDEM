package messages;

import com.google.gson.annotations.SerializedName;
import com.nimbusds.jose.jwk.JWK;

import net.minidev.json.JSONObject;

/*
 * Based on RFC 7518 - JSON Web Algorithms (JWA)
 * @author Salvador Pérez
 * @version 04/11/2016
 */

public class EPK_Info {
	private String alg;
	private String enc;
	private String apu;
	private String apv;
	@SerializedName("epk")
	private JSONObject jwk;
	
	public EPK_Info(String alg, String enc, String apu, String apv, JWK jwk){
		this.alg = alg;
		this.enc = enc;
		this.apu = apu;
		this.apv = apv;
		this.jwk = jwk.toJSONObject();
	}
	
	public String getAlg() {
		return alg;
	}
	public String getEnc() {
		return enc;
	}
	public String getApu() {
		return apu;
	}
	public String getApv() {
		return apv;
	}
	public JSONObject getJwk() {
		return jwk;
	}
	
}
