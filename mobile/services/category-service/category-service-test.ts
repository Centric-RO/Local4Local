import ChipData from "@/models/chip-data";
import CategoryService from "@/services/category-service/category-service";
import { API_PATH } from "@/constants/api";

global.fetch = jest.fn();

const mockedCategories: ChipData[] = [
	{
		id: 0,
		label: 'category-0'
	},
	{
		id: 1,
		label: 'category-1'
	}
];

describe('CategoryService', () => {
	describe('getCategories', () => {
		it('should fetch the categories', async () => {
			const mockedResponse = {
				ok: true,
				json: jest.fn().mockResolvedValue(mockedCategories)
			};

			(global.fetch as jest.Mock).mockResolvedValue(mockedResponse);

			const categories = await CategoryService.getCategories();

			expect(mockedResponse.json).toHaveBeenCalled();
			expect(global.fetch).toHaveBeenCalledWith(`${API_PATH}/category/all`);
			expect(categories).toEqual(mockedCategories);
		});

		it('should throw error if !response.ok', async () => {
			const mockedResponse = {
				ok: false,
				status: 404
			};

			(global.fetch as jest.Mock).mockResolvedValue(mockedResponse);

			await expect(async () => {
				await CategoryService.getCategories();
			}).rejects.toThrow();

			expect(global.fetch).toHaveBeenCalledWith(`${API_PATH}/category/all`);
		});
	});
});