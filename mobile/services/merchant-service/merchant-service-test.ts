import MerchantDto from "@/models/merchant-view-dto";
import MerchantService from "@/services/merchant-service/merchant-service";
import { API_PATH } from "@/constants/api";

global.fetch = jest.fn();

describe('MerchantService', () => {
	const mockedMerchants: MerchantDto[] = [
		{
			id: '1',
			companyName: 'company1',
			category: 'category1',
			kvk: '123',
			latitude: 45,
			longitude: 40,
			address: 'address1'
		},
		{
			id: '2',
			companyName: 'company2',
			category: 'category2',
			kvk: '1234',
			latitude: 40,
			longitude: 45,
			address: 'address2'
		}
	];

	describe('getMerchantsByCategoryId', () => {
		it('should fetch the merchants', async () => {
			const mockedResponse = {
				ok: true,
				json: jest.fn().mockResolvedValue(mockedMerchants)
			};

			(global.fetch as jest.Mock).mockResolvedValue(mockedResponse);

			const merchants = await MerchantService.getMerchantsByCategoryId(0);

			expect(mockedResponse.json).toHaveBeenCalled();
			expect(global.fetch).toHaveBeenCalledWith(`${API_PATH}/merchant/filter/0`);
			expect(merchants).toEqual(mockedMerchants);
		});

		it('should throw error if !response.ok', async () => {
			const mockedResponse = {
				ok: false,
				status: 404
			};

			(global.fetch as jest.Mock).mockResolvedValue(mockedResponse);

			await expect(async () => {
				await MerchantService.getMerchantsByCategoryId(0);
			}).rejects.toThrow();

			expect(global.fetch).toHaveBeenCalledWith(`${API_PATH}/merchant/filter/0`);
		});
	});
});