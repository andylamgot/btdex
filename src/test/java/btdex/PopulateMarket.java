package btdex;

import bt.BT;
import bt.Contract;
import btdex.core.Globals;
import btdex.core.Market;
import btdex.sc.SellContract;
import burst.kit.entity.BurstID;
import burst.kit.entity.BurstValue;
import burst.kit.entity.response.AT;

/**
 * Add some orders to a market.
 * 
 * @author jjos
 */
public class PopulateMarket extends BT {

	public static void main(String[] args) throws Exception {
		
		BT.setNodeAddress(BT.NODE_TESTNET);

		long amount = 1000 * Contract.ONE_BURST;
		long rate = 70;
		long security = 100 * Contract.ONE_BURST;

		BurstID feeContract = BT.getBurstAddressFromPassphrase(BT.PASSPHRASE).getBurstID();
		BurstID arbitrator1 = Globals.MEDIATORS[0];
		BurstID arbitrator2 = Globals.MEDIATORS[1];
		long offerType = Market.MARKET_BTC;
		long accountHash = 1234;

		long data[] = {
				feeContract.getSignedLongId(),
				arbitrator1.getSignedLongId(), arbitrator2.getSignedLongId(),
				offerType, accountHash
		};

		bt.compiler.Compiler compiled = BT.compileContract(SellContract.class);

		for (int i = 0; i < 4; i++) {
			String name = SellContract.class.getSimpleName() + System.currentTimeMillis();

			BT.registerContract(BT.PASSPHRASE2, compiled.getCode(), compiled.getDataPages(), name, name, data,
					BurstValue.fromPlanck(SellContract.ACTIVATION_FEE), BT.getMinRegisteringFee(compiled), 1000).blockingGet();
			BT.forgeBlock();

			AT contract = BT.findContract(BT.getBurstAddressFromPassphrase(BT.PASSPHRASE2), name);
			System.out.println(contract.getId().getID());

			// Initialize the offer
			BT.callMethod(BT.PASSPHRASE2, contract.getId(), compiled.getMethod("update"),
					BurstValue.fromPlanck(amount + security + SellContract.ACTIVATION_FEE), BurstValue.fromBurst(0.1), 100,
					rate, security).blockingGet();
			BT.forgeBlock();
			BT.forgeBlock();

			rate *= 2;
			amount *= 2;
			security *= 2;
		}
	}
}
