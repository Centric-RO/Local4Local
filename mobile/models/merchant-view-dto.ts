export default class MerchantDto {
	public companyName: string;
	public kvk: string;
	public category: string;
	public latitude: number;
	public longitude: number;
	public address: string;
	public website?: string;
	public id?: string;

	constructor(companyName: string, kvk: string, category: string, latitude: number, longitude: number, address: string, website?: string, id?: string) {
		this.companyName = companyName;
		this.kvk = kvk;
		this.category = category;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.website = website;
		this.id = id;
	}

}