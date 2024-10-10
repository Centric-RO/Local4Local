import { act, fireEvent, render, waitFor } from '@testing-library/react-native';
import Index from '@/app/index';
import MerchantDto from "@/models/merchant-view-dto";
import ChipData from "@/models/chip-data";
import CategoryService from "@/services/category-service/category-service";
import MerchantService from "@/services/merchant-service/merchant-service";

jest.mock("@/services/category-service/category-service", () => ({
	getCategories: jest.fn()
}));

jest.mock("@/services/merchant-service/merchant-service", () => ({
	getMerchantsByCategoryId: jest.fn()
}));

jest.mock('react-i18next', () => {
	return {
		useTranslation: () => ({
			t: jest.fn((key) => key),
		})
	};
});

describe('<Index />', () => {
	const mockCategories: ChipData[] = [
		{id: 0, label: 'category1'},
		{id: 1, label: 'category2'},
	];

	const mockMerchants: MerchantDto[] = [{
		id: '1',
		companyName: 'company',
		category: 'category',
		kvk: '123',
		latitude: 45,
		longitude: 40,
		address: 'address'
	}];

	beforeEach(() => {
		jest.clearAllMocks();

		(CategoryService.getCategories as jest.Mock).mockResolvedValue(mockCategories);
		(MerchantService.getMerchantsByCategoryId as jest.Mock).mockResolvedValue(mockMerchants);
	});

	it('renders successfully and fetches the required data', async () => {
		const {getByText} = render(<Index />);

		expect(CategoryService.getCategories).toHaveBeenCalledTimes(1);
		expect(MerchantService.getMerchantsByCategoryId).toHaveBeenCalledWith(-1);

		await waitFor(() => {
			expect(getByText('category1')).toBeTruthy();
			expect(getByText('category2')).toBeTruthy();
		});
	});

	it('renders successfully and logs an error if the data cannot be fetched', async () => {
		jest.spyOn(console, 'log').mockImplementation(jest.fn());

		(CategoryService.getCategories as jest.Mock).mockImplementation(() => {
			throw new Error('categories error');
		});
		(MerchantService.getMerchantsByCategoryId as jest.Mock).mockImplementation(() => {
			throw new Error('merchants error');
		});

		render(<Index />);

		expect(CategoryService.getCategories).toHaveBeenCalledTimes(1);
		expect(MerchantService.getMerchantsByCategoryId).toHaveBeenCalledWith(-1);

		await waitFor(() => {
			expect(console.log).toHaveBeenCalledTimes(2);
		});
	});

	it('should change the selected category when a chip is pressed', async () => {
		const {getByText} = render(<Index />);

		await waitFor(() => {
			act(() => {
				fireEvent(getByText('category1'), 'press');
			});

			expect(MerchantService.getMerchantsByCategoryId).toHaveBeenCalledWith(0);
		});
	});
});
