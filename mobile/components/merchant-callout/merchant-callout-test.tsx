import MerchantDto from "@/models/merchant-view-dto";
import { act, fireEvent, render } from "@testing-library/react-native";
import MerchantCallout from "@/components/merchant-callout/merchant-callout";
import { Platform } from "react-native";

const mockedMerchant: MerchantDto = {
	companyName: 'companyName',
	address: 'address',
	category: 'category',
	kvk: '12345678',
	latitude: 45,
	longitude: 40,
	website: 'www.example.com'
};
jest.mock('react-i18next', () => {
	return {
		useTranslation: () => ({
			t: jest.fn((key) => key),
		})
	};
});

const mockOnClose = jest.fn();

jest.mock('react-native/Libraries/Utilities/Platform', () => {
	const select = jest.fn().mockImplementation((obj) => {
		const value = obj[platform.OS]
		return !value ? obj.default : value
	})

	let platform = {
		OS: 'ios',
		select
	}

	return platform
});

describe('<MerchantCallout />', () => {
	it('should render', () => {
		const { getByText } = render(<MerchantCallout merchant={mockedMerchant} onClose={mockOnClose} />);

		expect(getByText(mockedMerchant.companyName)).toBeTruthy();
		expect(getByText(mockedMerchant.category)).toBeTruthy();
		expect(getByText(mockedMerchant.address)).toBeTruthy();
		expect(getByText(mockedMerchant.website as string)).toBeTruthy();
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	describe('Android behavior', () => {
		beforeEach(() => {
			Platform.OS = 'android'
		});

		it('should close the callout when pressing it', async () => {
			const { getByTestId } = render(<MerchantCallout merchant={mockedMerchant} onClose={mockOnClose} />);

			await act(() => {
				fireEvent(getByTestId('callout'), 'press');
			});

			expect(mockOnClose).toHaveBeenCalled()
		});
	});

	describe('iOS behavior', () => {
		beforeEach(() => {
			Platform.OS = 'ios'
		});

		it('should NOT close the callout when pressing it', async () => {
			const { getByTestId } = render(<MerchantCallout merchant={mockedMerchant} onClose={mockOnClose} />);

			await act(() => {
				fireEvent(getByTestId('callout'), 'press');
			});

			expect(mockOnClose).not.toHaveBeenCalled()
		});
	});
});