package btdex.markets;

import java.util.HashMap;

public class MarketLTC extends MarketCrypto {
	
	static final String ADDRESS = "Address";
	static final String REGEX = "^L[a-km-zA-HJ-NP-Z1-9]{26,33}$";

	public String toString() {
		return "LTC";
	}
	
	@Override
	public long getID() {
		return MARKET_LTC;
	}
	
	@Override
	public void validate(HashMap<String, String> fields) throws Exception {
		super.validate(fields);
		
		String addr = fields.get(ADDRESS);
		
		if(!addr.matches(REGEX))
			throw new Exception(addr + " is not a valid LTC address");
	}
}
