export class RejectMerchantDto  {
    public merchantId: string;
	public reason: string;

	constructor(merchantId: string, reason: string){
		this.merchantId = merchantId;
		this.reason = reason;
	}
}